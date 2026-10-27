#!/usr/bin/env sh
# Builds the Docker image, then runs tests or a main class inside it.

set -e

IMAGE="cpsc433"
CACHE="cpsc433-m2-cache"   # keeps Maven downloads between runs(beauty of caching)

if [ "$1" = "run" ]; then
    docker build -t "$IMAGE" .
    docker run --rm -v "$CACHE:/root/.m2" "$IMAGE" mvn exec:java -Dexec.mainClass="$2"

elif [ "$1" = "test" ] && [ -n "$2" ]; then
    docker build -t "$IMAGE" .
    docker run --rm -v "$CACHE:/root/.m2" "$IMAGE" mvn test -Dtest="$2"

elif [ "$1" = "test" ]; then
    docker build -t "$IMAGE" .
    docker run --rm -v "$CACHE:/root/.m2" "$IMAGE" mvn test

else
    echo "Usage:"
    echo "  ./run.sh test                  run all tests"
    echo "  ./run.sh test MyTest           run one test class"
    echo "  ./run.sh run my.package.Main   run a class with a main method"
    exit 1
fi
