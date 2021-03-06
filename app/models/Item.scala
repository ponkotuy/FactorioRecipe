package models

import scalikejdbc._
import skinny.orm.{Alias, SkinnyCRUDMapperWithId}

case class Item(id: Long, name: String, detail: Option[ItemDetail] = None)

object Item extends SkinnyCRUDMapperWithId[Long, Item] {
  import Aliases.{i, id}
  override val defaultAlias: Alias[Item] = createAlias("i")

  override def extract(rs: WrappedResultSet, n: ResultName[Item]): Item = autoConstruct(rs, n, "detail")

  override def idToRawValue(id: Long): Any = id
  override def rawValueToId(value: Any): Long = value.toString.toLong

  belongsToWithFkAndJoinCondition[ItemDetail](
    right = ItemDetail,
    fk = "id",
    on = sqls.eq(i.id, id.id),
    merge = (i, d) => i.copy(detail = d)
  ).byDefault

  def create(name: String)(implicit session: DBSession): Long =
    createWithAttributes('name -> name)
  def create(i: Item)(implicit session: DBSession): Long = create(i.name)

  def name(id: Long)(implicit session: DBSession = AutoSession): String =
    findById(id).map(_.name).get
}

object PersistItem {
  // 存在しない場合はItem作ってIDを返す
  def getItemId(name: String)(implicit session: DBSession): Long = {
    import Aliases.i
    Item.findBy(sqls.eq(i.name, name)).map(_.id)
        .getOrElse(Item.create(name))
  }
}
