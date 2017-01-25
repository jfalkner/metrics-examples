package falkner.jayson.metrics

import java.nio.file.{Files, Path}

import falkner.jayson.metrics.io.{CSV, JSON}
import org.specs2.matcher.MatchResult
import org.specs2.mutable.Specification

import scala.collection.JavaConverters._


/**
  * Test to confirm that the CSV code correctly handles multi-row files
  */
class MultiRowCSVSpec extends Specification {

  class Row(foo: String, bar: Int) extends Metrics {
    override val namespace = "Example"
    override val version = "_"
    override lazy val values: List[Metric] = List(Str("Foo", foo), Num("Bar", bar))
  }

  "Multi-line CSVs" should {
    "Parse correctly" in {
      withCleanup { (p) =>
        println("README.md Example CSV Export")
        val a = new Row("a", 1)
        val b = new Row("b", 2)
        val c = new Row("c", 3)
        Files.write(p, (CSV(a).all + "\n" + Seq(b, c).map(v => CSV(v).values).mkString("\n")).getBytes)
        val lines = Files.readAllLines(p).asScala
        CSV(lines) mustEqual CSV(a)
        CSV(lines, 2) mustEqual CSV(b)
        CSV(lines, 3) mustEqual CSV(c)
      }
    }
  }

  def withCleanup(f: (Path) => MatchResult[Any]): MatchResult[Any] = {
    val temp = Files.createTempFile("test", "tmp")
    try {
      f(temp)
    }
    finally {
      Files.delete(temp)
    }
  }
}



