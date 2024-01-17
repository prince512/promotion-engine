#! /bin/bash
set -e -o pipefail
gcloud beta run deploy ${SERVICE_NAME} --image=${IMAGE_NAME} --region=${REGION} --service-account=cloudrun@${PROJECT}.iam.gserviceaccount.com --update-secrets=SPRING_CLOUD_CONFIG_PASSWORD=SPRING_SECURITY_USER_PASSWORD:latest,DB_PASSWORD=WRITER:latest,NEWRELIC_API_KEY=NEWRELIC_API_KEY:latest --update-env-vars=ENV=${ENV},NAME=${SERVICE_NAME} --execution-environment=gen2 --ingress=internal-and-cloud-load-balancing --allow-unauthenticated --cpu-boost --vpc-connector=projects/${PROJECT_VPC}/locations/${REGION}/connectors/default

gcloud beta run services update-traffic ${SERVICE_NAME} --to-latest --region=${REGION}