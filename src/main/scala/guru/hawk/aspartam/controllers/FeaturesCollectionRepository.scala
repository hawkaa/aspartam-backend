/*
 * Copyright(c) 2018 Schibsted Media Group. All rights reserved.
 */
package guru.hawk.aspartam.controllers

import com.google.inject.ImplementedBy
import play.api.libs.json.JsValue

@ImplementedBy(classOf[DynamoDbFeaturesCollectionRepository])
trait FeaturesCollectionRepository {
  def get(): Option[JsValue]
  def put(polygons: JsValue): Unit
  def reset(): Unit
}
