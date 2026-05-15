name: Build Launcher JAR

on:
  push:
    branches: [ "main", "master" ]
  pull_request:
    branches: [ "main", "master" ]
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout repository
        uses: actions/checkout@v4

      - name: Set up Java 17
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'
          cache: maven

      - name: Build with Maven
        run: mvn -B clean package

      - name: Upload launcher jar
        uses: actions/upload-artifact@v4
        with:
          name: labymod-style-launcher-jar
          path: target/labymod-style-launcher-1.0.0.jar
