## Introduction

This application provides an endpoint returning the total datasets submitted by federal ministries

## Stack

Project built using:
* kotlin
* swing
* gradle

## Executing

To execute tests:
> sh gradlew test

To run application:
> sh gradlew bootRun

The API can then be accessed at http://localhost:8080/ministries

## Improvements

I don't have much experience either with the JVM or Kotlin and therefore also with spring.

* Performance
  * Coming from .NET the simple answer would be to use async/await. I'm not sure if coroutines are really supported or the most widely-accepted approach
  * The ckan api supports offset-based pagination, but I'm not sure that's really beneficial 
* Data-driven tests. I'm not sure how I could use them in combination with coroutines. Perhaps a change of test framework would be needed?
* Api docs. I see that spring has some facility for this in combination with integration tests
* Observability. There is no logging/performance metrics
* Project structure. Not sure what the approach is to packaging functionality in Java/Kotlin land