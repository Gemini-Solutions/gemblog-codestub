#!/bin/bash

# Azure AD Tenant ID
TENANT_ID="b9806c7d-9280-4e44-afea-6dc0ff495c2f"

# Check if both arguments are provided
if [ "$#" -ne 2 ]; then
    echo "Usage: $0 <CLIENT_ID> <SCOPE>"
    exit 1
fi

# Azure AD Application (Client) ID
CLIENT_ID="$1"

# Scope
SCOPE="$2"

# Endpoint URLs
DEVICE_CODE_URL="https://login.microsoftonline.com/$TENANT_ID/oauth2/v2.0/devicecode"
TOKEN_URL="https://login.microsoftonline.com/$TENANT_ID/oauth2/v2.0/token"

# Request device code
device_code_response=$(curl -s -X POST -H "Content-Type: application/x-www-form-urlencoded" -d "client_id=$CLIENT_ID&scope=openid%20email%20$SCOPE" $DEVICE_CODE_URL)


# Extract device code details
user_code=$(echo $device_code_response | jq -r '.user_code')
device_code=$(echo $device_code_response | jq -r '.device_code')
verification_uri=$(echo $device_code_response | jq -r '.verification_uri')
expires_in=$(echo $device_code_response | jq -r '.expires_in')
message=$(echo $device_code_response | jq -r '.message')

# Display the message to the user
echo "To sign in, use a web browser to open the page $verification_uri and enter the code $user_code"
echo "Message from server: $message"

# Polling for token
echo "Waiting for user to authenticate..."
token_response=""
while [ -z "$token_response" ]; do
  token_response=$(curl -s -X POST -d "grant_type=urn:ietf:params:oauth:grant-type:device_code&client_id=$CLIENT_ID&device_code=$device_code" $TOKEN_URL | jq -r 'if .error then empty else . end')
  if [ -n "$token_response" ]; then
    break
  fi
  sleep 5
done

# Extract access token
access_token=$(echo $token_response | jq -r '.access_token')

# Display access token
echo "Access token received:"
echo $access_token

