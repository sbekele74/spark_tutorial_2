package org.freemind.spark.sql

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

/**
  * DanubeStatesAnalysis2 improves on top of DanubeStatesAnalysis. It parses non java-transform and java-transform
  * into a common object: DanubeStates which has additional numeric fields jtNo and jtYes of value 0 or 1.
  * In this way, I can generate report of sum(jtNo) and sum(jtYes) side by side grouped by publish state,
  * resource or both.
  *
  * @author sling(threecuptea) on 2/7 - 2/8
  */

object DanubeStatesAnalysis2 {

  def main(args: Array[String]): Unit = {

    if (args.length < 6) {
      println("Usage: DanubeStatesAnalysis2 [non-jt-log] [jt-log] [non-jt-lower] [non-jt-upper] [jt-lower] [jt-upper]")
      System.exit(-1)
    }

    val nonJtLog = args(0)
    val jtLog = args(1)
    val nonJtLower = if (args.length > 2) args(2).toLong else 0L
    val nonJtUpper = if (args.length > 2) args(3).toLong else 999999999999L
    val jtLower = if (args.length > 2) args(4).toLong else 0L
    val jtUpper = if (args.length > 2) args(5).toLong else 999999999999L

    val spark = SparkSession
      .builder()
      .appName("DanubeStatesAnalysis2")
      .getOrCreate()
    import spark.implicits._

    val nonJtRawDS = spark.read.textFile(nonJtLog)
    val jtRawDS = spark.read.textFile(jtLog)

    val parser = new DanubeLogsParser()
    //Do the followings if I only want to include PUBLISH and UNPUBLISH state in the report
    //val statesInc = Seq("PUBLISH", "UNPUBLISH") //_* expanded to var args
    //val nonJtDS = nonJtRawDS.flatMap(parser.parseNonJtLog).filter($"pubId".between(nonJtLower, nonJtUpper) && $"pubId".isin(statesInc:_*)).cache()
    val nonJtDS = nonJtRawDS.flatMap(parser.parseNonJtLog2).filter($"pubId".between(nonJtLower, nonJtUpper)).cache()
    printf("NON Java-transform pubId boundary is [%d. %d], diff, inc= %d.\n", nonJtLower, nonJtUpper, (nonJtUpper - nonJtLower + 1))
    println(s"NON Java-transform DanubeState count= ${nonJtDS.count}")
    println()

    val jtDS = jtRawDS.flatMap(parser.parseJtLog2).filter($"pubId".between(jtLower, jtUpper)).cache()
    printf("Java-transform pubId boundary is [%d. %d], diff, inc= %d.\n", jtLower, jtUpper, (jtUpper - jtLower + 1))
    println(s"Java-transform DanubeState count= ${jtDS.count}")

    println()
    println("Union together to generate summary")
    val combinedDS = nonJtDS.union(jtDS)

    println("Count groupBy PUBLISH_STATE")
    combinedDS.groupBy($"state").agg(sum($"jtNo"), sum($"jtYes")).show(truncate = false)

    println("Count groupBy RESOURCE")
    combinedDS.groupBy($"resource").agg(sum($"jtNo"), sum($"jtYes")).sort($"resource").show(100, truncate = false)

    println("Count groupBy RESOURCE and PUBLISH_STATE")
    combinedDS.groupBy($"resource", $"state").agg(sum($"jtNo"), sum($"jtYes")).sort($"resource", $"state").show(300, truncate = false)

    println("Count groupBy PUBLISH_STATE and RESOURCE")
    combinedDS.groupBy($"state", $"resource").agg(sum($"jtNo"), sum($"jtYes")).sort($"state", $"resource").show(300, truncate = false)

  }

}
