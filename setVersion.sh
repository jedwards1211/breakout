#!/usr/bin/env bash

VERSION=$1

sed -i '' -e "s/0\.0\.0-SNAPSHOT/$VERSION/g" pom.xml */pom.xml

