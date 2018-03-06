#!/usr/bin/env bash

set -e

OPENCV_VERSION=3.4.1

if [[ -d "$OPENCV_JAVA_PATH" && "$(ls -A $OPENCV_JAVA_PATH)" ]]; then
    echo "OpenCV already installed. Installation will be skipped."
    exit 0
fi

echo "OpenCV-$OPENCV_JAVA_PATH is being installed..."
git clone https://github.com/opencv/opencv.git --branch $OPENCV_VERSION --depth=1
cd opencv
mkdir build
cd build
cmake \
    -D BUILD_SHARED_LIBS=OFF \
    -D BUILD_EXAMPLES=OFF \
    -D BUILD_TESTS=OFF \
    -D BUILD_PERF_TESTS=OFF \
    -D CMAKE_BUILD_TYPE=RELEASE \
    -D CMAKE_INSTALL_PREFIX=$OPENCV_HOME \
    ..
make -j$(nproc)
make install
cd ../..
