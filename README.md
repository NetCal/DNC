# NetworkCalculus.org DNC (NCorg DNC)

Deterministic Network Calculus is a methodology for worst-case modeling and analysis of communication networks. It
enables to derive deterministic bounds on a server’s backlog as well as a flow’s end-to-end delay. Given a directed
graph of servers (server graph) and the flows crossing these servers, the Deterministic Network Calculator (DNC)
automates the derivation of bounds.

### Academic Attribution

If you use the NetworkCalculus.org DNC (NCorg DNC) for a research publication, please include one of the following references

- NCorg DNC in general, and arbitrary multiplexing in particular:

```plain
@inproceedings{NCorg-DNC,
  author    = {Wlad Pesotsky and Eric Hermsen and Steffen Bondorf},
  title     = {Automated and Precise Deterministic NetCal Calculations from Models to Bounds},
  booktitle = {Proc. of the 38th European Conference on Real-Time Systems},
  series    = {ECRTS '26},
  month     = {July},
  year      = 2026
}
```

- LUDB as FIFO multiplexing analysis:

```plain
@inproceedings{LUDBFF,
  author    = {Alexander Scheffler and Steffen Bondorf},
  title     = {Network Calculus for Bounding Delays in Feedforward Networks of {FIFO} Queueing Systems},
  booktitle = {Proc. of the 18th International Conference on Quantitative Evaluation of Systems},
  series    = {QEST '21},
  pages     = {149--167},
  month     = {August},
  year      = 2021,
  url       = {https://link.springer.com/chapter/10.1007/978-3-030-85172-9_8}
}
```

The NCorg DNC was derived from the [DISCO](https://disco.cs.rptu.de) (Deterministic) Network Calculator, aka DiscoDNC.
See [here for v1](https://dl.acm.org/doi/10.1145/1190095.1190105) and [here for v2](https://dl.acm.org/doi/10.4108/icst.Valuetools.2014.258167).

# Submodule Structure

The NCorg DNC consists of 3 parts, that are located in 3 different repositories:

* The core code in `src/main` is contained in this repository, the other parts ones can be included as git submodules.
* (optional) the test files in `src/functional_tests` are in [DNC\_func\_tests](https://github.com/NetCal/DNC_func_tests) and
* (optional) the experiment in `src/experiments` are in [DNC\_experiments](https://github.com/NetCal/DNC_experiments).

In order to get all submodules, you can use the command `git submodule update --init --recursive`.

# Usage with the IntelliJ IDEA

(tested with IntelliJ IDEA 2026.1.2)

* use `File > New > Project from Version Control` to get the project
* run a demo from package `org.networkcalculus.dnc.demos` in folder `main/java
