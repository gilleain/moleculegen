#!/bin/sh
CDK_DIR=../../chemistry/cdk
CLASSPATH=bin:lib/*
for p in ${CDK_DIR}/*/*/target/classes; do
	CLASSPATH="${CLASSPATH}:${p}"
done
CLASSPATH=${DIST}/*:$CLASSPATH:/Users/maclean/.m2/repository/java3d/vecmath/1.5.2/vecmath-1.5.2.jar
CLASSPATH=$CLASSPATH:/Users/maclean/.m2/repository/uk/ac/ebi/beam/beam-core/0.8/beam-core-0.8.jar
CLASSPATH=$CLASSPATH:/Users/maclean/.m2/repository/uk/ac/ebi/beam/beam-func/0.8/beam-func-0.8.jar
CLASSPATH=$CLASSPATH:/Users/maclean/.m2/repository/com/google/collections/google-collections/1.0/google-collections-1.0.jar
CLASSPATH=$CLASSPATH:/Users/maclean/.m2/repository//com/github/gilleain/signatures/signatures/1.1/signatures-1.1.jar
java -cp ${CLASSPATH} appbranch.DuplicateChecker $*
