package org.andre.mleap.wine

import com.beust.jcommander.{JCommander, Parameter}
import ml.combust.mleap.runtime.frame.{DefaultLeapFrame,Row}
import org.andre.mleap.{MLeapUtils,PredictUtils}

object MLeapReader {

  def main(args: Array[String]) {
    new JCommander(opts, args.toArray: _*)
    println("Options:")
    println(s"  dataPath: ${opts.dataPath}")
    println(s"  schemaPath: ${opts.schemaPath}")
    println(s"  bundlePath: ${opts.bundlePath}")
    println(s"  autoSchema: ${opts.autoSchema}")

    val model = MLeapUtils.readModel(opts.bundlePath)
    println("Model: "+model.getClass)
    println("Model Schema:")
    model.inputSchema.print
    val schema = if (opts.autoSchema) {
      model.inputSchema // NOTE: this croaks
    } else {
      MLeapUtils.readSchema(opts.schemaPath)
    }
    println("Using Schema:")
    schema.print

    val records = readData(opts.dataPath)
    val data = DefaultLeapFrame(schema, records)

    PredictUtils.predict(model, data)
  }

  def readData(dataPath: String) = {
    import scala.io.Source
    val lines = Source.fromFile(dataPath).getLines.toSeq.drop(1)
    val lst =  lines.map(x => x.split(",").toSeq ).toSeq
    lst.map(x => Row(x(0).toDouble,x(1).toDouble,x(2).toDouble, x(3).toDouble, x(4).toDouble, x(5).toDouble, x(6).toDouble, x(7).toDouble, x(8).toDouble, x(9).toDouble, x(10).toDouble,x(11).toInt))
  } 

  object opts {
    @Parameter(names = Array("--dataPath" ), description = "Data path", required=true)
    var dataPath: String = null

    @Parameter(names = Array("--bundlePath" ), description = "Bundle Path", required=true)
    var bundlePath: String = null

    @Parameter(names = Array("--schemaPath" ), description = "Schema Path", required=true)
    var schemaPath: String = null

    @Parameter(names = Array("--autoSchema" ), description = "autoSchema", required=false)
    var autoSchema = false
  }
}
