package parsers

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import javax.script.ScriptEngineManager

import org.luaj.vm2.{LuaTable, LuaValue}

import scala.collection.breakOut

trait FactorioParser[T] {
  import FactorioParser._

  def parse(path: String): Seq[T] = {
    val lua = readAll("data.lua") + readAll(path)
    val engine = manager.getEngineByName("luaj")
    engine.eval(lua)
    val array: LuaTable = engine.get("array").asInstanceOf[LuaTable]
    tableToSeq(array)(_.checktable()).flatMap(transport)
  }

  def transport(table: LuaTable): Option[T]
}

object FactorioParser {
  private val manager = new ScriptEngineManager()

  def readAll(path: String): String =
    new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8)

  def tableToSeq[T](table: LuaTable)(f: LuaValue => T): Seq[T] = {
    table.keys().map(table.get).map(f)(breakOut)
  }

  def tableToMap[K, V](table: LuaTable)(f: LuaValue => K)(g: LuaValue => V): Map[K, V] = {
    table.keys().map { key =>
      f(key) -> g(table.get(key))
    }(breakOut)
  }
}
