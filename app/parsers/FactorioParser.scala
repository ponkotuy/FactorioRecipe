package parsers

import java.io.{InputStream, InputStreamReader}
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}
import javax.script.ScriptEngineManager

import com.google.common.base.Charsets
import com.google.common.io.CharStreams
import org.luaj.vm2.{LuaTable, LuaValue}

import scala.collection.breakOut
import scala.io.Source

trait FactorioParser[T] {
  import FactorioParser._

  def parse(path: String): Seq[T] = commonParse(readAll(path))
  def parse(path: Path): Seq[T] = commonParse(readAll(path))
  def parse(is: InputStream): Seq[T] = {
    val str = CharStreams.toString(new InputStreamReader(is, Charsets.UTF_8))
    commonParse(str)
  }

  def transport(table: LuaTable): Option[T]

  private[this] def commonParse(target: String): Seq[T] = {
    val dataLua = Source.fromURL(getClass.getResource("/data.lua")).mkString
    val lua = dataLua + target
    val engine = manager.getEngineByName("luaj")
    engine.eval(lua)
    val array: LuaTable = engine.get("array").asInstanceOf[LuaTable]
    tableToSeq(array)(_.checktable()).flatMap(transport)
  }
}

object FactorioParser {
  private val manager = new ScriptEngineManager()

  def readAll(path: String): String = readAll(Paths.get(path))

  def readAll(path: Path): String =
    new String(Files.readAllBytes(path), StandardCharsets.UTF_8)

  def tableToSeq[T](table: LuaTable)(f: LuaValue => T): Seq[T] = {
    table.keys().map(table.get).map(f)(breakOut)
  }

  def tableToMap[K, V](table: LuaTable)(f: LuaValue => K)(g: LuaValue => V): Map[K, V] = {
    table.keys().map { key =>
      f(key) -> g(table.get(key))
    }(breakOut)
  }


}
