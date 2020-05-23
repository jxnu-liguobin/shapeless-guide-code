
trait Migration[A, B] {
  def apply(original: A): B
}

object Migration {
  def pure[A, B](func: A => B): Migration[A, B] = (original: A) => func(original)
}

case class SameA(a: String, b: Int, c: Boolean)
case class SameB(a: String, b: Int, c: Boolean)
object SameB {
  implicit val p = Migration.pure[SameA, SameB](a => SameB(a.a, a.b, a.c))
}

case class DropFieldA(a: String, b: Int, c: Boolean)
case class DropFieldB(a: String, c: Boolean)
object DropFieldA {
  implicit val p = Migration.pure[DropFieldA, DropFieldB](a => DropFieldB(a.a, a.c))
}

case class AddFieldA(a: String)
case class AddFieldB(a: String, z: Int)

case class ReorderA(a: String, z: Int)
case class ReorderB(z: Int, a: String)

case class KitchenSinkA(a: String, b: Int, c: Boolean)
case class KitchenSinkB(c: Boolean, z: Option[String], a: String)

object Main extends Demo {
  implicit class MigrationOps[A](original: A) {
    def migrateTo[B](implicit migration: Migration[A, B]): B =
      migration(original)
  }

  print(SameA("abc", 123, true).migrateTo[SameB])
  print(DropFieldA("abc", 123, true).migrateTo[DropFieldB])
  //  need implicit migration like SameB DropFieldB
  //  print(AddFieldA("abc").migrateTo[AddFieldB])
  //  print(ReorderA("abc", 123).migrateTo[ReorderB])
  //  print(KitchenSinkA("abc", 123, true).migrateTo[KitchenSinkB])
}
