package falkner.jayson.metrics.example

import java.nio.file.{Files, Path}

import falkner.jayson.metrics.{Metric, Metrics, Num, Str}
import spray.json._
import MetricWithVersions.strip

import scala.util.{Failure, Success, Try}


/**
  * Example of how to version a metric
  *
  * A common use case is supporting similar data that has an underlying representation that changes over time. A good
  * way to support this is as follows:
  *
  * 1. Make one class per version of the underlying raw data. e.g. MetricWithVersions_0_0_1 and MetricWithVersions_0_1_0
  * 2. Have an object with an apply method that resolves the appropriate version to use
  *
  * A simple case demonstrated here is when the version is an attribute. This is commonly done in JSON and XML data;
  * however, it is easy to also imagine a case where there is no easy-to-use version tag. In such cases the logic in the
  * apply method must capture whatever is appropriate to differentiate version.
  */
object MetricWithVersions {
  // match build.sbt for your metrics module
  lazy val version = "0.0.1"

  lazy val blank = MetricWithVersions_1_3_0(null)

  def apply(p: Path): Metrics = Try(strip(new String(Files.readAllBytes(p)).parseJson.asJsObject.fields("Version"))) match {
    case Success(v) => v match {
      case MetricWithVersions_1_2_3.version => MetricWithVersions_1_2_3(p)
      case MetricWithVersions_1_3_0.version => MetricWithVersions_1_3_0(p)
      // also practical to fallback to latest version here, if it that is preferred to having an exception raised
      case _ => throw new Exception(s"Unknown version: $v")
    }
    case Failure(t) => throw new Exception(s"Can't parse version: $p")
  }

  def strip(jsv: JsValue): String = jsv.toString.stripPrefix("\"").stripSuffix("\"")
}

// example version 0.0.1
object MetricWithVersions_1_2_3 {
  def apply(p: Path): MetricWithVersions_1_2_3 = new MetricWithVersions_1_2_3(p)
  lazy val version = "1.2.3"
}

class MetricWithVersions_1_2_3(p: Path) extends Metrics {
  override lazy val namespace = "MWV"
  override lazy val version = MetricWithVersions_1_2_3.version

  lazy val json = new String(Files.readAllBytes(p)).parseJson.asJsObject

  lazy val m = json.fields.map{case (k, v) => strip(k) -> strip(v)}

  override lazy val values = List[Metric](
    // suggested conventions from https://github.com/jfalkner/metrics#versioning-and-caching
    Str("Code Version", MetricWithVersions.version),
    Str("Spec Version", m("Version")),
    // contrived values parse from a JSON
    Str("Foo", m("Foo")),
    Num("Foo", m("Bar"))
  )
}

// example version 0.1.0 -- major change since this would break older CSV exports
object MetricWithVersions_1_3_0 {
  def apply(p: Path): MetricWithVersions_1_3_0 = new MetricWithVersions_1_3_0(p)
  lazy val version = "1.3.0"
}

class MetricWithVersions_1_3_0(p: Path) extends Metrics {
  override lazy val namespace = "MWV"
  override lazy val version = MetricWithVersions_1_3_0.version

  def removeQuotes(s:String): String = s.stripPrefix("\"").stripSuffix("\"")

  lazy val json = new String(Files.readAllBytes(p)).parseJson.asJsObject

  lazy val m = json.fields.map{case (k, v) => removeQuotes(k) -> removeQuotes(v.toString())}

  // only need to list new stuff. can recycle the old, which helps indicate exactly what is different this version
  override lazy val values = MetricWithVersions_1_2_3(p).values ++ List[Metric](
    Str("FooBar", m("FooBar"))
  )
}