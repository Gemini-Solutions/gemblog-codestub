#!/usr/bin/env bash

set -o errexit          # Exit on most errors (see the manual)
set -o errtrace         # Make sure any error trap is inherited
set -o nounset          # Disallow expansion of unset variables
set -o pipefail

APISERVER=https://kubernetes.default.svc

# Path to ServiceAccount token
SERVICEACCOUNT=/var/run/secrets/kubernetes.io/serviceaccount

# Read this Pod's namespace
NAMESPACE=$(cat ${SERVICEACCOUNT}/namespace)

# Read the ServiceAccount bearer token
TOKEN=$(cat ${SERVICEACCOUNT}/token)

# Reference the internal certificate authority (CA)
CACERT=${SERVICEACCOUNT}/ca.crt

# Explore the API with TOKEN
echo "-----Well known open id configuration endpoint-----"
curl --cacert ${CACERT} --header "Authorization: Bearer ${TOKEN}" -sX GET ${APISERVER}/.well-known/openid-configuration | jq

echo "-----JWKS public key-----"
curl --cacert ${CACERT} --header "Authorization: Bearer ${TOKEN}" -sX GET ${APISERVER}/openid/v1/jwks | jq