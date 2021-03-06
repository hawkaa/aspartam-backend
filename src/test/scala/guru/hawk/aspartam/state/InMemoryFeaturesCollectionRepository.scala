/*
 * Copyright(c) 2018 Schibsted Media Group. All rights reserved.
 */
package guru.hawk.aspartam.state

import play.api.libs.json.JsValue

class InMemoryFeaturesCollectionRepository extends FeaturesCollectionRepository {

  private [this] var polygons: Option[JsValue] = None

  override def get(): Option[JsValue] = polygons

  override def put(p: JsValue): Unit = polygons = Some(p)

  override def reset(): Unit = { /* not implemented */ }
}
