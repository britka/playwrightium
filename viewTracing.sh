#!/bin/sh

if [ $# -eq 0 ] || [ "$1" = help ]; then
  echo "Usage: ./viewTracing.sh [option]"
  echo "Option can be:"
  echo "show - open trace viewer"
  echo "help - will type this help"
  echo "file path - relative tracing file path. if empty then will type help."
  exit 0
fi
if [ "$1" = show ]; then
  mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="show-trace"
  exit 0
else
  if [ ! -f "$1" ]; then
    echo "File is not exists"
    exit 0
  fi
  mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="show-trace $1"
fi

