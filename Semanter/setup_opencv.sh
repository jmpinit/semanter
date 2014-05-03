#!/bin/bash

# DESCRIPTION: helps setup Android OpenCV for an Android Studio project
# USAGE: install_opencv.sh <root/dir/of/app (inside project root folder)> <root/dir/of/android/opencv>
# before running script:
# SETUP:
#   1) download android opencv from http://opencv.org/downloads.html
#   2) unzip android opencv
#   3) setup project in Android Studio using the normal New Project wizard
#   4) run this script
#
# method from http://stackoverflow.com/questions/17767557/how-to-use-opencv-in-android-studio-using-gradle-build-tool

# exit on any error
set -e

die () {
    echo >&2 "$@"
    exit 1
}

pause () {
    read -n1 -r -p "Press any key to continue..." key
}

# validate arguments
[ "$#" -eq 2 ] || die "2 arguments required, $# provided"

approot=$1
projectroot=$approot/../
[ -d "$approot" ] || die "Directory $dir does not exist"

cvroot=$2
[ -d "$cvroot" ] || die "Directory $dir does not exist"

# copy library files
mkdir -p $approot/lib/armeabi-v7a >/dev/null
cp $cvroot/sdk/native/libs/armeabi-v7a/libopencv_info.so $approot/lib/armeabi-v7a >/dev/null
cp $cvroot/sdk/native/libs/armeabi-v7a/libopencv_java.so $approot/lib/armeabi-v7a >/dev/null

# zip into jar
mkdir -p $approot/libs >/dev/null
zip -r $approot/libs/opencv.jar $approot/lib >/dev/null 2>/dev/null
rm -r $approot/lib >/dev/null

# modify project's build.gradle
echo -e "\nedit $approot/build.gradle and put \"compile files('libs/opencv.jar')\" in the dependencies section.\n"
pause

# copy opencv java folder into project
cp -r $cvroot/sdk/java $approot/libs/OpenCV > /dev/null
curl https://gist.githubusercontent.com/jmptable/9925335/raw/ff30bcff9e133038c99f8aa40eee8cddb1fee47f/build.gradle > $approot/libs/OpenCV/build.gradle >/dev/null 2>/dev/null

projectname=$(basename $approot)
echo "include ':$projectname:libs:OpenCV', ':$projectname'" > $projectroot/settings.gradle

echo -e "\nadd \"compile project(':$projectname:libs:OpenCV')\" to the dependencies section of $approot/build.gradle\n"
pause

echo "done!"