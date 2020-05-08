# NetworkCalculus.org DNC

Deterministic Network Calculus is a methodology for worst-case modeling and analysis of communication networks. It enables to derive deterministic bounds on a server’s backlog as well as a flow’s end-to-end delay. Given a directed graph of servers (server graph) and the flows crossing these servers, the Deterministic Network Calculator (DNC) automates the derivation of bounds.

### Academic Attribution

The NetworkCalculus.org DNC was derived from the the [Disco Deterministic Network Calculator (DiscoDNC)](disco.networkcalculus.org). If you use it for research, please include the following reference in any resulting publication:

```plain
@inproceedings{DiscoDNCv2,
  author    = {Steffen Bondorf and Jens B. Schmitt},
  title     = {The {DiscoDNC} v2 -- A Comprehensive Tool for Deterministic Network Calculus},
  booktitle = {Proc. of the International Conference on Performance Evaluation Methodologies and Tools},
  series    = {ValueTools '14},
  pages     = {44--49},
  month     = {December},
  year      = 2014,
  url       = {https://dl.acm.org/citation.cfm?id=2747659}
}
```

# Submodule Structure

The DNC consists of 4 parts, that are located in 4 different repositories:
 
* The core code in `src/main` is contained in this repository, the other parts ones can be included as git submodules. 
* The MPA RTC wrapper extension in `src/mpa_ext` is located in [DNCext\_MPARTC](https://github.com/NetCal/DNCext_MPARTC), 
* the test files in `src/functional_tests` are in [DNC\_func\_tests](https://github.com/NetCal/DNC_func_tests) and 
* the experiment in `src/experiments` are in [DNC\_experiments](https://github.com/NetCal/DNC_experiments). 

In order to get all submodules, use the command `git submodule update --init --recursive`.

# Development with Eclipse

This small guide assumes you have installed Eclipse 2020-03 (4.15.0), a Java 13 JDK (preferably OpenJDK) and a git command line client.

## Setting up the Project in Eclipse
* Navigate to you Eclipse workspace directory, clone the DNC repository to it and pull the submodules.

* In Eclipse, navigate to `File > New > Java Project`, use the name `DNC` and click `Finish` (you do not need to create a module-info in the next dialog). This creates a `DNC` project in your workbench.

* Right-click on `DNC` and select `Configure > Convert to Maven Project`. Your workspace will be rebuilt accordingly.

## Add and Use Profiles
* Right-click on the project `DNC`and select `Properties`. In the new window, navigate to `Maven` and add `eclipse,tests,exp,mpa` to your Active Maven Profiles (comma separated). Click `Apply and Close` and confirm that your `DNC` project will be updated.

## Get the rtc.jar to run the MPA RTC Curve Backend and the Tests 
First, make sure you are connected to the Internet and that you have a ``lib`` folder in your ``DNC`` project. You can then get the dependency in different ways:

* Using Maven (in gerneral): Run a build with goal 'validate'.

* Using Eclipse's Maven integration: 
  - Right-click on the project `DNC` and select `Run As > Maven build...`. In the new window, add `validate` to the (empty) Goals-field and then click `Run`.
  - Right-click on the project `DNC` and select `Maven > Update Project...`. In the new window, check that `DNC` is selected and click `OK`.

* Manual install: Download the file from http://www.mpa.ethz.ch/static/download.php?file=RTCToolbox_bin.zip and unpack it in the `DNC` project's /lib folder. 

## Maven Lifecycle Mapping Error caused by pom.xml
In your `DNC` project, dougle-click on `pom.xml`. This opens the `pom` editor view. Select its `Overview` tab. In the title, you will get (an abbreviation of) this error:

> Plugin execution not covered by lifecycle configuration: com.googlecode.maven-download-plugin:download-maven-plugin:1.4.1:wget (execution: get-mpa-rtc, phase: validate)

As we just used the `wget` plugin in the `validate` phase to get the `rtc.jar` dependency, it is safe to ignore. Clicking on it, there are two alternatives to ignore the error:

* Mark goal wget as ignored in pom.xml
* Mark goal wget as ignored in eclipse preferences

Choose the second one (globally ignore error cause) to prevent changes to the `pom.xml` file.

## Functional Tests

You can run the functional tests to check if you installation succeeded. We use the Eclipse Maven plugin to run the JUnit tests.

Right-click on the project `DNC` and select `Run As > Maven test`. The Eclipse console will show outputs like this:

> [INFO] Running org.networkcalculus.dnc.func\_tests.S\_1SC\_2F\_2AC\_Test </br>
> [INFO] Tests run: 6272, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 4.498 s - in org.networkcalculus.dnc.func_tests.S\_1SC\_2F\_2AC\_Test

# Compile jars with Maven

Use the following maven profiles for compiling jars the different parts of the DNC:

* `mvn package` - builds the base code in `src/main`
* `mvn package -P mpa` - builds the base code and the MPA RTC wrappers in `src/mpa_ext`
* `mvn package -P exp` - builds the base code and the experiment classes in `src/experiments`
* `mvn package -P tests,mpa` - builds an additional jar for the test classes, note that you also need the `mpa` profile, since the classes are needed for running the tests
* The profiles can also be combined as needed
