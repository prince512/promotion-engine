#!/bin/bash -e

#ENV=dev ./ops/app.sh [] [build] [run]

ENV=${ENV:-dev}
if [ "$ENV" = "dev" ]; then
  export ET=d;
elif [[ "$ENV" = "qa" || "$ENV" = "uat" ]]; then
  export ET=t;
elif [ "$ENV" = "prd" ]; then 
  export ET=p;
fi

echo ==============current env==========
echo $ENV
echo $ET
echo ===================================

DIR=$( cd "$( dirname "$0" )" && pwd )
export ACT=$1
pushd $DIR/..
  if [ "${ACT}" == 'build' ] ; then
    ./gradlew clean build -x test -Djacoco.skip=true
  elif [ "${ACT}" == 'run' ] ; then
    java -jar build/libs/*-SNAPSHOT.jar
  else
    ./gradlew clean bootRun
  fi
popd

