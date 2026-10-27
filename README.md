# Search Model Implementation

This project contains code that build the implementation for the search model X(replace).

## Requirements

- [Docker](https://www.docker.com/get-started/)(Required)

## Usage

### Testing
```bash
./run.sh test                    # run all tests
./run.sh test MyTest             # run one test class
```

### Running a file

TODO(shenukp1) Oct 26, 2026: It is probably the case we have a main file that runs everything so we
should make that the default when we run `./run.sh run`.

```bash
./run.sh run my.package.MyClass  # run a class with a main method
```

- `my.package` is only relevant if we create a directory within `src/main/java`. For example if we added a file
within `src/main/java/package/fileName.java`, then it would be the case we would need to do `./run.sh run package.fileName`.

