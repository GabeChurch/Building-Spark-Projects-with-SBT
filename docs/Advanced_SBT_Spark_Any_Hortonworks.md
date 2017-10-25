---
title: Advanced SBT Spark Build 
permalink: /AdvBuildHDPany/
---

Building an Advanced SBT Application to Build Spark Against Any Hortonworks Hadoop Deployment
==============

About
-----

This advanced SBT application reads the system defaults for any Hortonworks Hadoop installation based on the default HDP path of /usr/hdp (if hdp path is different than default it will need to be changed in this build).

It reads the native spark jars on your installation matching those for both hadoop and spark in the hortonworks repo (which it parses) and gives specific version information, an identifier used, as well as a specific repo url for each jar. You can have multiple versions of both hdp and spark and it will read the larger version for both.

As of right now I have not written a method for recursively adding a list of libraryDependencies that have been identified, which is why the build.sbt currently uses the base required spark library dependencies with sparkVersion and hadoopVersion (that have been found using [Dependencies.scala][2] to resolve. 

The reason for using specific librarydependencies and resolvers (and not simply telling the build to copy any native jars found) is because a more complicated program that needs to share build information and may have version overlaps will need to use sbt functions based on resolver information to solve for overlaps and (possibly) share between multiple builds. 

------------

Future
-------
I am currently in the process of utilizing various techniques identified in this build to rewrite the [spark-notebook build from source][3] to allow it to be easily implemented at the production level for Hortonworks Hadoop Clusters and others.

Exploring the Advanced Build
-------------------

![open file icon](https://cdn2.iconfinder.com/data/icons/snipicons/5000/folder-open-24.png) **Top**
<details>
	<summary> &emsp; <img src="https://cdn2.iconfinder.com/data/icons/snipicons/500/file-24.png" alt="pic" /><b> Build.sbt </b> </summary>
	<div markdown="1">
	>Using the information passed from [Dependencies.scala][2] (the sparkVersion and hadoopVersion) this build will use the hortonworks repo to get the dependencies needed to run spark. 
```scala

import Dependencies._

organization := "com.gabechurch"

//so apparently the name is what loads our scala file?
name := "Practie Hortonworks repo"

scalaVersion := "2.11.8"

version      := "0.1.0-SNAPSHOT"


resolvers +="Hortonworks Releases" at "http://repo.hortonworks.com/content/groups/public"
   
libraryDependencies ++= Seq(
	"org.apache.spark" %% "spark-core" % sparkVersion,
	"org.apache.spark" %% "spark-yarn" % sparkVersion,
	"org.apache.spark" %% "spark-hive" % sparkVersion,
	"org.apache.spark" %% "spark-repl" % sparkVersion,
	"org.apache.spark" %% "spark-sql" % sparkVersion,
	"org.apache.hadoop" % "hadoop-client" % hadoopVersion,
	"org.apache.hadoop" % "hadoop-yarn-server-web-proxy" % hadoopVersion
)	

```

</div>
</details>

&emsp; ![open file icon](https://cdn2.iconfinder.com/data/icons/snipicons/5000/folder-open-24.png) **project**

<details>
	<summary> &emsp; &emsp; <img src="https://cdn2.iconfinder.com/data/icons/snipicons/500/file-24.png" alt="pic" /><b> build.properties </b> </summary>
	<div markdown="1">
```scala
sbt.version=0.13.16
```
</div>
</details>

<details>
	<summary> &emsp; &emsp; <img src="https://cdn2.iconfinder.com/data/icons/snipicons/500/file-24.png" alt="pic" /><b> Dependencies.sbt </b> </summary>
	<div markdown="1">
> This is a sub-sbt (nested build) as explained in the [Intro to SBT][1] project. This build.sbt is needed to load a non-native scala library to use in Dependencies.scala. It is using the default maven resolver (default repo) to find the library
```
libraryDependencies +=  "commons-io" % "commons-io" % "2.5"
```

</div>
</details>

<details>
	<summary> &emsp; &emsp; <img src="https://cdn2.iconfinder.com/data/icons/snipicons/500/file-24.png" alt="pic" /><b> Dependencies.scala </b> </summary>
	<div markdown="1">
> This contains the meat of the program. It runs before the build.sbt and allows us to use scala to gather dependencies for our build.
> 
> [CLICK HERE][2] to view the program with output (showing variables generated)
</div>
</details>

&emsp; ![open file icon](https://cdn2.iconfinder.com/data/icons/snipicons/5000/folder-open-24.png) **src**

&emsp; &emsp; ![open file icon](https://cdn2.iconfinder.com/data/icons/snipicons/5000/folder-open-24.png) **main**
<details>
	<summary>&emsp; &emsp; &emsp; <img src="https://cdn2.iconfinder.com/data/icons/snipicons/5000/folder-open-24.png" alt="pic" /><b> resources </b> </summary>
	<div markdown="1">
>The Dependencies.scala program will place the hive-site.xml here if found.
</div>
</details>

&emsp; &emsp; &emsp; ![open file icon](https://cdn2.iconfinder.com/data/icons/snipicons/5000/folder-open-24.png) **scala**
<details>
	<summary> &emsp; &emsp; &emsp;&emsp; <img src="https://cdn2.iconfinder.com/data/icons/snipicons/500/file-24.png" alt="pic" /><b> SparkExample.scala </b> </summary>
	<div markdown="1">
```scala
package example
object Entirety extends App {

import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql._
import org.apache.spark._
import org.apache.spark.sql.functions._

val spark = SparkSession.builder()
  .master("yarn-client")
  .appName("my-spark-app")
  .config("spark.yarn.archive", "hdfs://busprddtahin01.cc.ku.edu:8020/hdp/apps/2.6.0.3-8/spark2/spark2-hdp-yarn-archive.tar.gz")
  .config("spark.yarn.stagingDir", "hdfs://busprddtahin01.cc.ku.edu:8020/tmp/")
  .config("spark.driver.extraLibraryPath", "/usr/hdp/current/hadoop-client/lib/native:/usr/hdp/current/hadoop-client/lib/native/Linux-amd64-64")
  .config("spark.dynamicAllocation.enabled", "true")
  .config("spark.dynamicAllocation.initialExecutors", "0")
  .config("spark.dynamicAllocation.maxExecutors", "20")
  .config("spark.dynamicAllocation.minExecutors", "0")
  .config("spark.executor.extraLibraryPath", "/usr/hdp/current/hadoop-client/lib/native:/usr/hdp/current/hadoop-client/lib/native/Linux-amd64-64")
  .config("spark.driver.extraJavaOptions", "-Dhdp.version=2.6.0.3-8") 
  .config("spark.yarn.am.extraJavaOptions", "-Dhdp.version=2.6.0.3-8")
  .config("spark.home", "/usr/hdp/2.6.0.3-8/spark2")
  .config("spark.shuffle.service.enabled", "true")
  .config("spark.yarn.queue", "default")
  .config("spark.hadoop.yarn.resourcemanager.address", "10.101.232.97:8050")
  .enableHiveSupport()
  .getOrCreate()

spark.sql("show tables").show

     //create a Dataset using spark.range starting from 5 to 100, with increments of 5
val numDs = spark.range(5, 100, 5)
     // reverse the order and display first 5 items
numDs.orderBy(desc("id")).show(5)
    //compute descriptive stats and display them
numDs.describe().show()
    // create a DataFrame using spark.createDataFrame from a List or Seq
val langPercentDF = spark.createDataFrame(List(("Scala", 35), ("Python", 30), ("R", 15), ("Java", 20)))
    //rename the columns
val lpDF = langPercentDF.withColumnRenamed("_1", "language").withColumnRenamed("_2", "percent")
   //order the DataFrame in descending order of percentage
lpDF.orderBy(desc("percent")).show(false)

}

```

</div>
</details>

&emsp; ![closed file icon](https://cdn2.iconfinder.com/data/icons/snipicons/5000/folder-close-24.png) **lib**










[1]: https://gabechurch.github.io/Building-Spark-Projects-with-SBT/IntrotoSBT/#-project
[2]: https://gabechurch.github.io/Building-Spark-Projects-with-SBT/AdvBuildDeps/
[3]: https://github.com/spark-notebook/spark-notebook


