---
title: Installing SVN
permalink: /InstallingSVN/
---

Installing SVN
===========


## Linux 
### Ubuntu
```sh
sudo apt-get install svn
```

### CentOS
```sh
yum install svn
```

&emsp; - if you receive an unsigned package error one option is to rerun with:

```sh
yum install --nogpgcheck svn
```


## Mac 
### Using homebrew
You can install homebrew on you mac by following [these instructions](https://brew.sh/).

If you have homebrew installed on your mac simply run:
```sh
brew install subversion
```

### From binary package
> Follow instructions in link [here](http://support.beanstalkapp.com/article/816-installing-subversion-for-mac-os-x).




## Windows
### Command Prompt
You can install a command line SVN 
> Go to https://sliksvn.com/download/ and download the zip, then extract
> &emsp; - Select typical and install

You can also install a more complex SVN to use with file explorer
> https://tortoisesvn.net/downloads.html

### Xterm
If you use mobaXterm you can install svn directly in Xterm with
```sh
apt-get install subversion
```

----------------

&emsp;
&emsp;
&emsp;

[Back to Installing SBT][1] 
{: style="text-align: center"}


[1]: https://gabechurch.github.io/Building-Spark-Projects-with-SBT/InstallingSBT/