#!/bin/bash -e

DIR=$( cd "$( dirname "$0" )" && pwd )
BRH=$(git branch --show-current)
echo $DIR
echo $BRH

pushd $DIR/..
  git add .
  git commit -m "Autopush"
  git push origin $BRH
popd

