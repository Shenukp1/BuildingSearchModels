FROM maven:3.9-eclipse-temurin-21
WORKDIR /app

# Compile at build time; Maven downloads are cached between builds
COPY . .
RUN --mount=type=cache,target=/root/.m2 mvn compile test-compile

CMD ["mvn", "test"]
