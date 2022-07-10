package app.jopiter

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DummyObjectTest : FunSpec({

  val target = DummyObject

  test("foo returns foo") {
    target.foo() shouldBe "foo"
  }

})
