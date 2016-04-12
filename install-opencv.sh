#!/usr/bin/env bash

set -e

OPENCV_VERSION=3.1.0

if [[ -d "$OPENCV_JAVA_PATH" && "$(ls -A $OPENCV_JAVA_PATH)" ]]; then
    echo "OpenCV already installed. Installation will be skipped."
    exit 0
fi

echo "OpenCV-$OPENCV_JAVA_PATH is being installed..."
git clone git://github.com/Itseez/opencv.git --branch $OPENCV_VERSION --depth=1
cd opencv
mkdir build
cd build
cmake -DBUILD_SHARED_LIBS=OFF -DCMAKE_BUILD_TYPE=RELEASE -DCMAKE_INSTALL_PREFIX=$OPENCV_HOME ..
make -j4
make install
cd ../..