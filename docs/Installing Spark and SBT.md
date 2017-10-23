
Installing Spark and  <a href="http://www.scala-sbt.org"><img src ="http://www.scala-sbt.org/release/docs/files/sbt-logo.svg" width="100" height="100" border="10" ></a>
==================


-------------



Installing 
-----------

You will need both sbt and apache spark installed. 

First install apache spark. 

(Click on the links for instructions)
###Windows

####[Installing Apache Spark](http://www.eaiesb.com/blogs/?p=334)
####[Installing SBT](#windows-installation)


###Mac

####[Installing Apache Spark](https://medium.freecodecamp.org/installing-scala-and-apache-spark-on-mac-os-837ae57d283f)
####[Installing SBT](#mac-installation)


###Linux

####Installing Apache Spark
####[Installing SBT](#linux-installation)

--------


Build with Unmanaged Dependencies
------------
As discussed in the SBT basics document, this is the easiest option. 

It does have some drawbacks however, which should be noted. 



Build with Managed Dependencies
---------------

Like much of the hadoop ecosystem, Apache Spark was built in scala. Scala is a functional/object-oriented language founded by *Martin Odersky* which is built on/in **Java**. <i class="icon-coffee"></i>

This is important because both Java and Scala programs are compiled into jars, which form the basis of our Spark installations. **DEPENDENCIES**

These spark installations can vary quite widely among the different platforms which support and are capable of supporting spark. These platforms include:
>* Hortonworks HDP (Hadoop)
* Cloudera Hadoop 
* MapR
* Windows
* Mac













[TOC]
> Written with [StackEdit](https://stackedit.io/).



----------------------------------------------------------------
----------------------------------------------------------------
----------------------------------------------------------------
EXTRA
---------------------------------------------------------------------------
----------------------------------------------------------------------------------------

Windows-installation 
------------------

Simply download the [msi installer](https://github.com/sbt/sbt/releases/download/v1.0.2/sbt-1.0.2.msi) and install.

*For more help see [sbt official windows installation instructions](http://www.scala-sbt.org/release/docs/Installing-sbt-on-Windows.html). *

--------------

Mac-installation
-----------------
Download the [ZIP](https://github.com/sbt/sbt/releases/download/v1.0.2/sbt-1.0.2.zip) and expand it. 

*For more help see [sbt official mac installation instructions](http://www.scala-sbt.org/release/docs/Installing-sbt-on-Mac.html). *

---------------

Linux-installation
-----------------
Depending on your OS, you may use a few different commands to install. 

####Debian-based distribution (Ubuntu etc)
Use the following, (assuming your package manager is aptitude) 
```ubuntu
echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
sudo apt-get update
sudo apt-get install sbt
```
####RPM-based distributions (Red Hat etc)
```linux
curl https://bintray.com/sbt/rpm/rpm | sudo tee /etc/yum.repos.d/bintray-sbt-rpm.repo
sudo yum install sbt
```



#Build From Sample SBT File

http://www.scala-sbt.org/1.0/docs/sbt-new-and-Templates.html


```
$ sbt new scala/scala-seed.g8
....
name [hello]:

Template applied in ./hello

```


*For more help see the [sbt official linux installation instructions](http://www.scala-sbt.org/0.13/docs/Installing-sbt-on-Linux.html).*


------------


&ensp;
&ensp;
&ensp;

&emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp;[previous page][1] *Intro to SBT* &ensp; &ensp; [next page][2] *Simple SBT Spark Build*

 






 [1]: https://github.com/GabeChurch/Building-Spark-Projects-with-SBT/blob/master/docs/Intro%20to%20SBT.md
[2]: https://github.com/GabeChurch/Building-Spark-Projects-with-SBT/blob/master/docs/Simple%20SBT%20Spark%20Build.md

