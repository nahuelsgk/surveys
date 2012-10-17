package dsbw.chirps


import org.scalatest.{BeforeAndAfterEach, FlatSpec}
import java.text.SimpleDateFormat
import java.util.Date

import org.bson.types.ObjectId
import org.mockito.Mockito._
import org.scalatest.Assertions._
import dsbw.util.Mocks

class ChirpsApiV1Spec extends FlatSpec with Mocks with BeforeAndAfterEach {

  trait TestChirpsApi{

    val chirpsRepository = mock[ChirpsRepository]
    val chirpersRepository = mock[ChirpersRepository]
    val api = new ChirpsApi(chirpsRepository,chirpersRepository)
  }

  it should "list all chirps" in {
    new TestChirpsApi {
      val chirps = api.listChirps
      assert(chirps === List())
    }
  }
}
