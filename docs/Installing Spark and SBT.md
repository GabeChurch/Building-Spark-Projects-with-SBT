---
title: Installing SBT
permalink: /InstallingSBT/
---

Installing  <a href="http://www.scala-sbt.org/release/docs/files/"><img src ="http://www.scala-sbt.org/assets/sbt-logo.svg" width="60" height="30" border="0" ></a>
==================


-------------
> Note: 
>In addition to the SBT installation you will need to have SVN (subversion) installed. You can check to see if your have this by running ```> svn help``` 
>To install see [Installing SVN][3].

Windows-installation 
------------------

Simply download the [msi installer](https://github.com/sbt/sbt/releases/download/v1.0.2/sbt-1.0.2.msi) and install.
> you will use from the command line

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

#### Debian-based distribution (Ubuntu etc)
Use the following, (assuming your package manager is aptitude) 
```bash
echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
sudo apt-get update
sudo apt-get install sbt
```
#### RPM-based distributions (Red Hat etc)
```bash
curl https://bintray.com/sbt/rpm/rpm | sudo tee /etc/yum.repos.d/bintray-sbt-rpm.repo
sudo yum install sbt
```

*For more help see the [sbt official linux installation instructions](http://www.scala-sbt.org/0.13/docs/Installing-sbt-on-Linux.html).*


------------




# Building a Sample SBT Scala Project

*For more on default templates see [sbt official tempates](http://www.scala-sbt.org/1.0/docs/sbt-new-and-Templates.html).*


```bash
$ sbt new scala/scala-seed.g8
....
name [hello]:

Template applied in ./hello

```



&ensp;
&ensp;
&ensp;

[previous page][1] *Intro to SBT* &ensp; &ensp; [next page][2] *Simple SBT Spark Build*
{: style="text-align: center"}
 



[1]: https://gabechurch.github.io/Building-Spark-Projects-with-SBT/IntrotoSBT/
[2]: https://gabechurch.github.io/Building-Spark-Projects-with-SBT/SimpleSBTSparkBuild/
[3]: https://gabechurch.github.io/Building-Spark-Projects-with-SBT/InstallingSVN/
