fitnesse-p4[![Build Status](https://travis-ci.org/AIM360/fitnesse-p4.png)](https://travis-ci.org/AIM360/fitnesse-p4)
===========

This is a simple plugin for [FitNesse](http://fitnesse.org) which provides a connection between the `FitNesse Wiki` and and a `Perforce` repository. This plugin has been tested with the `FitNesse` version `2013-05-30`.

Howto build
===========

```
cmake -H. -BBuild
cd Build
make
cp PerforceCmSystem.jar ..
cd ..
```

Copy the produced `PerforceCmSystem.jar` file into a `plugins` folder next to the `fitnesse-standalone.jar` of the `FitNesse` server you are using.

Howto use in FitNesse
=====================

Be sure that you have the `p4` executable in your search path. For me it worked best to open a command shell from within the `p4v` client directly from the workspace the `FitNesse` Wiki should be managed for and start the `FitNesse` server from this shell in order to get the current `P4` credentials correctly passed to the `p4` executable. 

And you need to place this line
```
!define CM_SYSTEM {fitnesse.wiki.cmSystems.PerforceCmSystem}
```
into a root page of your `FitNesse` Wiki so all files below this page gets automatically managed and added to `P4` changelists as soon as you start creating or modifying them.

[This](http://fitnesse.org/FitNesse.UserGuide.SourceCodeControl) page might also give you some more infos about the `CM` plugin mechanism in `FitNesse`.

Let us know, if you need more help to get it up and running, and we try to give more hints if possible.