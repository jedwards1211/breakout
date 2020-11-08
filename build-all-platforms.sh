#!/bin/bash

mvn clean install -Dbreakout.version="${BREAKOUT_VERSION}" \
  -P osx,windows,windows-i586,windows-amd64,linux,linux-i586,linux-amd64

cp breakout/target/breakout-0.0.0-SNAPSHOT.jar breakout/target/breakout-${BREAKOUT_VERSION}-all-platforms.jar