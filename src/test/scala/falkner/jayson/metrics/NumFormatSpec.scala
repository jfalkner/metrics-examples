package falkner.jayson.metrics

import java.nio.file.{Files, Path}
import java.text.DecimalFormat

import falkner.jayson.metrics.Distribution.{calcContinuous, calcDiscrete}
import falkner.jayson.metrics.io.CSV
import org.specs2.matcher.MatchResult
import org.specs2.mutable.Specification

import scala.collection.JavaConverters._


/**
  * Confirms that custom number formatting works
  *
  * Use cases:
  *
  * 1. Default format floats to 2 decimal places. Saves space in serialized output
  * 2. Confirm that String value Nums never are formatted -- also allows for custom formatting
  */
class NumFormatSpec extends Specification {

  "Num formatting" should {
    "Default Double to 4 decimal places" in (Num("Test", Math.PI).value mustEqual "3.1416")
    "Default Float to 4 decimal places" in (Num("Test", Math.PI.toFloat).value mustEqual "3.1416")
    "Don't pad Float to 4 decimal places" in (Num("Test", 1.5f).value mustEqual "1.5")
    "Don't pad Decimal to 4 decimal places" in (Num("Test", 1.5d).value mustEqual "1.5")
    "Keep string precision as-is" in (Num("Test", "0.123456").value mustEqual "0.123456")
    "Custom DecimalFormat is used" in {
      val df = new DecimalFormat("#.#")
      Num("Test", "0.123456".toFloat, df).value mustEqual "0.1"
    }
    "Custom Option[DecimalFormat] is used" in {
      val df = new DecimalFormat("#.###")
      Num("Test", "0.123456".toFloat, df).value mustEqual "0.123"
    }
    "Custom continuous distribution formatting works" in {
      val a = DistCon("a", calcContinuous(Seq(0f, 1f, 0.5f), nBins = 3))
      a.mean.value mustEqual "0.5"
      a.min.value mustEqual "0"
      a.max.value mustEqual "1"
      val df = new DecimalFormat("#")
      val b = DistCon("b", calcContinuous(Seq(0f, 1f, 0.5f), nBins = 3), df)
      b.mean.value mustEqual "0"
      b.max.value mustEqual "1"
      b.min.value mustEqual "0"
    }
    "Custom discrete distribution formatting works" in {
      val a = Dist("a", calcDiscrete(Seq(2, 4, 6), nBins = 4))
      a.mean.value mustEqual "4"
      a.min.value mustEqual "2"
      a.max.value mustEqual "6"
      val df = new DecimalFormat("##")
      df.setMinimumIntegerDigits(2)
      val b = Dist("a", calcDiscrete(Seq(2, 4, 6), nBins = 4), df)
      b.mean.value mustEqual "04"
      b.min.value mustEqual "02"
      b.max.value mustEqual "06"
    }
  }
}



