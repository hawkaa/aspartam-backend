/*
 * Copyright(c) 2018 Schibsted Media Group. All rights reserved.
 */
package guru.hawk.aspartam.controllers

import play.api.libs.json.JsValue

class DynamoDbPolygonRepository extends PolygonRepository {

  private [this] var polygons: Option[JsValue] = None

  override def get(): Option[JsValue] = polygons

  override def put(p: JsValue): Unit = polygons = Some(p)

}
