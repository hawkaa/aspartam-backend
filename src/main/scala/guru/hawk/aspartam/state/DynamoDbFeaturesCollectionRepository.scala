/*
 * Copyright(c) 2018 Schibsted Media Group. All rights reserved.
 */
package guru.hawk.aspartam.state

import java.util.UUID

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.model._
import javax.inject.Inject
import play.api.Configuration
import play.api.libs.json.{JsArray, JsValue, Json}

import scala.io.Source
import scala.util.control.NonFatal
import scala.collection.JavaConverters._

/*
 * The following class will store one FeaturesCollection to DynamoDB, and have the ability to fetch that
 * collection later.
 *
 * Although the repository acts on an entire collection at a time, the features are actually stored separately as
 * separate items. On a `put` operation, all features are wiped, and new features are inserted. Good design choice
 * or not? Time will tell. The idea was that we might require feature-granular access to the repository some time
 * in the future.
 *
 * And yes, did I mention that separate items are indeed easier to manipulate from other interfaces compared to the
 * entire GeoJSON string stored as one item.
 *
 * TODO: Add tests for this thing
 */
class DynamoDbFeaturesCollectionRepository @Inject()(config: Configuration) extends FeaturesCollectionRepository {

  val TableName = "features"
  val IdField = "id"
  val FeatureField = "feature"

  val client = AmazonDynamoDBClientBuilder.standard()
    .withEndpointConfiguration(
      new EndpointConfiguration(config.get[String]("dynamodb.endpoint"),  config.get[String]("dynamodb.region"))
    )
    .build()


  override def get(): Option[JsValue] = {
    val features = client
      .scan(TableName, List(FeatureField).asJava)
      .getItems
      .asScala
      .map { item =>
        Json.parse(item.get(FeatureField).getS)
      }
    Some(Json.obj(
      "type" -> "FeatureCollection",
      "features" -> features
    ))
  }

  override def put(p: JsValue): Unit = {
    deleteAllItems()
    addFeaturesFromGeoJSON(p)
  }

  override def reset(): Unit = {
    /* delete table if exist */
    try {
      client.deleteTable(TableName)
    } catch { case NonFatal(_) => }

    /* create table */
    val createTableRequest = new CreateTableRequest()
      .withTableName(TableName)
      .withAttributeDefinitions(
        new AttributeDefinition(IdField, ScalarAttributeType.S))
      .withKeySchema(
        new KeySchemaElement(IdField, KeyType.HASH)
      )
      .withProvisionedThroughput(
        new ProvisionedThroughput(config.get[Long]("dynamodb.readCapacity"), config.get[Long]("dynamodb.writeCapacity"))
      )
    client.createTable(createTableRequest)

    /* load the sample GeoJSON */
    deleteAllItems()
    addFeaturesFromGeoJSON(Json.parse(Source.fromResource("polygons.json").mkString))
  }

  private[this] def deleteAllItems(): Unit = {
    client
      .scan(TableName, List(IdField).asJava)
      .getItems
      .asScala
      .map { item =>
        val id = item.get(IdField).getS
        client.deleteItem(TableName, Map(IdField -> new AttributeValue(id)).asJava)
      }
  }

  private[this] def addFeaturesFromGeoJSON(json: JsValue): Unit = {
    (json \ "features")
      .as[JsArray]
      .value
      .map { feature =>
        client.putItem(TableName, Map(
          IdField -> new AttributeValue(UUID.randomUUID().toString),
          /* we're lazy, we're storing the actual feature as a string instead of proper DynamoDB attributes */
          FeatureField -> new AttributeValue(feature.toString())
        ).asJava)
      }
  }
}
