#!/bin/sh
CDK_DIR=../../cdk/cdk
CLASSPATH=${CDK_DIR}/dist/jar/*:${CDK_DIR}/jar/*:bin:lib/*
java -cp ${CLASSPATH} app.AMG $*
