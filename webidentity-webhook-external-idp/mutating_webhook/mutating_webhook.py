import base64
import copy
import http
import jsonpatch
import json
from flask import Flask, jsonify, request

app = Flask(__name__)

web_identity_pod_spec = {
            "name": "webidentity-sidecar",
            "image": "registry-np.geminisolutions.com/webidentity-sidecar:latest",
            "env" : [
                {
                    "name" : "CLIENT_ID",
                    "valueFrom" : {
                        "secretKeyRef" : {
                           "name" : "azure-client-cred",
                           "key"  : "CLIENT_ID"
                        }
                    }
                },
                {
                    "name" : "CLIENT_SECRET",
                    "valueFrom" : {
                        "secretKeyRef" : {
                           "name" : "azure-client-cred",
                           "key"  : "CLIENT_SECRET"
                        }
                    }
                },
                {
                    "name" : "SCOPE",
                    "valueFrom" : {
                        "secretKeyRef" : {
                           "name" : "azure-client-cred",
                           "key"  : "SCOPE"
                        }
                    }
                }
            ]
}



'''
The mutating webhook receives an admission review request and receives and admissionreview response
so the payload of this request has the kind 'AdmissionReview'.
And as this is the request we fetch the request from admissionreview json and so on
'''

@app.route("/mutate", methods=["POST"])
def mutate():

    '''use this to debug and print the incoming request and then play along'''
    # print(request.data)
    pod = request.json["request"]["object"]
    pod_name = request.json["request"]["object"]["metadata"]["labels"]["app"]
    name_space = request.json["request"]["namespace"]
    # This will be used to make changes and patch to the original request
    pod_copy = copy.deepcopy(pod)



    try:

        pod_copy["spec"]["containers"].append(web_identity_pod_spec)

        pod_copy["spec"]["volumes"].append(
          {
            "name": "web-identity-token",
            "emptyDir": {}
          }
        )

        containers = pod_copy["spec"]["containers"]
        for container in containers:
            if "volumeMounts" not in container.keys():
                container["volumeMounts"] = []
            if "env" not in container.keys():
                container["env"] = []
            container["volumeMounts"].append(
                {
                    "name" : "web-identity-token",
                    "mountPath" : "/home/web_identity_token"
                }
            )
            container["env"].append(
                {
                    "name" : "AWS_ROLE_ARN",
                    "value" : "arn:aws:iam::1122334455:role/" + pod_name + "-" + name_space + "-onprem-webidentity-role"
                }
            )
            container["env"].append(
                {
                    "name" : "AWS_WEB_IDENTITY_TOKEN_FILE",
                    "value" : "/home/web_identity_token/token"
                }
            )

    #This needs to pass as if key does not exist we do not want the pod creation to fail
    except KeyError:
        pass
    patch = jsonpatch.JsonPatch.from_diff(pod, pod_copy)
    return jsonify(
        {
            "apiVersion": "admission.k8s.io/v1",
            "kind": "AdmissionReview",
            "response": {
                "allowed": True,
                "uid": request.json["request"]["uid"],
                "patch": base64.b64encode(str(patch).encode()).decode(),
                "patchType": "JSONPatch",
            }
        }
    )


@app.route("/health", methods=["GET"])
def health():
    return ("", http.HTTPStatus.NO_CONTENT)


if __name__ == "__main__":
    ssl_context = ('server.crt', 'server.key')
    app.run(host="0.0.0.0", ssl_context=ssl_context, port=443, debug=True)
