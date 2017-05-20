package models

import scalikejdbc._
import skinny.orm.{Alias, SkinnyCRUDMapperWithId}

case class ItemDetail(id: Long, subgroup: String, order: String, stackSize: Int)

object ItemDetail extends SkinnyCRUDMapperWithId[Long, ItemDetail] {
  override def defaultAlias: Alias[ItemDetail] = createAlias("id")
  override val tableName = "item_details"

  override def extract(rs: WrappedResultSet, n: ResultName[ItemDetail]): ItemDetail = autoConstruct(rs, n)

  override def idToRawValue(id: Long): Any = id
  override def rawValueToId(value: Any): Long = value.toString.toLong

  def create(i: ItemDetail)(implicit session: DBSession): Unit =
    createWithNamedValues(
      sqls"id" -> i.id,
      sqls"subgroup" -> i.subgroup,
      sqls""""order"""" -> i.order,
      sqls"stack_size" -> i.stackSize
    )
//    createWithAttributes('id -> i.id, 'subgroup -> i.subgroup, 'order -> i.order, 'stackSize -> i.stackSize)
}
