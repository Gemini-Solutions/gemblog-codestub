#!/bin/bash

# 1. Create the CA key and certificate
openssl req -new -x509 -days 365 -nodes -out ca.crt -keyout ca.key -subj "/CN=MyCA"

# 2. Generate the server key and CSR
openssl req -new -nodes -out server.csr -newkey rsa:2048 -keyout server.key -subj "/CN=mutating.webhook.svc"

# 3. Sign the server certificate with the CA
openssl x509 -req -days 365 -in server.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out server.crt -extensions v3_req -extfile <(echo -e "[v3_req]\nsubjectAltName = DNS:mutating.webhook.svc")

# Concatenate the CA certificate and server certificate into a single file
cat ca.crt server.crt > ca_bundle.crt

# Base64 encode the combined file
base64 -w 0 ca_bundle.crt