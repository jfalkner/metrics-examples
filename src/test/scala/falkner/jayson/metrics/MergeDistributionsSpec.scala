package falkner.jayson.metrics


import org.specs2.mutable.Specification
import Distribution._


/**
  * Test to confirm that distribution merging works as expected
  *
  * In some cases there is too much data or it is otherwise convenient to calculate many smaller distributions and merge
  * them together later. This logic approximates the overall distribution, mean and median.
  */
class MergeDistributionsSpec extends Specification {

  "Distribution merge logic" should {
    "Merges Continuous correctly" in {
      val a = Seq(0f, 0.1f, 0.1f, 0.2f)
      val b = Seq(0.1f, 0.2f, 0.2f, 0.3f)
      val c = Seq(0.2f, 0.3f, 0.3f, 0.4f)
      val ad = calcContinuous(a, nBins = 4, sort=false, forceMin = 0f, forceMax = 0.4f)
      val bd = calcContinuous(b, nBins = 4, sort=false, forceMin = 0f, forceMax = 0.4f)
      val cd = calcContinuous(c, nBins = 4, sort=false, forceMin = 0f, forceMax = 0.4f)
      val m = mergeContinuous(Seq(ad, bd, cd), nBins = 4, forceMin = 0f, forceMax = 0.4f)

      val all = (a ++ b ++ c)

      m.sampleNum mustEqual all.size
      Math.abs(m.mean - (all.sum / all.size)) < 0.01 must beTrue
      m.median mustEqual bd.median
      m.bins mustEqual Seq(1, 3, 4, 4)
    }

    "Merges Discrete correctly" in {
      val a = Seq[Short](0, 1, 1, 2)
      val b = Seq[Short](1, 2, 2, 3)
      val c = Seq[Short](2, 3, 3, 4)
      val ad = calcShort(a, nBins = 4, sort=false, forceMin = 0, forceMax = 4)
      val bd = calcShort(b, nBins = 4, sort=false, forceMin = 0, forceMax = 4)
      val cd = calcShort(c, nBins = 4, sort=false, forceMin = 0, forceMax = 4)
      val m = mergeDiscrete(Seq(ad, bd, cd), nBins = 4, forceMin = 0, forceMax = 4)

      val all = (a ++ b ++ c)

      m.sampleNum mustEqual all.size
      Math.abs(m.mean - (all.sum / all.size)) < 0.1 must beTrue
      m.median mustEqual bd.median
      m.bins mustEqual Seq(1, 3, 4, 4)
    }
  }
}



