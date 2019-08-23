#!/bin/sh

# recherche du répertoire qui contient TransferOnLAN
PROGRAM=`readlink "$0"`
if [ "$PROGRAM" = "" ]; then
  PROGRAM=$0
fi
PROGRAM_DIR=`dirname "$PROGRAM"`

# lancement de TransferOnLAN
java -jar $PROGRAM_DIR/TransferOnLAN.jar
