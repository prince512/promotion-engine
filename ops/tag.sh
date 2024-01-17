#!/bin/bash +e
# This will work fine on mac and linux
# For Windows users : use git bash or cygwin

gradle build

DIR=$( cd "$( dirname "$0" )" && pwd )
BRANCH=$(git branch --show-current)
BRANCH=${BRANCH//\//_}
COMMIT=$(git log -n 1 --pretty=format:%h)
ENV=$1
#APP=$2

[[ -z "$ENV" ]] && { echo "Pass environment name(dev/qa/uat/prd) to deploy" ; exit 1; }
#[[ -z "$APP" ]] && { echo "Pass app/dir name to deploy" ; exit 1; }
[[ -z "$BRANCH" ]] && { echo "Not able to find git branch name. this folder is not git repository" ; exit 1; }
[[ -z "$COMMIT" ]] && { echo "Not able to find git commit id. this folder is not git repository" ; exit 1; }

pushd "${DIR}" > /dev/null
  if "./allow_deployment_to_environments/decide.sh" "$ENV" "$BRANCH"; then
    export ALLOWED=true
  fi
popd

[[ -z "$ALLOWED" ]] && { echo "You are not allowed to deploy into ${ENV} environment from this ${BRANCH} branch" ; exit 1; }

COMMIT_USER=$(git config user.name)
COMMIT_USER=${COMMIT_USER//[^[:alnum:]]/}

if [ -z "$COMMIT_USER" ]
then
  echo "git user name is not set, type the following commands to set it. replace <wasoko-user-id> with your wasoko id"
  echo "command 1) git config user.name <wasoko-user-id>"
  echo "command 2) git config user.email <wasoko-user-id>@wasoko.com"
  exit 1
  echo "\$COMMIT_USER is empty"
fi

TAG=$1---$(date -u +%Y-%m-%d--%H-%M-%S)---commit--$COMMIT---branch--$BRANCH---by--$COMMIT_USER
TAG="${TAG:0:127}"

mkdir -p $DIR/../tmp
echo $TAG > $DIR/../tmp/tag
echo $TAG

git tag $TAG
git push -f origin $TAG


#to delete a local tag ---> "git tag -d TAG"
#to delete a remote tag ---> "git push origin :TAG"    or    "git push -d origin tag"

# To delete all remote tags (cloud build tags)
#git push origin --delete $(git ls-remote --tags origin *---* | awk '{ print $2 }')
# To delete all local tags (cloud build tags)
#git tag -d $(git tag -l *---*)

