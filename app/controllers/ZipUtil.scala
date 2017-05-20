package controllers

import java.io.{File, InputStream}
import java.util.zip.ZipFile

import scala.collection.JavaConverters._

object ZipUtil {
  /**
    * zipファイルから、中身のファイルのinputStreamのIteratorを返す
    */
  def inputStreams(file: File): Iterator[InputStream] = {
    val zip = new ZipFile(file)
    zip.entries().asScala.filterNot(_.isDirectory).map(zip.getInputStream)
  }
}
