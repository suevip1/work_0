#!/usr/bin/env bash
echo 'DAGSchedulex Building...'

mvn clean package -T 1C -DskipTests  -pl \
engine-entrance \
-am
