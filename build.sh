#!/bin/bash
mvn clean package
docker login
docker build -t test/app:1.0 .
docker run  -it -p 8080:8080 -t test/app:1.0