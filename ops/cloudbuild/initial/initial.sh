#!/bin/bash -e

#./ops/cloudbuild/initial/initial.sh <project> <env> <businessunit> <repo>
#./ops/cloudbuild/initial/initial.sh sok-dev-svc dev retailiq promotion-service

DIR=$( cd "$( dirname "$0" )" && pwd )
mkdir -p $DIR/../../../tmp
touch $DIR/../../../tmp/.keep
tmpl=$DIR/../../../tmp/temp.yaml

sed -e "s/projectname/$1/g" -e "s/env/$2/g" -e "s/businessunit/$3/g" -e "s/reponame/$4/g" $DIR/initial.tmpl > $tmpl
_NAME=setup-$2--$3--$4
echo ${_NAME}

exist=$(gcloud beta builds triggers list --format="get(name)" --filter="name <= ${_NAME} AND name >= ${_NAME}" --project ${1} | wc -l);
echo $exist;

if [[ $exist -eq 0 ]]; 
then 
	echo "Not Exist, So Creating...."
    gcloud beta builds triggers import --source=$tmpl --project=$1;
else
	echo "Already Exist, So Ignoring...."
	if [ -n "$5" ] && [ "$5" == "_destroy" ];
	then
		echo "Destroying......."
		dest=$DIR/../../../tmp/dest.yaml
		echo gcloud beta builds triggers export ${_NAME} --destination $dest --project=$1;
		gcloud beta builds triggers export ${_NAME} --destination $dest --project=$1;
        sed -i '/  _ENV: /a \ \ _DESTROY: "true"' $dest
        echo gcloud beta builds triggers import --source=$dest --project=$1;
        echo ./ops/tag.sh setup-$2
	fi
fi
