# Zendesk Ticket Viewer - Zendesk Coding Challenge

## Table of contents
* [Introduction](#introduction)
* [Technologies](#technologies)
* [Installation](#installation)
* [Usage](#Usage)

## Introduction
This project connects to the Zendesk API to retrieve ticket information from the created sub-domain. The tickets or the details of a specific ticket can be viewed on the command line interface.

## Technologies
The technologies used in this project are: 
* Java
* JUnit
* Maven

## Installation
Run the following command in the working directory (directory with pom.xml) of the project to compile, test and package the project

```
$ mvn clean package
```

## Usage
Run the following command to start the CLI Ticket Viewer
```
$ java -jar target/Zendesk-Ticket-Viewer-ZCC-1.0-SNAPSHOT-jar-with-dependencies.jar
```

