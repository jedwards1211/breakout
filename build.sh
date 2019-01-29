#!/bin/bash

mvn clean install -P osx,windows,windows-i586,windows-amd64,linux,linux-i586,linux-amd64
if [[ -e /Applications ]]; then
	VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | grep -Ev '(^\[|Download\w+:)')
  	rm /Applications/breakout-*.jar
	cp breakout/target/breakout-$VERSION.jar /Applications
	ln -sf /Applications/breakout-$VERSION.jar /Applications/Breakout
fi
