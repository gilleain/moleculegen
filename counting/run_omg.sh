#!/bin/sh
CDK_DIR=../../cdk/cdk
DIST=${CDK_DIR}/dist/jar
OMG_DIR=../openmg-code
CLASSPATH=${DIST}/*:${CDK_DIR}/jar/*:${OMG_DIR}/src
java -cp ${CLASSPATH} org.omg.OMG $*
