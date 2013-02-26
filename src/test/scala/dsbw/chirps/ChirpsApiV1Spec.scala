package dsbw.chirps


import org.scalatest.{BeforeAndAfterEach, FlatSpec}
import java.text.SimpleDateFormat
import java.util.Date

import org.bson.types.ObjectId
import org.mockito.Mockito._
import org.scalatest.Assertions._
import dsbw.util.Mocks
import dsbw.server.HttpStatusCode

class ChirpsApiV1Spec extends FlatSpec with Mocks with BeforeAndAfterEach {

  trait TestChirpsApi{

    val chirpsRepository = mock[ChirpsRepository]
    val chirpersRepository = mock[ChirpersRepository]
    val api = new ChirpsApi(new ChirpsService(chirpsRepository,chirpersRepository))
  }

  it should "return an empty list when listing all chirps and there are no chirps in the DB" in {
    new TestChirpsApi {
      when(chirpsRepository.findAll).thenReturn(List())
      val response = api.service("GET", "/api/chirps")
      assert(HttpStatusCode.Ok === response.status)
      assert(Some(List()) === response.body)
    }
  }
}
