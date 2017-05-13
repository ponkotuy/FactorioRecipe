package models

import scalikejdbc._
import skinny.orm.{Alias, SkinnyCRUDMapperWithId}

case class Item(id: Long, name: String)

object Item extends SkinnyCRUDMapperWithId[Long, Item] {
  override val defaultAlias: Alias[Item] = createAlias("i")

  override def extract(rs: WrappedResultSet, n: ResultName[Item]): Item = autoConstruct(rs, n)

  override def idToRawValue(id: Long): Any = id
  override def rawValueToId(value: Any): Long = value.toString.toLong

  def create(name: String)(implicit session: DBSession): Long =
    createWithAttributes('name -> name)
  def create(i: Item)(implicit session: DBSession): Long = create(i.name)
}
