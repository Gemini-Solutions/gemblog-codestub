import boto3
from botocore.exceptions import ClientError
import os
from flask import Flask

app = Flask(__name__)

secret_name = "irsa/onprem/test"
region_name = 'ap-south-1'

def get_secret(secret_name, region_name) -> str:
    # Create a Secrets Manager client
    session = boto3.session.Session()
    client = session.client(
        service_name='secretsmanager',
        region_name=region_name
    )

    try:
        get_secret_value_response = client.get_secret_value(
            SecretId=secret_name
        )
    except ClientError as e:
        if e.response['Error']['Code'] == 'ResourceNotFoundException':
            print("The requested secret " + secret_name + " was not found")
        elif e.response['Error']['Code'] == 'InvalidRequestException':
            print("The request was invalid due to:", e)
        elif e.response['Error']['Code'] == 'InvalidParameterException':
            print("The request had invalid params:", e)
        else:
            print("Error occurred:", e)
    else:
        # Secrets Manager decrypts the secret value using the associated KMS key automatically
        if 'SecretString' in get_secret_value_response:
            secret = get_secret_value_response['SecretString']
            print(secret)
            return secret
        else:
            print("Binary data not supported. Secret value is in binary.")

    return "could not find secret"

# Replace 'your-secret-name' with the name of your secret and 'your-region' with your AWS region

@app.route("/ping")
def hello():
    return get_secret(secret_name=secret_name, region_name=region_name)


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)

