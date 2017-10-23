

 Simple SBT Spark Build  
====================


You can copy the entire repository into your current directory using 
```
git clone https://github.com/GabeChurch/Building-Spark-Projects-with-SBT
```
OR the RECOMMENDED METHOD to only get this build is to use 
```
svn export https://github.com/GabeChurch/Building-Spark-Projects-with-SBT/Simple-SBT-Spark-Build

```

## Directory Contains
:open_file_folder: **Top**

&emsp; [:page_with_curl: **Build.sbt**](#build)
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

&emsp; [:open_file_folder: **project**](#project)

&emsp; [:open_file_folder: **src**](#src)

&emsp; &emsp; [:open_file_folder: **main**](#main)

&emsp; &emsp; &emsp; [:open_file_folder: **resources**](#resources)

&emsp; &emsp; &emsp; [:open_file_folder: **scala**](#scala)

&emsp; &emsp; &emsp; &emsp; :page_with_curl: **SparkBasics.scala**

```scala
package example

object Hello extends App {


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

&emsp; [:open_file_folder: **lib**](#lib)

----------




This build is utilizing the default maven resolvers to get the specified spark versions and their dependencies. It is setup to run only in local mode.


*If you are building spark on a hadoop cluster you will need a more advanced spark config, and you may need to add specific resolvers and version dependencies. *

&ensp;
&ensp;
&ensp;


&emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; [previous page][1] *Installing Spark and SBT* &ensp; &ensp; [next page](#none) *None* 

 [1]: https://github.com/GabeChurch/Building-Spark-Projects-with-SBT/blob/master/docs/Installing%20Spark%20and%20SBT.md