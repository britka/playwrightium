@echo off

IF "%1%"=="" GOTO help
IF "%1%"=="help" GOTO help
if "%1%"=="show" GOTO show
GOTO show_with_file

:help
  echo Usage:
  echo viewTracing.sh [option]
  echo option can be:
  echo show - open trace viewer
  echo help - will type this help
  echo file path - relative tracing file path. if empty then will type help.
  exit /b 0

:show
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D "exec.args=show-trace"
goto:eof

:show_with_file
set scriptFolder=%~dp0
set scriptFolder=%scriptFolder:~0,-1%
set file_path=%scriptFolder%\%1%
if not exist %file_path% (
    echo File: %file_path% is not exists
    goto:eof
)
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="show-trace %file_path%"
