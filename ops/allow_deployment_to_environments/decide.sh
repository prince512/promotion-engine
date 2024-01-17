#!/bin/bash +e
#./decide.sh env branch

DIR=$( cd "$( dirname "$0" )" && pwd )
pushd "${DIR}" > /dev/null
  if [ "$(uname)" == "Darwin" ]; then
    IFS=$'\n' branches=($(cut -d '=' -f1 "${1##*-}"))
  else
    readarray -t branches <"${1##*-}"
  fi
  echo "${branches[@]}"
  for branch in "${branches[@]}"; do
    branch=${branch//\//_}
    branch=${branch//\*}
    if [[ "${branch}" == "all" ]] || [[ "${2}" == *"${branch}"* ]] ; then
      exit 0
    fi
  done
  exit 1
popd

