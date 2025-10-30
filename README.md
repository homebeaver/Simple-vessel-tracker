# Description

This repository contains a simple vessel tracker written in java using the AISStream API.

## Prerequisites

Before you can use this program, you will need to sign up for an AISStream API key at [aisstream.io](https://aisstream.io/authenticate).

## Run

- download a [jar-with-dependencies](https://github.com/homebeaver/Simple-vessel-tracker/releases)
- to run you need a java JDK f.i. OpenJDK Runtime Environment Temurin-17.0.7+7

```
>java -version
openjdk version "17.0.7" 2023-04-18

>java -jar vessel-tracker-0.0.1-SNAPSHOT-jar-with-dependencies.jar nimbus
```
- push the small play button in the upper left corner to start
- live demo stops after 10 minutes

<img width="852" height="788" alt="grafik" src="https://github.com/user-attachments/assets/94b4a715-79f3-4a04-8fc6-c34147d4f666" />

## Build

- clone repo

```
mvn clean install
```
