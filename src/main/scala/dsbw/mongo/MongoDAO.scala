package dsbw.mongo

import com.mongodb.casbah.MongoCollection
import com.novus.salat.dao.SalatDAO
import org.bson.types.ObjectId
import com.mongodb.casbah.Imports._
import com.mongodb.MongoException.DuplicateKey
import com.novus.salat.Context
import com.mongodb.casbah.commons.MongoDBObject
import dsbw.mongo.SalatContext.ctx


class MongoDao[ObjectType <: AnyRef](collection: MongoCollection)(implicit mot: Manifest[ObjectType]) {

  val salatDao = new SalatDAO[ObjectType,ObjectId](collection){}

  def checkLastError() {
    val lastError = collection.getLastError()
    try{
      lastError.throwOnError()
    }catch{
      case m:DuplicateKey => {
        val errorMsg = lastError.getString("err")
        val index = collection.indexInfo.find(ii => (ii.get("unique") == true) && errorMsg.contains("%s.$%s".format(ii.get("ns"),ii.get("name"))))
        index.map(ii => throw new DuplicateKeyException(collection.name,ii.get("name").asInstanceOf[String]))
        throw m
      }
      case t:Throwable => throw t
    }
  }

  def findOne[T<: AnyRef](query:Map[String,T]): Option[ObjectType] = salatDao.findOne(query)

  def findOneByID(id:ObjectId): Option[ObjectType] = salatDao.findOneByID(id)

  def findByIds(ids:Set[ObjectId]) =  salatDao.find("_id" $in ids).toSet

  def findAll = salatDao.find((MongoDBObject.empty))

  def update[T<: AnyRef](query:Map[String,T],update:MongoDBObject,multi:Boolean=true) {
    salatDao.update(query,update,multi=multi)
  }

  def updateById(id:ObjectId,update:MongoDBObject){
    this.update(Map("_id"->id),update,multi=false)
  }

  def updateByIds(ids:Set[ObjectId],update:MongoDBObject){
    salatDao.update("_id" $in ids,update,multi=true)
  }

  def save(obj:ObjectType) {
    salatDao.save(obj)
  }

}

case class DuplicateKeyException(collection:String, key:String) extends RuntimeException{

  override def toString = """DuplicateKeyException(collection="%s",key="%s")""".format(collection,key)
}
