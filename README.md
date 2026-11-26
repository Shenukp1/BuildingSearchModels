# Search Model Implementation

This project contains code that build the implementation for the search model X(replace).

## Requirements

- [Docker](https://www.docker.com/get-started/)(Required)

## Usage

### Running Search Model Program

Showcases how to run the set based search model. The following CLI command requires two types of inputs, one of them being
an text file(i.e., `input.txt`) and the rest being integer parameters.

```bash
./run.sh run CPSC433F25Main filename Wminfilled Wpref Wpair Wsecdiff pen_lecturemin pen_tutorialmin pen_notpaired pen_section
```
- `CPSC433F25Main` is the Java class (`CPSC433F25Main.java`) containing the `main` method entry point.


### Running Java File Generally

Showcases how to run a java file generally. As in, following the instructions below, you can run any java file of
your choosing.

```bash
./run.sh run my.package.MyClass [args...]  # run a class with a main method and optional arguments
```

- `my.package` is only relevant if we create a directory within `src/main/java`. For example if we added a file
within `src/main/java/package/fileName.java`, then it would be the case we would need to do `./run.sh run package.fileName`.

TODO(shenukp1) Oct 26, 2026: It is probably the case we have a main file that runs everything so we
should make that the default when we run `./run.sh run`.

### Testing
```bash
./run.sh test                    # run all tests
./run.sh test MyTest             # run one test class
```


