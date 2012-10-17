package dsbw.util

import org.scalatest.mock.MockitoSugar
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.anyObject

trait Mocks extends MockitoSugar{
  def v[T](value:T):T = {
    org.mockito.Matchers.eq(value)
  }
  def default = {
    org.mockito.Matchers.eq(null)
  }

  def ?[T]:T = anyObject()

  def capture[T<: AnyRef](implicit mf: ClassManifest[T]):ArgumentCaptor[T] = {
    ArgumentCaptor.forClass(mf.erasure.asInstanceOf[Class[T]])
  }
}
