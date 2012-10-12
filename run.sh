#!/bin/sh
CDK_DIR=../../cdk/cdk
DIST={$CDK_DIR}/dist/jar
CLASSPATH=${DIST}/*:${CDK_DIR}:${CDK_DIR}/jar/*:bin:lib/*
java -cp ${CLASSPATH} app.AMG $*
