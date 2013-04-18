package dsbw.mongo

import com.mongodb.casbah.MongoCollection
import com.novus.salat.dao.SalatDAO
import org.bson.types.ObjectId
import com.mongodb.casbah.Imports._
import com.mongodb.MongoException.DuplicateKey
import com.novus.salat.Context
import com.mongodb.casbah.commons.MongoDBObject
import dsbw.mongo.SalatContext.ctx


/** Generic DAO class used as a superclass of MongoDB DAOs  */
class MongoDao[ObjectType <: AnyRef](collection: MongoCollection)(implicit mot: Manifest[ObjectType]) {

  val salatDao = new SalatDAO[ObjectType,ObjectId](collection){}

  /** Check the last operation performend and, in case of error, throw an exception */
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

  /** Find a document using a query and return it or None if it wasn't found */
  def findOne[T<: AnyRef](query:Map[String,T]): Option[ObjectType] = salatDao.findOne(query)

  /** Find a document by id and return it or None if it wasn't found */
  def findOneByID(id:ObjectId): Option[ObjectType] = salatDao.findOneById(id)

  /** Find a collection of documents given their ids */
  def findByIds(ids:Set[ObjectId]) =  salatDao.find("_id" $in ids).toSet

  /** Find all the documents in a collection */
  def findAll = salatDao.find((MongoDBObject.empty))

  /** Update the documents matching a query with the given update */
  def update[T<: AnyRef](query:Map[String,T],update:MongoDBObject,multi:Boolean=true) {
    salatDao.update(query,update,multi=multi)
  }

  /** Update a document identified by its id with the given update */
  def updateById(id:ObjectId,update:MongoDBObject){
    this.update(Map("_id"->id),update,multi=false)
  }

  /** Updatea collection of documents identified by their ids with the given update */
  def updateByIds(ids:Set[ObjectId],update:MongoDBObject){
    salatDao.update("_id" $in ids,update,multi=true)
  }

  /** Save a document */
  def save(obj:ObjectType) {
    salatDao.save(obj)
  }

  /** Remove all elements from collection */
  def removeAll(){
    salatDao.collection.dropCollection()
  }

  /** Remove a document identified by its id from collection */
  def remove(id:ObjectId){
    salatDao.removeById(id)
  }



}

case class DuplicateKeyException(collection:String, key:String) extends RuntimeException{

  override def toString = """DuplicateKeyException(collection="%s",key="%s")""".format(collection,key)
}
