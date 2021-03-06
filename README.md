# Zendesk Ticket Viewer - Zendesk Coding Challenge

## Table of contents
* [Introduction](#introduction)
* [Technologies](#technologies)
* [Requirements](#requirements)
* [Installation](#installation)
* [Usage](#Usage)

## Introduction
This project connects to the Zendesk API to retrieve ticket information from the created sub-domain. The tickets or the details of a specific ticket can be viewed on the command line interface.

## Technologies
The technologies used in this project are: 
* Java
* JUnit
* Maven

## Requirements
* JDK 17.0.1
* Apache Maven 3.8.4
* git

## Installation
Download the project as a zip and unzip it in your system or clone the project using git with the following command
```
git clone https://github.com/abhi-srivathsa/Zendesk-Ticket-Viewer-ZCC.git
```

Change the current working directory
```
cd Zendesk-Ticket-Viewer-ZCC
```
Generate an API token from your Zendesk Account or use a previous active token

Replace credential placeholders with your credentials in .env file (if using a linux / MacOS terminal, use ls -a to list all files including . files)
```
SUBDOMAIN=<subdomain>
USERNAME=<username>
TOKEN=<token>
```
Run the following command (in the current working directory with pom.xml) to compile, test and package the project
```
mvn clean package
```

## Usage
Run the following command to start the CLI Ticket Viewer
```
java -jar target/Zendesk-Ticket-Viewer-ZCC-1.0-SNAPSHOT-jar-with-dependencies.jar
```

