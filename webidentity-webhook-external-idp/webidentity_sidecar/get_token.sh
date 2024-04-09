#!/bin/sh

wget --quiet \
     --header="Content-Type: application/x-www-form-urlencoded" \
     --post-data="client_id=$CLIENT_ID&client_secret=$CLIENT_SECRET&scope=$SCOPE&grant_type=client_credentials" \
     --output-document=- \
     https://login.microsoftonline.com/<TENANT-ID>/oauth2/v2.0/token \
     | sed -n 's/.*"access_token":"\([^"]*\)".*/\1/p' > /home/web_identity_token/token
