package parsers
import models.ItemDetail
import org.luaj.vm2.LuaTable

import scala.util.Try

object ItemParser extends FactorioParser[Item] {
  override def transport(table: LuaTable): Option[Item] = Item.fromTable(table)
}

case class Item(
    typ: String,
    name: String,
    subgroup: String,
    order: String,
    stackSize: Int
) {
  def detail(id: Long) = ItemDetail(id, subgroup, order, stackSize)
}

object Item {
  def fromTable(table: LuaTable): Option[Item] = Try {
    Item(
      table.get("type").checkjstring(),
      table.get("name").checkjstring(),
      table.get("subgroup").checkjstring(),
      table.get("order").checkjstring(),
      table.get("stack_size").checkint()
    )
  }.toOption
}
