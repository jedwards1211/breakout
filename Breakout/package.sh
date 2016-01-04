#!/bin/bash

cd ..
BASE_DIR=`pwd`

cd "${BASE_DIR}/andork-core" 				&& mvn install &&
cd "${BASE_DIR}/andork-math" 				&& mvn install &&
cd "${BASE_DIR}/andork-math3d" 				&& mvn install &&
cd "${BASE_DIR}/andork-ui" 					&& mvn install &&
cd "${BASE_DIR}/andork-ui-test" 			&& mvn install &&
cd "${BASE_DIR}/andork-plot" 				&& mvn install &&
cd "${BASE_DIR}/andork-spatial" 			&& mvn install &&
cd "${BASE_DIR}/andork-jogl-gl2es2-utils" 	&& mvn install &&
cd "${BASE_DIR}/andork-jogl-gl2es2-swing" 	&& mvn install &&
cd "${BASE_DIR}/Breakout" 					&& mvn package
