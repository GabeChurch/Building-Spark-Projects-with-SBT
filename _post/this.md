
Intro to SBT / Your First Spark Build In <a href="http://www.scala-sbt.org"><img src ="http://www.scala-sbt.org/release/docs/files/sbt-logo.svg" width="100" height="100" border="10" ></a>
==================


-------------

Basics
-----------

Like much of the hadoop ecosystem, Apache Spark was built in scala. Scala is a functional/object-oriented language founded by *Martin Odersky* which is built on/in **Java**. <i class="icon-coffee"></i>

This is important because both Java and Scala programs are compiled into jars, which form the basis of our Spark installations. **DEPENDENCIES**

These spark installations can vary quite widely among the different platforms which support and are capable of supporting spark. These platforms include:

* Hortonworks/HDP (Hadoop)
* Cloudera Hadoop 
* MapR
* Windows
* Mac
             .... and many more

Deployments differ programmatically beyond just Operating Systems. They differ in terms of versions, JVMs, and resource managers (yarn-managed, mesos-managed, docker deployments etc.)
They may have securities in place (kerberos authentication) and they may even differ in deployment strategy like standalone or cluster mode. 

-------------

Why use <a href="http://www.scala-sbt.org/release/docs/files/"><img src ="http://www.scala-sbt.org/release/docs/files/sbt-logo.svg" width="100" height="100" border="10" ></a>
-------------------

#### Understanding your deployment
--------------------------
Understanding how and why certain components work and rely on each other is crucial to success. SBT is the perfect choice for learning structure, management, and compilation. SBT is the perfect tool to assist in building or automate spark programs capable of more than just ETL.

#### Save time 
Sbt is a scala compiler that builds on the apache maven project and adds dependency management with apache ivy. It supports writing scala to identify dependencies (useful for making programs which can compile against many operating systems or versions) and is far and away the compilation tool of choice for spark.

#### Support 
Sbt is built into many of the major IDEs like IntelliJ and Eclipse. Sbt also supports reading project Maven poms and more. 

#### Community
Sbt is used by a large portion of the scala community and is overwhelmingly popular among spark developers.


---------------------


# Basic SBT Build Structure
-------------------------------------

<i class ="icon-folder-open"> </i> Top

practice  &nbsp; <space> hello
&emsp; Hello

