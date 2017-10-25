---
title: Advanced SBT Spark Build Dependencies.scala
permalink: /AdvBuildDeps/
---

Advanced Build Dependencies.scala
================

About This Build
---------------
The following scala program tests the default Hortonworks Hadoop directory for any version, and takes the largest version found. It then uses this version to find the correct directory containing spark, and checks for versions 1 and 2, taking version 2 if found. 

The program next reads all *spark jars* in the base spark directory found, and parses the hortonworks repo for all matching official spark jars. After finding the matching jars a list of these jars is output along with the spark version and the corresponding url repo-location. 

The program then reads all *spark hdp hadoop jars* and parses the hortonworks repo for all matching official spark hdp hadoop jars. After finding the matching jars a list of these jars is also output, along with the hdp hadoop version and the corresponding url repo-location.

After doing all of the above this script will find the current build and add the hive-site.xml contained in the selected spark version home folder in your system and add it to the correct sbt build path before initializing the overarching build.sbt 

All of this will occur only if an hdpversion is detected in the default hdp path. 

-----------
Things to add:

- If hdp versions are found of different lengths ask to set a major version before building.
- change to read from system first, aka allow users to pass differing paths from the environment 


-----------------------

Full Dependencies.scala Script
------------------------------


```scala
//The following 3 are commented out for this build because they will not work in pure scala 
//import sbt._

//object Dependencies {
//  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.3"

import java.io.File
import scala.collection.mutable.ArrayBuffer

def getHDP(dir: java.io.File): Option[String] = {
  try {
      val remove_path = dir.toString.r
      val remove_backslash = "/".r
      //Removing decimals, and Flattening the version values to more easily compare.
      val remove_decimals = "\\.".r
      val replace_dash = "-".r
      //getting all of the folders in the directory
      val hdp_directories = dir.listFiles
      //case class to organize and store data in the array buffer
      case class combined(hdpVdec: Double, hdpVfull: String, hdpVid: Int, hdpVbuild: Int, hdpPath: String)
      //Arraybuffer to store the new versions
      val PathsAndVersions = new ArrayBuffer[combined]
      for (hdp_dir <- hdp_directories){
        val r1 = remove_path.replaceFirstIn(hdp_dir.toString, "")
        val r2 = remove_backslash.replaceAllIn(r1, "")
        //if the hdp_version without the path starts with a digit
        if (r2.toString.charAt(0).isDigit) {
          val r3 = remove_decimals.replaceAllIn(r2, "")
          val r4 = r3.split("-")
          val r5 = replace_dash.replaceAllIn(r3, ".").toDouble
          PathsAndVersions += new combined(r5, r2, r4(0).toInt, r4(1).toInt, hdp_dir.toString)
        }
      }
      val maxByKey = PathsAndVersions.maxBy(_.hdpVdec)
      val hdp_version = maxByKey.hdpVfull
      Some(hdp_version)
  } catch {
    case e: Exception => None
  }
}

val hdp_directory = new File("/usr/hdp/")
//this will return the newest hdp version directory in /usr/hdp/   or it will return null
val hdpVersion = getHDP(hdp_directory).getOrElse(null)

val sparkDependencies=
hdpVersion match {
  case hdp: String  =>
      println(s"""|
**************************************************************
 SEARCHING for Hortonworks Spark Jars for HDP version ~ $hdpVersion
**************************************************************
""".stripMargin)
    //Using the HDP Version and Path to Check for Spark versions and take the larger
    val dir = new File(hdp_directory.toString++"/"++ hdpVersion++"/")
    val hdp_directories = dir.listFiles
    val spark_path =
      if (hdp_directories.contains(new File(dir.toString++"/spark2"))) dir.toString++"/spark2"
      else dir.toString++"/spark"
    //
    //Def to get read local jars
    def getListOfFiles(dir: File, extensions: List[String]): ArrayBuffer[String] = {
      val results = new ArrayBuffer[String]
      val files = dir.listFiles.filter(_.isFile).toList.filter { file =>
        extensions.exists(file.getName.endsWith(_))
      }
      for (file <- files){
        //results.add(file.getName())
        results += file.getName()
      }
      results
    }
    //Getting and filtering the local spark jars
    val okFileExtensions = List("jar")
    val sparkLib = getListOfFiles(new File(spark_path++"/jars"), okFileExtensions)
    val sparkJars = sparkLib.filter(_.toString.contains("spark"))
    val remove_jar = ".jar".r
    val sparkJars_clean = sparkJars.map(x => remove_jar.replaceAllIn(x, ""))
    //
    //searching the hortonworks repo for matching jars
    import scala.io.Source.fromURL
    val mainPage = "http://repo.hortonworks.com/content/groups/public/org/apache/spark/"
    val repos = fromURL(mainPage).mkString
    //These will get the links from the repos, and parse them into a list
    val c = repos.split("\n").toList.filter(!_.contains("<link")).mkString
    val r = scala.xml.parsing.XhtmlParser(scala.io.Source.fromString(c))
    val spark_repos = r \\ "a"
    //this is where it all comes together
    val remove_backslash = "/".r
    val replace_dash = "-".r
    import scala.collection.mutable.ArrayBuffer
    case class dependencies(Identifier:String, Spark_Version:String, RepoLocation:String)
    val Dependencies = new ArrayBuffer[dependencies]
    for (sparkJar <- sparkJars_clean){
      for (repo <- spark_repos) {
        val repo_dir = remove_backslash.replaceAllIn(repo.child.toString, "")
        if (sparkJar.contains(repo_dir)){
          val remove_repoDir = repo_dir.r
          val r1= remove_repoDir.replaceAllIn(sparkJar, "")
          val r2 = replace_dash.replaceFirstIn(r1, "")
          Dependencies += new dependencies(repo_dir, r2, repo.attribute("href").head.text)
        }
      }
    }

    //.groupBy(_._3).mapValues(_.sortBy(_._2).last._1)
    //.map{ case (v, url) => (v == sv, v, url) }
    println("======================================================================== ")
    println("Found these repos")
    Dependencies.foreach(println)

    //the default jars look like this
    //libraryDependencies += "org.apache.spark" %% "spark-core" % "2.2.0"
    //so you should be able to add them like this

    Dependencies
  case null =>
    Nil
}

val sparkVersion =
    hdpVersion match {
    case hdp: String  =>
      sparkDependencies(1).Spark_Version
    case null =>
     sys.props.getOrElse("spark.version", "2.1.1")
}

val ScalaID =
    hdpVersion match {
    case hdp: String  =>
        val remove_identifier = "spark-core".r
        remove_identifier.replaceFirstIn(sparkDependencies.map(_.Identifier).filter(_.toString.contains("spark-core")).toList(0).toString, "")
    case null =>
     Nil
}
///////////////////////
/////////////////////////
//Now can get the other jars needed

val sparkhdpHadoopDependencies =
  hdpVersion match {
  case hdp: String  =>
      println(s"""|
**************************************************************
 SEARCHING for Hortonworks Spark Jars for HDP version ~ $hdpVersion
**************************************************************
""".stripMargin)
    //Using the HDP Version and Path to Check for Spark versions and take the larger
    val dir = new File(hdp_directory.toString++"/"++ hdpVersion++"/")
    val hdp_directories = dir.listFiles
    val spark_path =
      if (hdp_directories.contains(new File(dir.toString++"/spark2"))) dir.toString++"/spark2"
      else dir.toString++"/spark"
    //Def to get read local jars
    def getListOfFiles(dir: File, extensions: List[String]): ArrayBuffer[String] = {
      val results = new ArrayBuffer[String]
      val files = dir.listFiles.filter(_.isFile).toList.filter { file =>
        extensions.exists(file.getName.endsWith(_))
      }
      for (file <- files){
        //results.add(file.getName())
        results += file.getName()
      }
      results
    }
    //Getting and filtering the local spark jars
    val okFileExtensions = List("jar")
    val sparkHadoopJars = getListOfFiles(new File(spark_path++"/jars"), okFileExtensions).filter(_.toString.contains("hadoop"))
    val remove_jar = ".jar".r
    val sparkHadoopJars_clean = sparkHadoopJars.map(x => remove_jar.replaceAllIn(x, ""))
    //
    //searching the hortonworks repo for matching jars
    import scala.io.Source.fromURL
    val mainPage = "http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/"
    val repos = fromURL(mainPage).mkString
 //These will get the links from the repos, and parse them into a list
    val c = repos.split("\n").toList.filter(!_.contains("<link")).mkString
    val r = scala.xml.parsing.XhtmlParser(scala.io.Source.fromString(c))
    val spark_hadoop_repos = r \\ "a"
    //this is where it all comes together
    val remove_backslash = "/".r
    val replace_dash = "-".r
    import scala.collection.mutable.ArrayBuffer
    case class dependencies(Identifier:String, Version:String, RepoLocation:String)
    val Dependencies = new ArrayBuffer[dependencies]
    for (sparkHadoopJar <- sparkHadoopJars_clean){
      for (repo <- spark_hadoop_repos) {
        val repo_dir = remove_backslash.replaceAllIn(repo.child.toString, "")
        if (sparkHadoopJar.contains(repo_dir)){
          val remove_repoDir = repo_dir.r
          val r1 = remove_repoDir.replaceAllIn(sparkHadoopJar, "")
          val r2 = replace_dash.replaceFirstIn(r1, "")
          //check if r2 (Version) starts with number
          if (r2.toString.charAt(0).isDigit) {
            Dependencies += new dependencies(repo_dir, r2, repo.attribute("href").head.text)
          }
        }
      }
    }

    //.groupBy(_._3).mapValues(_.sortBy(_._2).last._1)
    //.map{ case (v, url) => (v == sv, v, url) }
    println("======================================================================== ")
    println("Found these repos")
    Dependencies.foreach(println)

    //the default jars look like this
    //libraryDependencies += "org.apache.spark" %% "spark-core" % "2.2.0"
    //so you should be able to add them like this


    //checking the current directory
    val current_dir = new File(".").getAbsolutePath().toString.replaceAll("^(.*)\\.(.*)$","$1$2")
    println("The current_dir in SBT is" ++ current_dir)

    Dependencies


  case null =>
    Nil
}




val hadoopVersion =
    hdpVersion match {
    case hdp: String  =>
      sparkhdpHadoopDependencies(1).Version
    case null =>
     sys.props.getOrElse("hadoop.version", "2.7.3")
}

import java.io.File
import org.apache.commons.io.FileUtils
import org.apache.commons.io.LineIterator
import org.apache.commons.io.filefilter.SuffixFileFilter



//copying the hive-site.xml if hdpVersion exists
hdpVersion match {
    case hdp: String  =>
       val dir = new File(hdp_directory.toString++"/"++ hdpVersion++"/")
       val hdp_directories = dir.listFiles
       val spark_path =
          if (hdp_directories.contains(new File(dir.toString++"/spark2"))) dir.toString++"/spark2"
          else dir.toString++"/spark"
      val srcFile = new File(spark_path++"/conf/hive-site.xml")
      val destDir = new File(new File(".").getAbsolutePath().toString.replaceAll("^(.*)\\.(.*)$","$1$2")++"src/main/resources")
      FileUtils.copyFileToDirectory(srcFile, destDir)
    case null =>
      println("Hortonworks HDP Directory Not Found in /usr/hdp/YOUR_HDP_VERSION")
}

//}

```


