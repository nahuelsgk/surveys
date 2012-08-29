package dsbw.mongo

import com.novus.salat._

/**SalatContext to configure type hinting */
package object SalatContext {

  val CustomTypeHint = "_t"

  implicit val ctx = new Context {
    override val name = "SalatContext-WhenNecessary"
    override val typeHintStrategy: TypeHintStrategy = StringTypeHintStrategy(when = TypeHintFrequency.WhenNecessary, typeHint = TypeHint)
  }
}
