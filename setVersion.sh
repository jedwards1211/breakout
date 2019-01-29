#!/usr/bin/env bash

VERSION=$1

sed -i '' -e "s/$(./getVersion.sh)/$VERSION/g" pom.xml */pom.xml

