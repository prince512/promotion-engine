
#!/bin/bash 
set -e -o pipefail
(gcloud run services describe "${SERVICE_NAME}" --platform managed --region="${REGION}" --format=json) > newrelic.json;
apt install -y jq;
jq --version;
newrelic_app_name=${SERVICE_NAME};
newrelic_apikey=$(gcloud secrets versions access latest --secret="NEWRELIC_API_KEY" --format="get(payload.data)" | base64 -d); 
revision=$(jq  ".status.latestReadyRevisionName" newrelic.json);
changelog=$(jq  ".spec.template.spec.containers[0].image" newrelic.json);
newrelic_app_id=$(curl -X GET "https://api.eu.newrelic.com/v2/applications.json?filter%5Bname%5D=$newrelic_app_name" -H "accept: application/json" -H "X-Api-Key:$newrelic_apikey"  | jq -r ".applications[0].id" );
curl -X POST "https://api.eu.newrelic.com/v2/applications/$newrelic_app_id/deployments.json"  -H "X-Api-Key:$newrelic_apikey"  -i -H "Content-Type: application/json" -d "{\"deployment\" : { \"revision\" : $revision, \"changelog\" : $changelog }}" ;
      