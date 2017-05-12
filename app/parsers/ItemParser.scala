package parsers
import org.luaj.vm2.LuaTable

import scala.util.Try

object ItemParser extends FactorioParser[Item] {
  override def transport(table: LuaTable): Option[Item] = Item.fromTable(table)
}

case class Item(
    typ: String,
    name: String
)

object Item {
  def fromTable(table: LuaTable): Option[Item] = Try {
    Item(
      table.get("type").checkjstring(),
      table.get("name").checkjstring()
    )
  }.toOption
}
