package guru.hawk.aspartam.controllers

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.libs.json.{JsValue, Json}
import play.api.test._
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class AspartamControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "AspartamControllerSpec polygons GET" should {

    def getController(json: Option[JsValue]) = {
      val repo = new InMemoryFeaturesCollectionRepository()
      json.foreach(repo.put(_))
      new AspartamController(stubControllerComponents(), app.materializer, repo)
    }

    "return json" in {
      val controller = getController(Some(Json.obj("foo" -> "bar")))
      val response = controller.get().apply(FakeRequest(GET, "/"))
      contentType(response) mustBe Some("application/json")
      contentAsString(response) mustBe """{"foo":"bar"}"""
    }

    "return 404 if nothing set" in {
      val controller = getController(None)
      val response = controller.get().apply(FakeRequest(GET, "/"))
      status(response) mustBe (404)
    }

  }

  "AspartamControllerSpec polygons POST" should {


    "add json to the repo" in {
      implicit val mat = app.materializer
      val repo = new InMemoryFeaturesCollectionRepository()
      val controller = new AspartamController(stubControllerComponents(), mat, repo)
      val response = controller.post()
        .apply(FakeRequest(POST, "/").withJsonBody(Json.obj("foo" -> "bar")))

      status(response) mustBe (201)
      repo.get() mustBe Some(Json.obj("foo" -> "bar"))
    }

    "return 400 if invalid json" in {
      implicit val mat = app.materializer
      val repo = new InMemoryFeaturesCollectionRepository()
      val controller = new AspartamController(stubControllerComponents(), mat, repo)
      val response = controller.post()
        .apply(FakeRequest(POST, "/").withTextBody("foobar"))

      status(response) mustBe (400)
      repo.get() mustBe None
    }
  }
}
