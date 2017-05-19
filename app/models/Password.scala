package models

import scalikejdbc._
import skinny.orm.{Alias, SkinnyCRUDMapperWithId}

case class Password(id: Long, version: String, password: String)

object Password extends SkinnyCRUDMapperWithId[Long, Password] {
  override val defaultAlias: Alias[Password] = createAlias("p")

  override def idToRawValue(id: Long): Any = id
  override def rawValueToId(value: Any): Long = value.toString.toLong

  override def extract(rs: WrappedResultSet, n: ResultName[Password]): Password = autoConstruct(rs, n)

  def create(p: Password)(implicit session: DBSession): Long =
    createWithAttributes('version -> p.version, 'password -> p.password)
}