><pre>
> <console>:113: warning: inferred existential type scala.collection.AbstractSeq[dependencies] with Serializable forSome { type dependencies <: Product with Serializable{val Identifier: String; val Spark_Version: String; val RepoLocation: String; def copy(Identifier: String,Spark_Version: String,RepoLocation: String): dependencies; def copy$default$1: String @scala.annotation.unchecked.uncheckedVariance; def copy$default$2: String @scala.annotation.unchecked.uncheckedVariance; def copy$default$3: String @scala.annotation.unchecked.uncheckedVariance} }, which cannot be expressed by wildcards,  should be enabled
> by making the implicit value scala.language.existentials visible.
> This can be achieved by adding the import clause 'import scala.language.existentials'
> or by setting the compiler option -language:existentials.
> See the Scaladoc for value scala.language.existentials for a discussion
> why the feature should be explicitly enabled.
> hdpVersion match {
>            ^
> <console>:208: warning: inferred existential type scala.collection.AbstractSeq[dependencies] with Serializable forSome { type dependencies <: Product with Serializable{val Identifier: String; val Version: String; val RepoLocation: String; def copy(Identifier: String,Version: String,RepoLocation: String): dependencies; def copy$default$1: String @scala.annotation.unchecked.uncheckedVariance; def copy$default$2: String @scala.annotation.unchecked.uncheckedVariance; def copy$default$3: String @scala.annotation.unchecked.uncheckedVariance} }, which cannot be expressed by wildcards,  should be enabled
> by making the implicit value scala.language.existentials visible.
>   hdpVersion match {
>              ^
> 
> **************************************************************
>  SEARCHING for Hortonworks Spark Jars for HDP version ~ 2.6.2.0-205
> **************************************************************
> 
> ======================================================================== 
> Found these repos
> dependencies(spark-catalyst_2.11,2.1.1.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-catalyst_2.11/)
> dependencies(spark-cloud_2.11,2.1.1.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-cloud_2.11/)
> dependencies(spark-core_2.11,2.1.1.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-core_2.11/)
> dependencies(spark-graphx_2.11,2.1.1.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-graphx_2.11/)
> dependencies(spark-hive-thriftserver_2.11,2.1.1.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-hive-thriftserver_2.11/)
> dependencies(spark-hive_2.11,2.1.1.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-hive_2.11/)
> dependencies(spark-launcher_2.11,2.1.1.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-launcher_2.11/)
> dependencies(spark-mllib-local_2.11,2.1.1.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-mllib-local_2.11/)
> dependencies(spark-mllib_2.11,2.1.1.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-mllib_2.11/)
> dependencies(spark-network-common_2.11,2.1.1.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-network-common_2.11/)
> dependencies(spark-network-shuffle_2.11,2.1.1.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-network-shuffle_2.11/)
> dependencies(spark-repl_2.11,2.1.1.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-repl_2.11/)
> dependencies(spark-sketch_2.11,2.1.1.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-sketch_2.11/)
> dependencies(spark-sql_2.11,2.1.1.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-sql_2.11/)
> dependencies(spark-streaming_2.11,2.1.1.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-streaming_2.11/)
> dependencies(spark-tags_2.11,2.1.1.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-tags_2.11/)
> dependencies(spark-unsafe_2.11,2.1.1.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-unsafe_2.11/)
> dependencies(spark-yarn_2.11,2.1.1.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-yarn_2.11/)
> 
> **************************************************************
>  SEARCHING for Hortonworks Spark Jars for HDP version ~ 2.6.2.0-205
> **************************************************************
> 
> ======================================================================== 
> Found these repos
> dependencies(hadoop-mapreduce-client-app,2.7.3.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-mapreduce-client-app/)
> dependencies(hadoop-auth,2.7.3.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-auth/)
> dependencies(hadoop-aws,2.7.3.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-aws/)
> dependencies(hadoop-azure,2.7.3.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-azure/)
> dependencies(hadoop-client,2.7.3.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-client/)
> dependencies(hadoop-common,2.7.3.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-common/)
> dependencies(hadoop-hdfs,2.7.3.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-hdfs/)
> dependencies(hadoop-yarn-api,2.7.3.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-yarn-api/)
> dependencies(hadoop-annotations,2.7.3.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-annotations/)
> dependencies(hadoop-azure-datalake,2.7.3.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-azure-datalake/)
> dependencies(hadoop-mapreduce-client-common,2.7.3.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-mapreduce-client-common/)
> dependencies(hadoop-mapreduce-client-core,2.7.3.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-mapreduce-client-core/)
> dependencies(hadoop-mapreduce-client-jobclient,2.7.3.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-mapreduce-client-jobclient/)
> dependencies(hadoop-mapreduce-client-shuffle,2.7.3.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-mapreduce-client-shuffle/)
> dependencies(hadoop-openstack,2.7.3.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-openstack/)
> dependencies(hadoop-yarn-client,2.7.3.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-yarn-client/)
> dependencies(hadoop-yarn-common,2.7.3.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-yarn-common/)
> dependencies(hadoop-yarn-registry,2.7.3.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-yarn-registry/)
> dependencies(hadoop-yarn-server-common,2.7.3.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-yarn-server-common/)
> dependencies(hadoop-yarn-server-web-proxy,2.7.3.2.6.2.0-205,http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-yarn-server-web-proxy/)
> The current_dir in SBT is/apps/spark-notebook_from_src/spark-notebook/
> import java.io.File
> import scala.collection.mutable.ArrayBuffer
> getHDP: (dir: java.io.File)Option[String]
> hdp_directory: java.io.File = /usr/hdp
> hdpVersion: String = 2.6.2.0-205
> sparkDependencies: scala.collection.AbstractSeq[dependencies] with Serializable forSome { type dependencies <: Product with Serializable{val Identifier: String; val Spark_Version: String; val RepoLocation: String; def copy(Identifier: String,Spark_Version: String,RepoLocation: String): dependencies; def copy$default$1: String @scala.annotation.unchecked.uncheckedVariance; def copy$default$2: String @scala.annotation.unchecked.uncheckedVariance; def copy$default$3: String @scala.annotation.unchecked.uncheckedVariance} } = ArrayBuffer(dependencies(spark-catalyst_2.11,2.1.1.2.6.2.0-205,http://repo.hortonworks.com/c...
> </pre>



## Displaying Variables Created

```scala
println(sparkVersion)
```


><pre>
> 2.1.1.2.6.2.0-205
> </pre>




```scala
println(hdpVersion)
```


><pre>
> 2.6.2.0-205
> </pre>




```scala
println(ScalaID)
```


><pre>
> _2.11
> </pre>




```scala
println(hadoopVersion)
```


><pre>
> 2.7.3.2.6.2.0-205
> </pre>



### This is a program to output data in a tabular manner. 

```scala
object Tabulator {
  import java.lang._
  def format(table: Seq[Seq[Any]]) = table match {
    case Seq() => ""
    case _ => 
      val sizes = for (row <- table) yield (for (cell <- row) yield if (cell == null) 0 else cell.toString.length)
      val colSizes = for (col <- sizes.transpose) yield col.max
      val rows = for (row <- table) yield formatRow(row, colSizes)
      formatRows(rowSeparator(colSizes), rows)
  }

  def formatRows(rowSeparator: String, rows: Seq[String]): String = (
    rowSeparator :: 
    rows.head :: 
    rowSeparator :: 
    rows.tail.toList ::: 
    rowSeparator :: 
    List()).mkString("\n")

  def formatRow(row: Seq[Any], colSizes: Seq[Int]) = {
    val cells = (for ((item, size) <- row.zip(colSizes)) yield if (size == 0) "" else ("%" + size + "s").format(item))
    cells.mkString("|", "|", "|")
  }

  def rowSeparator(colSizes: Seq[Int]) = colSizes map { "-" * _ } mkString("+", "+", "+")
}
```


><pre>
> defined object Tabulator
> </pre>



### The following are the lists generated

```scala
// could use     println(Tabulator.format(sparkhdpHadoopDependencies.map(x => Seq(x.Identifier, x.Version, x.RepoLocation))))
//Used following for titles
import scala.language.existentials

val refit = new ArrayBuffer[List[String]]

refit += List("Identifier", "Hadoop_Version", "Repolocation")

for (dep <- sparkhdpHadoopDependencies){
  refit += List(dep.Identifier, dep.Version, dep.RepoLocation)
}

println(Tabulator.format(refit))
```


><pre>
> +---------------------------------+-----------------+------------------------------------------------------------------------------------------------------+
> |                       Identifier|   Hadoop_Version|                                                                                          Repolocation|
> +---------------------------------+-----------------+------------------------------------------------------------------------------------------------------+
> |      hadoop-mapreduce-client-app|2.7.3.2.6.2.0-205|      http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-mapreduce-client-app/|
> |                      hadoop-auth|2.7.3.2.6.2.0-205|                      http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-auth/|
> |                       hadoop-aws|2.7.3.2.6.2.0-205|                       http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-aws/|
> |                     hadoop-azure|2.7.3.2.6.2.0-205|                     http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-azure/|
> |                    hadoop-client|2.7.3.2.6.2.0-205|                    http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-client/|
> |                    hadoop-common|2.7.3.2.6.2.0-205|                    http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-common/|
> |                      hadoop-hdfs|2.7.3.2.6.2.0-205|                      http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-hdfs/|
> |                  hadoop-yarn-api|2.7.3.2.6.2.0-205|                  http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-yarn-api/|
> |               hadoop-annotations|2.7.3.2.6.2.0-205|               http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-annotations/|
> |            hadoop-azure-datalake|2.7.3.2.6.2.0-205|            http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-azure-datalake/|
> |   hadoop-mapreduce-client-common|2.7.3.2.6.2.0-205|   http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-mapreduce-client-common/|
> |     hadoop-mapreduce-client-core|2.7.3.2.6.2.0-205|     http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-mapreduce-client-core/|
> |hadoop-mapreduce-client-jobclient|2.7.3.2.6.2.0-205|http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-mapreduce-client-jobclient/|
> |  hadoop-mapreduce-client-shuffle|2.7.3.2.6.2.0-205|  http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-mapreduce-client-shuffle/|
> |                 hadoop-openstack|2.7.3.2.6.2.0-205|                 http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-openstack/|
> |               hadoop-yarn-client|2.7.3.2.6.2.0-205|               http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-yarn-client/|
> |               hadoop-yarn-common|2.7.3.2.6.2.0-205|               http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-yarn-common/|
> |             hadoop-yarn-registry|2.7.3.2.6.2.0-205|             http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-yarn-registry/|
> |        hadoop-yarn-server-common|2.7.3.2.6.2.0-205|        http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-yarn-server-common/|
> |     hadoop-yarn-server-web-proxy|2.7.3.2.6.2.0-205|     http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-yarn-server-web-proxy/|
> +---------------------------------+-----------------+------------------------------------------------------------------------------------------------------+
> import scala.language.existentials
> refit: scala.collection.mutable.ArrayBuffer[List[String]] = ArrayBuffer(List(Identifier, Hadoop_Version, Repolocation), List(hadoop-mapreduce-client-app, 2.7.3.2.6.2.0-205, http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-mapreduce-client-app/), List(hadoop-auth, 2.7.3.2.6.2.0-205, http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-auth/), List(hadoop-aws, 2.7.3.2.6.2.0-205, http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-aws/), List(hadoop-azure, 2.7.3.2.6.2.0-205, http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-azure/), List(hadoop-client, 2.7.3.2.6.2.0-205, http://repo.hortonworks.com/content/groups/public/org/apache/hadoop/hadoop-client/), List(h...
> </pre>




```scala
//println(Tabulator.format(sparkDependencies.map(x => List(x.Identifier, x.Spark_Version, x.RepoLocation))))

import scala.language.existentials

val refit = new ArrayBuffer[List[String]]

refit += List("Identifier", "Spark_Version", "Repolocation")

for (dep <- sparkDependencies){
  refit += List(dep.Identifier, dep.Spark_Version, dep.RepoLocation)
}

println(Tabulator.format(refit))
```


><pre>
> +----------------------------+-----------------+------------------------------------------------------------------------------------------------+
> |                  Identifier|    Spark_Version|                                                                                    Repolocation|
> +----------------------------+-----------------+------------------------------------------------------------------------------------------------+
> |         spark-catalyst_2.11|2.1.1.2.6.2.0-205|         http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-catalyst_2.11/|
> |            spark-cloud_2.11|2.1.1.2.6.2.0-205|            http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-cloud_2.11/|
> |             spark-core_2.11|2.1.1.2.6.2.0-205|             http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-core_2.11/|
> |           spark-graphx_2.11|2.1.1.2.6.2.0-205|           http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-graphx_2.11/|
> |spark-hive-thriftserver_2.11|2.1.1.2.6.2.0-205|http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-hive-thriftserver_2.11/|
> |             spark-hive_2.11|2.1.1.2.6.2.0-205|             http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-hive_2.11/|
> |         spark-launcher_2.11|2.1.1.2.6.2.0-205|         http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-launcher_2.11/|
> |      spark-mllib-local_2.11|2.1.1.2.6.2.0-205|      http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-mllib-local_2.11/|
> |            spark-mllib_2.11|2.1.1.2.6.2.0-205|            http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-mllib_2.11/|
> |   spark-network-common_2.11|2.1.1.2.6.2.0-205|   http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-network-common_2.11/|
> |  spark-network-shuffle_2.11|2.1.1.2.6.2.0-205|  http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-network-shuffle_2.11/|
> |             spark-repl_2.11|2.1.1.2.6.2.0-205|             http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-repl_2.11/|
> |           spark-sketch_2.11|2.1.1.2.6.2.0-205|           http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-sketch_2.11/|
> |              spark-sql_2.11|2.1.1.2.6.2.0-205|              http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-sql_2.11/|
> |        spark-streaming_2.11|2.1.1.2.6.2.0-205|        http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-streaming_2.11/|
> |             spark-tags_2.11|2.1.1.2.6.2.0-205|             http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-tags_2.11/|
> |           spark-unsafe_2.11|2.1.1.2.6.2.0-205|           http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-unsafe_2.11/|
> |             spark-yarn_2.11|2.1.1.2.6.2.0-205|             http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-yarn_2.11/|
> +----------------------------+-----------------+------------------------------------------------------------------------------------------------+
> import scala.language.existentials
> refit: scala.collection.mutable.ArrayBuffer[List[String]] = ArrayBuffer(List(Identifier, Spark_Version, Repolocation), List(spark-catalyst_2.11, 2.1.1.2.6.2.0-205, http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-catalyst_2.11/), List(spark-cloud_2.11, 2.1.1.2.6.2.0-205, http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-cloud_2.11/), List(spark-core_2.11, 2.1.1.2.6.2.0-205, http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-core_2.11/), List(spark-graphx_2.11, 2.1.1.2.6.2.0-205, http://repo.hortonworks.com/content/groups/public/org/apache/spark/spark-graphx_2.11/), List(spark-hive-thriftserver_2.11, 2.1.1.2.6.2.0-205, http://repo.hortonworks.com/content/groups/public/org/apache/spark/...
> </pre>



More
-----------

> This script is the basis for a more advanced Spark Notebook Build which will build against any version of HDP and Spark. It could also be altered to test the default paths for other hadoop clusters to build against, and even include these repos by default. Alternatively it could also be used to build against any user specified path (of any type of Hadoop Cluster) against any user specified repo.



&emsp;
&emsp;
&emsp;
[Return to Simple Build][1]
{: style="text-align: center"}


[1]: https://gabechurch.github.io/Building-Spark-Projects-with-SBT/