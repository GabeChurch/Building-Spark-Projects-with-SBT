
---
title: Intro
---

Intro to <a href="http://www.scala-sbt.org"><img src ="http://www.scala-sbt.org/release/docs/files/sbt-logo.svg" height="30" width="60" border="0" ></a>
==================

Basics
-----------

Like much of the hadoop ecosystem, Apache Spark was built in scala. Scala is a functional/object-oriented language founded by *Martin Odersky* which is built on/in **Java**. <i class="icon-coffee"></i>

This is important because both Java and Scala programs are compiled into jars, which form the basis of our spark installations: **DEPENDENCIES**.

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

Why use <a href="http://www.scala-sbt.org/release/docs/files/"><img src ="http://www.scala-sbt.org/release/docs/files/sbt-logo.svg" width="60" height="30" border="0" ></a>
-------------------

#### Understanding your deployment
Understanding how and why certain components work and rely on each other is crucial to success. SBT is the perfect choice for learning structure, management, and compilation. SBT is also a fantastic tool to assist in building or automate spark programs capable of more than just ETL.

#### Save time 
Sbt is a scala compiler that builds on the apache maven project and adds dependency management with apache ivy. It supports writing scala to identify dependencies (useful for making programs which can compile against many operating systems or versions) and is far and away the compilation tool of choice for spark.

#### Support 
Sbt is built into many of the major IDEs like IntelliJ and Eclipse. Sbt also supports reading project Maven poms and more. 

#### Community
Sbt is used by a large portion of the scala community and is overwhelmingly popular among spark developers.


---------------------


# Build Structure
-------------------------------------

:open_file_folder: **Top**

&emsp; [:page_with_curl: **Build.sbt**](#page_with_curl-build-)

&emsp; [:open_file_folder: **project**](#open_file_folder-project)

&emsp; &emsp; :page_with_curl: **build.properties**

&emsp; [:open_file_folder: **src**](#open_file_folder-src)

&emsp; &emsp; [:open_file_folder: **main**](#open_file_folder-main)

&emsp; &emsp; &emsp; [:open_file_folder: **resources**](#open_file_folder-resources)

&emsp; &emsp; &emsp; [:open_file_folder: **scala**](#open_file_folder-scala-or-java)

&emsp; &emsp; &emsp; &emsp; :page_with_curl: **your_program.scala**

&emsp; [:open_file_folder: **lib**](#open_file_folder-lib)


----------


:page_with_curl: Build <a href="http://www.scala-sbt.org/release/docs/files/"><img src ="http://www.scala-sbt.org/0.13/docs/files/typesafe_sbt_svg.svg" width="30" height="14" border="0" ></a>
---------------------------------
This is the main location for build specifications. Can contain a number of tools to use in the construction of a project however, the main purpose of the build.sbt is to specify and acquire dependencies as needed for a project. The dependencies indicated in the build.sbt are known as **managed dependencies**.

* All projects must contain a build definition, these typically specify only the most basic information needed.



### Option A: Build Definition *with Projects*
> The **build definition** is contained in the build.sbt, consists of a set of "projects" (which are often referred to as "sub-projects")



#### Sub-project

- configured by key-value pairs. 

&emsp; &emsp; Ex : (where first pair is | *key* =name | *value*=Hello |)
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
- &emsp;  the previous sub-project example is a setting key value pair
&ensp;

Task Keys
: Some task, like compile or package, which may return a Unit (scala for void) or a value related to the task. The task will be computed each time the task is executed.

```scala
lazy val hello = taskKey[Unit]("An example task")
```
&emsp; *//See official documentation for more on other keys/key types*
&emsp; [Here][1] .
&ensp;

#### Defining Tasks and Settings
* use **:=**
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
>  ```scalaVersion``` needs to match your scala version


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
* It is important that these dependencies match those contained in your specific deployment (unless you are running in a container environment such as docker).
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
You can add multiple Dependencies in this way
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

You can add resolvers in a similar manner to dependencies. 
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

:open_file_folder: Project
---------------------
* This directory is "a build within your build", which knows how to build your build. Otherwise referred to as the **meta-build**

* This folder is the home for :page_with_curl: **build.properties** which contains the sbt version information. I have found version 0.13.16 to be the most stable for spark builds.

*  Commonly contains a file called **Dependencies.scala** which is a source file in the build definition. 
	* Can be any name ```any_name.scala```
	* Can also contain multiple .scala files 
* Can contain its own build.sbt and even another nested project directory!
	* For example you would define a sub-sbt (build sbt) for your project folder dependencies (Dependencies.scala) in the event that you need to utilize an import statement to call libraries outside of those shipped with scala and which you wish to utilize within your Dependencies.scala file. Obviously, you cannot access the libraries specified in your main build.sbt, within the file that builds dependencies for it, so it only makes sense to have the ability to load them at a lower level. This is one of many powerful features of sbt.

* The project directory contain other sbt build properties and can contain multiple .scala based build dependencies.

----------




:open_file_folder: Src
----------------------
* This folder contains the **main** and **test** folders
* It is very similar to the src folder in an Apache Maven Build.


### :open_file_folder: Main

* The main directory typically contains subfolders for your core application.


#### :open_file_folder: Resources
* Contains other files which may be crucial to your program and which are included in the java classpath at runtime. 
* This folder is very useful for those building in apache spark with hive as it is the place to add any .xmls or configs (like a hive-site.xml needed for Spark-SQL hive support)


#### :open_file_folder: Scala *(or Java)*
* This folder is the home for :page_with_curl: **your_program.scala**


### :open_file_folder: Test

* This folder may contain test dependencies to run with sbt in a test mode.

-------------------




:open_file_folder: Lib
----------------------

> This is one of the most useful folders in the project, you can simply place your spark library/dependencies in this folder and your program will compile with them. These are **unmanaged dependencies** because sbt is not "building against them" using ivy or maven resolvers against repositories.

---------------------


&emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp;[previous page](#none) *None* &ensp; &ensp; [next page][2] *Installing Spark and SBT*

 






 [1]: http://www.scala-sbt.org/release/docs/Basic-Def.html
[2]: https://github.com/GabeChurch/Building-Spark-Projects-with-SBT/blob/master/docs/Installing%20Spark%20and%20SBT.md





