---
title: Simple SBT Spark Build
permalink: /SimpleSBTSparkBuild/
---



 Simple <a href="http://www.scala-sbt.org/release/docs/files/"><img src ="http://www.scala-sbt.org/assets/sbt-logo.svg" width="60" height="30" border="0" ></a> Spark Build  
====================

The first step to any sbt spark build is to identify a method (or identify the required method) of project dependency management. 

## Building with Managed Dependencies

**If you do not have any installed version of spark this will be the method you will use.** We discussed managed dependencies in the [Intro to SBT][3]. As a refresher, you specify these dependencies in your build.sbt file and acquire them from the default maven (or custom) repo(s) by adding library dependencies with the target version of spark. You may need different dependencies according to your build goals. See the managed dependency build.sbt

To download the sbt spark example for managed dependencies use:
```
svn export https://github.com/GabeChurch/Building-Spark-Projects-with-SBT/tree/master/builds/SimpleSBTSpark_ManagedDeps

```
This build is utilizing the default maven resolvers to get the specified spark versions and their dependencies. It is setup to run only in local mode.

*If you are building spark on a hadoop cluster you will need a more advanced spark config, and you may need to add specific resolvers and version dependencies. *

### Managed Dependency Build
![open file icon](https://cdn2.iconfinder.com/data/icons/snipicons/5000/folder-open-24.png) **Top**
<details>
	<summary> &emsp; <img src="https://cdn2.iconfinder.com/data/icons/snipicons/500/file-24.png" alt="pic" /><b> Build.sbt </b> </summary>
	<div markdown="1">
```scala

name := "Hello"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-core" % "2.2.0",
    "org.apache.spark" %% "spark-sql" % "2.2.0",
    "org.apache.commons" % "commons-csv" % "1.2"
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

&emsp; ![open file icon](https://cdn2.iconfinder.com/data/icons/snipicons/5000/folder-open-24.png) **src**

&emsp; &emsp; ![open file icon](https://cdn2.iconfinder.com/data/icons/snipicons/5000/folder-open-24.png) **main**

&emsp; &emsp; &emsp; ![closed file icon](https://cdn2.iconfinder.com/data/icons/snipicons/5000/folder-close-24.png) **resources**

&emsp; &emsp; &emsp; ![open file icon](https://cdn2.iconfinder.com/data/icons/snipicons/5000/folder-open-24.png) **scala**
<details>
	<summary> &emsp; &emsp; &emsp;&emsp; <img src="https://cdn2.iconfinder.com/data/icons/snipicons/500/file-24.png" alt="pic" /><b> SparkExample.scala </b> </summary>
	<div markdown="1">
```scala
package SparkExample
object entirety extends App {

import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql._
import org.apache.spark._
import org.apache.spark.sql.functions._

//Creating A SparkSession
val spark = SparkSession.builder()
  .master("local")
  .appName("my-spark-app")
  .getOrCreate()

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

spark.stop()
}
```
</div>
</details>

&emsp; ![closed file icon](https://cdn2.iconfinder.com/data/icons/snipicons/5000/folder-close-24.png) **lib**

----------


## Building with Unmanaged Dependencies

**If you already have an installed version of spark can be the method to use if you wish to build against your current version of spark or use spark on your cluster (in yarn-client mode, with hive, etc)** As discussed in the [Intro to SBT][3], this is generally the easiest option for running in cluster mode. It does have some drawbacks in terms of build clarity. It may make more complex projects difficult or impossible to emulate or share with others. 

To download the sbt spark example for unmanaged dependencies use:
```
svn export https://github.com/GabeChurch/Building-Spark-Projects-with-SBT/builds/SimpleSBTSpark_UnmanagedDeps
```

You will also need to copy the entire contents of the jars from ```path_to/your_spark_version/jars``` to ```path_to_this_build/build/SimpleSBTSpark_UnmanagedDeps/libs ```

*You can also clone the entire repository into your current directory using*
```
git clone https://github.com/GabeChurch/Building-Spark-Projects-with-SBT
```

### Unmanaged Dependency Build
![open file icon](https://cdn2.iconfinder.com/data/icons/snipicons/5000/folder-open-24.png) **Top**
<details>
	<summary> &emsp; <img src="https://cdn2.iconfinder.com/data/icons/snipicons/500/file-24.png" alt="pic" /><b> Build.sbt </b> </summary>
	<div markdown="1">
```scala

name := "Hello"

version := "1.0"

scalaVersion := "2.11.8"
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

&emsp; ![open file icon](https://cdn2.iconfinder.com/data/icons/snipicons/5000/folder-open-24.png) **src**

&emsp; &emsp; ![open file icon](https://cdn2.iconfinder.com/data/icons/snipicons/5000/folder-open-24.png) **main**

&emsp; &emsp; &emsp; ![closed file icon](https://cdn2.iconfinder.com/data/icons/snipicons/5000/folder-close-24.png) **resources**
> Put any of your .xml spark or hive configs files here. 

&emsp; &emsp; &emsp; ![open file icon](https://cdn2.iconfinder.com/data/icons/snipicons/5000/folder-open-24.png) **scala**
<details>
	<summary> &emsp; &emsp; &emsp;&emsp; <img src="https://cdn2.iconfinder.com/data/icons/snipicons/500/file-24.png" alt="pic" /><b> SparkExample.scala </b> </summary>
	<div markdown="1">
```scala
package SparkExample

object Entirety extends App {

import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql._
import org.apache.spark._
import org.apache.spark.sql.functions._

//enable various commented out config options for hadoop clusters with hive

val spark = SparkSession.builder()
//  .master("yarn-client")
  .master("local")
  .appName("my-spark-app")
 // .config("spark.warehouse.dir", "hdfs//your_node:your_hdfs_port/apps/hive/warehouse")
//  .config("spark.sql.warehouse.dir", "hdfs//your_node:your_hdfs_port/apps/hive/warehouse")
//  .config("spark.sql.hive.metastore.version", "1.2.1")
//  .config("spark.sql.hive.metastore.jars", "/builtin")
//  .config("spark.sql.hive.metastore.jars", "/path/to/your/hive/lib")
//  .config("spark.yarn.archive", "hdfs//your_node:your_hdfs_port/apps/spark2/spark2-hdp-yarn-archive.tar.gz")
//  .config("spark.yarn.stagingDir", "hdfs//your_node:your_hdfs_port/tmp/")
//  .config("spark.driver.extraLibraryPath", "/yourhadoop/lib/paths")
  .config("spark.dynamicAllocation.enabled", "true")
  .config("spark.dynamicAllocation.initialExecutors", "0")
  .config("spark.dynamicAllocation.maxExecutors", "20")
  .config("spark.dynamicAllocation.minExecutors", "0")
//  .config("spark.executor.extraLibraryPath", "/yourhadoop/lib/paths")
//  .config("spark.executor.id", "driver")
// add for hdp hortonworks //.config("spark.driver.extraJavaOptions", "-Dhdp.version=2.6.0.3-8")
// add for hdp hortonworks //.config("spark.yarn.am.extraJavaOptions", "-Dhdp.version=2.6.0.3-8")
//  .config("spark.home", "/path/to/your/spark/home/spark2")
//  .config("spark.jars", "/usr/hdp/current/spark2-client/jars")
//  .config("spark.submit.deployMode", "client")
//  .config("spark.shuffle.service.enabled", "true")
//  .config("spark.yarn.queue", "default")
//  .config("spark.hadoop.yarn.resourcemanager.address", "local:8050")
//  .config("hive.metastore.warehouse.dir", "hdfs//your_node:your_hdfs_port/apps/hive/warehouse")
//.enableHiveSupport()
  .getOrCreate()



//test for hive table support spark.sql("show tables").show


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

spark.stop()
}

```
</div>
</details>

<details>
	<summary> &emsp; <img src="https://cdn2.iconfinder.com/data/icons/snipicons/5000/folder-open-24.png" alt="pic" /><b> lib </b> </summary>
	<div markdown="1">
		> You need to place your dependencies here. Find your spark home and copy the contents of the jars folder to this location

</div>
	</details>

--------

##Running
To run the sbt build navigate to the main directory (which contains the build.sbt), and type
```
user$ sbt
```
Then  
```
user$ > run
```

##Closing
You can exit the build with

```
user$ > exit
```






&ensp;
&ensp;
&ensp;


&emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; [previous page][1] *Installing SBT* &ensp; &ensp; [next page](#none) *None* 

 [1]: https://gabechurch.github.io/Building-Spark-Projects-with-SBT/InstallingSBT/

[3]:https://github.com/GabeChurch/Building-Spark-Projects-with-SBT/blob/master/docs/Intro%20to%20SBT.md