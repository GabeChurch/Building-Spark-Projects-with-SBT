
Installing  <a href="http://www.scala-sbt.org/release/docs/files/"><img src ="http://www.scala-sbt.org/assets/sbt-logo.svg" width="60" height="30" border="0" ></a>
==================


-------------


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

#### Debian-based distribution (Ubuntu etc)
Use the following, (assuming your package manager is aptitude) 
```ubuntu
echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
sudo apt-get update
sudo apt-get install sbt
```
#### RPM-based distributions (Red Hat etc)
```linux
curl https://bintray.com/sbt/rpm/rpm | sudo tee /etc/yum.repos.d/bintray-sbt-rpm.repo
sudo yum install sbt
```

*For more help see the [sbt official linux installation instructions](http://www.scala-sbt.org/0.13/docs/Installing-sbt-on-Linux.html).*


------------




# Building a Sample SBT Scala Project

http://www.scala-sbt.org/1.0/docs/sbt-new-and-Templates.html


```
$ sbt new scala/scala-seed.g8
....
name [hello]:

Template applied in ./hello

```



&ensp;
&ensp;
&ensp;

&emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp;[previous page][1] *Intro to SBT* &ensp; &ensp; [next page][2] *Simple SBT Spark Build*

 






 [1]: https://github.com/GabeChurch/Building-Spark-Projects-with-SBT/blob/master/docs/Intro%20to%20SBT.md
[2]: https://github.com/GabeChurch/Building-Spark-Projects-with-SBT/blob/master/docs/Simple%20SBT%20Spark%20Build.md

