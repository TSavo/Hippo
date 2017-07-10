#!/bin/bash
docker run -d -v $PWD:/app -w /app tsavo/hippo:latest mvn install exec:exec

