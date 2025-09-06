#!/bin/bash
set -e


# Build the project using the specified Maven path
/Users/shailesh/codebase/apache-maven-3.9.9/bin/mvn clean package

## List files inside the TensorFlow JAR for inspection
#JAR_PATH=$(ls ./libs/tensorflow-core-api-macosx-x86_64.jar 2>/dev/null | head -n 1)
#if [ -n "$JAR_PATH" ]; then
#  unzip -l "$JAR_PATH" > jar_contents.txt
#fi
#
## Extract TensorFlow native library from the JAR if not already present
#if [ -n "$JAR_PATH" ]; then
#  if ! ls ./libs/libjnitensorflow.dylib 1> /dev/null 2>&1; then
#    unzip -j "$JAR_PATH" 'org/tensorflow/internal/c_api/macosx-x86_64/libjnitensorflow.dylib' -d ./libs
#  fi
#fi

# Run the main class and log output
java -cp "target/system-design-txn-1.0-SNAPSHOT.jar:libs/*" \
     -Dtensorflow.native.autoLoad=true \
     model.TensorFlowProtoExample \
     > runlog.log 2>&1
