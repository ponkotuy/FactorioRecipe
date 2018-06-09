package controllers

import javax.inject.Inject
import play.api.mvc.InjectedController

class MyAssets @Inject()(assets: Assets) extends InjectedController {
  def at(path: String, file: String, aggressiveCaching: Boolean = false) = {
    if (file.endsWith("/")) assets.at(path, file + "index.html")
    else assets.at(path, file, aggressiveCaching)
  }
}