$\qquad$[<i class ="icon-file"></i> Build.sbt](#build)

&emsp; trick

: [<i class="icon-folder-open"></i> project](#project)
: [<i class="icon-folder-open"></i> src](#src)
: - [<i class="icon-folder-open"></i> main](#main)
: - - [<i class="icon-folder-open"></i> resources](#resources)
: - - [<i class="icon-folder-open"></i> scala](#scala)
: - - - **<i class ="icon-file"></i> your_program.scala**
: [<i class="icon-folder-open"></i> lib](#lib)



:open_file_folder: **Top**

&emsp; [:page_with_curl: **Build.sbt**](#build)

&emsp; [:open_file_folder: **project**](#project)

&emsp; [:open_file_folder: **src**](#src)

&emsp; &emsp; [:open_file_folder: **main**](#main)

&emsp; &emsp; &emsp; [:open_file_folder: **resources**](#resources)

&emsp; &emsp; &emsp; [:open_file_folder: **scala**](#scala)

&emsp; &emsp; &emsp; &emsp; :page_with_curl: **your_program.scala**

&emsp; [:open_file_folder: **lib**](#lib)


----------







----------


#Build


Build <a href="http://www.scala-sbt.org/release/docs/files/"><img src ="http://www.scala-sbt.org/0.13/docs/files/typesafe_sbt_svg.svg" width="50" height="50" border="10" ></a>
---------------------------------
This is the main location for build specifications. Can contain a number of tools to use in the construction of a project however, the main purpose of the build.sbt is to specify and acquire dependencies as needed for a project. The dependencies indicated in the build.sbt are known as **managed dependencies**.

* All projects must contain a build definition, these typically specify only the most basic information needed.



### Option A: Build Definition *with Projects*
> The **build definition** is contained in the build.sbt, consists of a set of "projects" (which are often referred to as "sub-projects")



#### Sub-project

- configured by key-value pairs. 

$\qquad$$\qquad$ Ex : (where first pair is | *key* =name | *value*=Hello |)
```scala 
lazy val root = (project in file("."))
  .settings(
    name := "Hello",
    scalaVersion := "2.11.8"
  )
```
&ensp;
#### Two (main) Types of Keys
 Setting Keys
: Some setting which will be computed at project load time 
- $\qquad$  the previous sub-project example is a setting key value pair
&ensp;

Task Keys
: Some task, like compile or package, which may return a Unit (scala for void) or a value related to the task. The task will be computed each time the task is executed.

```scala
lazy val hello = taskKey[Unit]("An example task")
```
$\qquad$*//See official documentation for more on other keys/key types*
$\qquad$ [Here][1] .
&ensp;

#### Defining Tasks and Settings
* done using **:=**
* assigns a value to a setting 
* or assigns a computation to a task


To implement the hello *task* from the previous section:
``` scala
lazy val hello = taskKey[Unit]("An example task")

lazy val root = (project in file("."))
  .settings(
    hello := { println("Hello!") }
  )
```
We already saw an example defining *settings* when we defined the project’s name
``` scala
lazy val root = (project in file("."))
  .settings(
    name := "hello"
  )
``` 

### Option B: Build Definition *with bare .sbt build* (most common)
- alternative to defining Projects, very basic.
- consists of a list of Setting[_] expressions
``` scala
name := "hello"
version := "1.0"
scalaVersion := "2.11.8"
```
&ensp; 

>**Note**

> - ```scalaVersion``` needs to match your scala version


### Imports
* statements placed at the top of  build.sbt that allow us to include certain elements of our build at runtime
* The following are implicitly built in

```scala 
import sbt._
import Process._
import Keys._
```
&ensp;


### Adding a Library (managed Dependencies)

* These libraries, or dependencies,  are the backbone of your program.
* It is important that these dependencies match those contained in your specific deployment (unless you are running in a container environment such as docker)
* We can use unmanaged dependencies if we wish, see the [lib directory](#lib).

#### May Be Contained in Val(s)
```scala
val derby = "org.apache.derby" % "derby" % "10.4.1.3"

lazy val commonSettings = Seq(
  organization := "com.example",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.11.8"
)

lazy val root = (project in file("."))
  .settings(
    commonSettings,
    name := "Hello",
    libraryDependencies += derby
  )
```
&ensp;
#### Or May Simply be Defined
```scala
libraryDependencies += "org.apache.derby" % "derby" % "10.4.1.3"
```
You can add multiple Dependencies this way
```scala
libraryDependencies ++= Seq(
    "org.apache.derby" % "derby" % "10.4.1.3"
    "org.apache.spark" %% "spark-core" % sparkVersion
)
```
Notice in the above the **%%** after org.apache.spark, this passes the scala version to the spark-core. When it builds it will actually look for **spark-core_2.11** because we are running scala 2.11.8

Also notice the val **sparkVersion**, sbt can use vals set in the place of specific strings. We could set these in the project folder under dependencies.scala or in our main build.sbt.


### Resolvers

* Resolvers are repositories sbt uses to "resolve" managed dependencies. 
* Sbt is shipped with the main maven repository https://repo1.maven.org/maven2/

You can add resolvers in the same manner as dependencies 
```scala
resolvers += 
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
```


### Other Useful


You can add bash scripts to the build.sbt

```
lazy val execScript = taskKey[Unit]("Execute the shell script")

execScript := {
  "yourshell.sh" !
}
```
http://www.scala-sbt.org/sbt-native-packager/archetypes/java_app/customize.html


You can build a "fat-jar" or "assembly-jar" of your dependencies
See https://sparkour.urizone.net/recipes/building-sbt/

--------------

<i class ="icon-folder-open"></i> Project
---------------------
* This directory is "a build within your build", which knows how to build your build. Otherwise referred to as the **meta-build**
* NOT REQUIRED TO BUILD It is a support which can be thought of as an extension of the build.sbt
*  Commonly contains a file called **Dependencies.scala** which is a source file in the build definition. 
	* Can be any name ```any_name.scala```
	* Can also contain multiple .scala files 
* Can contain its own build.sbt and even another nested project directory!
	* For example you would define a sub-sbt (build sbt) for your project folder dependencies (Dependencies.scala) in the event that you need to utilize an import statement to call libraries outside of those shipped with scala and which you wish to utilize within your Dependencies.scala file. Obviously, you cannot access the libraries specified in your main build.sbt, within the file that builds dependencies for it, so it only makes sense to have the ability to load them at a lower level. This is one of many powerful features of sbt.

* The project directory contain other sbt build properties and can contain multiple .scala based build dependencies.

----------




<i class ="icon-folder-open"></i> Src
----------------------
* This folder contains the **main** and **test** folders
* It is very similar to the src folder in an Apache Maven Build.


###<i class ="icon-folder-open"></i> Main

* The main directory typically contains subfolders for your core application.


####<i class ="icon-folder-open"></i> Resources
* Contains other files which may be crucial to your program and which are included in the java classpath at runtime. 
* This folder is very useful for those building in apache spark with hive as it is the place to add any .xmls or configs (like a hive-site.xml needed for Spark-SQL hive support)


####<i class ="icon-folder-open"></i> Scala *(or Java)*
* This folder is the home for **<i class ="icon-file"></i> your_program.scala**


###<i class ="icon-folder-open"></i> Test
* This folder may contain test dependencies to run with sbt in a test mode.

-------------------




<i class ="icon-folder-open"></i> Lib
----------------------

> This is one of the most useful folders in the project, you can simply place your spark library/dependencies in this folder and your program will compile with them. These are **unmanaged dependencies** because sbt is not "building against them" using ivy or maven resolvers against repositories.

---------------------



#<line> Simple Spark SBT Build  
-------------------------------------

<i class ="icon-folder-open"> </i> Top
$\qquad$[<i class ="icon-file"></i> Build.sbt](#build)
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
<hide>
: [<i class="icon-folder-open"></i> project](#project)
: [<i class="icon-folder-open"></i> src](#src)
: - [<i class="icon-folder-open"></i> main](#main)
: - - [<i class="icon-folder-open"></i> resources](#resources)
: - - [<i class="icon-folder-open"></i> scala](#scala)
: - - - **<i class ="icon-file"></i> SparkBasic.scala**

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
<hide>
: [<i class="icon-folder-open"></i> lib](#lib)

----------


*If you are building spark on a hadoop cluster you will need a more advanced spark config, and you may need to add specific resolvers and version dependencies. *






[TOC]





 [1]: http://www.scala-sbt.org/release/docs/Basic-Def.html






