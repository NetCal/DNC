# NetworkCalculus.org DNC

Deterministic Network Calculus is a methodology for worst-case modeling and analysis of communication networks. It enables to derive deterministic bounds on a server’s backlog as well as a flow’s end-to-end delay. Given a directed graph of servers (server graph) and the flows crossing these servers, the Deterministic Network Calculator (DNC) automates the derivation of bounds.

### Academic Attribution

The NetworkCalculus.org DNC was derived from the the Disco Deterministic Network Calculator (DiscoDNC). If you use it for research, please include the following reference in any resulting publication:

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

In order to checkout all submodules, use the command `git submodule update --init --recursive`.

# Development with Eclipse
This small guide assumes you cloned the DNC repository, executed the above commands to pull the submodules, and created an Eclipse project from the code.
You might also be able to do all this from Eclipse using its EGit plugin.

These steps were initially tested with Eclipse Photon. Some steps have later been adapted to work with Eclipse 2018-09.

## Add Profiles
Go to the project properties > Maven and add "eclipse,tests,exp,mpa" (no quotes) to your active profiles.

## Maven Lifecycle Mapping Error (pom.xml Errors)
Initially, the error console will show these errors:<br />
> Plugin execution not covered by lifecycle configuration: org.codehaus.mojo:build-helper-maven-plugin:1.7:add-source (execution: add-source, phase: generate-sources)
>
> Plugin execution not covered by lifecycle configuration: com.googlecode.maven-download-plugin:download-maven-plugin:1.4.1:wget (execution: get-mpa-rtc, phase: validate)

Additionally, the according lines in the pom.xml will be marked to contain errors.
To resolve this, got to Eclipse's the pom.xml view, Overview tab where you will find 
> Plugin execution not covered by lifecycle configuration: org.codehaus.mojo.build (Click for details)
>
> Plugin execution not covered by lifecycle configuration: com.googlecode.maven-do (Click for details)

above the Overview caption.
On click, there are two suggestions to ignore this error:

* Mark goal add-source (or wget) as ignored in pom.xml
* Mark goal add-source (or wget) as ignored in eclipse preferences

Choose the second on (globally ignore error cause) to prevent changes to the `pom.xml` file.

## Add Source Folders
Select "eclipse" Maven profile in eclipse to import source folders automatically.

## Get the rtc.jar to run the MPA RTC Curve Backend and the Tests 
Useing Maven: Run a build with goal 'validate'.

Manually: Download the file from http://www.mpa.ethz.ch/static/download.php?file=RTCToolbox_bin.zip and unpack it in the /lib folder. 

## Functional Tests
* In the above dialog to add source folders, change "Contains test sources:" of src/functional_test/java to Yes.
* You need to change the output folder, e.g., create `target/func-test-classes` for this purpose.
* To run a test from within Eclipse, you need to add this folder to the test's classpath. Otherwise, it will break with a "class not found" exception.
  * Eclipse **Photon**:  Navigate to Run Configurations ... > your test > Classpath > select User Entries > click Advanced > Add Folder to do so.
  * Eclipse **2018-09**: Navigate to Run Configurations ... > your test > Dependencies > select Classpath Entries > click Advanced > Add Folder 

# Compile jars with Maven

Use the following maven profiles for compiling jars the different parts of the DNC:

* `mvn package` - builds the base code in `src/main`
* `mvn package -P mpa` - builds the base code and the MPA RTC wrappers in `src/mpa_ext`
* `mvn package -P exp` - builds the base code and the experiment classes in `src/experiments`
* `mvn package -P tests,mpa` - builds an additional jar for the test classes, note that you also need the `mpa` profile, since the classes are needed for running the tests
* The profiles can also be combined as needed
