# DiscoDNC

Deterministic Network Calculus (DNC) is a methodology for worst-case modeling and analysis of communication networks. It enables to derive deterministic bounds on a server’s backlog as well as a flow’s end-to-end delay. Given a directed graph of servers (server graph) and the flows crossing these servers, the Disco Deterministic Network Calculator (DiscoDNC) automates the derivation of bounds.

### Academic Attribution

If you use the Disco Deterministic Network Calculator for research, please include the following reference in any resulting publication:

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

The DiscoDNC consists of 4 parts, that are located in 4 different repositories:<br /> 
* The core code in `src/main` is contained in this repository, the other parts ones can be included as git submodules. 
* The MPA RTC wrapper extension in `src/mpa_ext` is located in [DiscoDNCext_MPARTC](https://github.com/NetCal/DiscoDNCext_MPARTC), 
* the test files in `src/test` are in [DiscoDNC_tests](https://github.com/NetCal/DiscoDNC_tests) and 
* the experiment in `src/experiments` are in [DiscoDNC_experiments](https://github.com/NetCal/DiscoDNC_experiments). 

In order to checkout all submodules, use the command `git submodule update --init --recursive`.

# Development with Eclipse
This small guide assumes you cloned the DiscoDNC repository, executed the above commands to pull the submodules, and created an Eclipse project from the code.
You might also be able to do all this from Eclipse using its EGit plugin.

These steps were tested with Eclipse Photon only.

## Add Profiles
Go to the project properties > Maven and add "eclipse,tests,exp,mpa" (no quotes) to your active profiles.

## Maven Lifecycle Mapping Error (pom.xml Error)
Initially, the error console will show this error:<br />
> Plugin execution not covered by lifecycle configuration: org.codehaus.mojo:build-helper-maven-plugin:1.7:add-source (execution: add-source, phase: generate-sources) Maven Project Build Lifecycle Mapping Problem

Additionally, it will be shown as an error of our pom.xml build file.
To resolve it, got to Eclipse's the pom.xml view, Overview tab where you will find 
> Plugin execution not covered by lifecycle configuration: org.codehaus.mojo.build (Click for details)

above the Overview caption.
On click, there are suggestions. Two offerings are to ignore this error.
* Mark goal add-source as ignored in pom.xml
* Mark goal add-source as ignored in eclipse preferences
Choose the second on (globally ignore error cause) to prevent changes to the pom.xml file.

## Add Source Folders
Select "eclipse" maven profile in eclipse to import source folders automatically.

## Tests
* In the above dialog to add source folders, change "Contains test sources:" of src/functional_test/java to Yes.
* You need to change the output folder, e.g., create `target/func-test-classes` for this purpose.
* To run a test from within Eclipse, you need to add this folder to the test's classpath. Otherwise, it will break with a "class not found" exception. Go to Run Configurations ..., select your test > Classpath > select User Entries > click Advanced > Add Folder to do so.   

# Compile jars with Maven

Use the following maven profiles for compiling jars the different parts of the DiscoDNC

* `mvn package` - builds the base code in `src/main`
* `mvn package -P mpa` - builds the base code and the MPARTC wrappers in `src/mpa_ext`
* `mvn package -P ext` - builds the base code and the experiment classes in `src/experiments`
* `mvn package -P tests,mpa` - builds an additional jar for the test classes, note that you also need the `mpa` profile, since the classes are needed for running the tests
* the profiles can also be combined as needed
