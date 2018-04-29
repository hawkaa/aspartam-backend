package guru.hawk.aspartam.controllers

import akka.stream.Materializer
import guru.hawk.aspartam.state.FeaturesCollectionRepository
import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._

class AspartamController @Inject()(cc: ControllerComponents, mat: Materializer, repository: FeaturesCollectionRepository)
  extends AbstractController(cc) {

  def get() = Action {
    repository
      .get()
      .map(Ok(_))
      .getOrElse(NotFound)
  }

  def post() = Action { implicit request: Request[AnyContent] =>
    request
      .body
      .asJson
      .map { jsValue =>
         repository.put(jsValue)
         Created
      }
      .getOrElse(BadRequest)
  }

  def reset() = Action {
    repository.reset()
    Ok
  }
}
