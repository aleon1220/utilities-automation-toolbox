import requests
import json
import os
from requests.auth import HTTPBasicAuth

# script that creates Azure devops tickets for smoke testing of applications deployed in ECS Fargate. 
# It reads the organization, project, and PAT token from environment variables, creates a User Story work item 
# for each application in the list, linking it to a specified Feature.
# applications = [
    # "webservice-loadcontrol", "webservice-flights", "webservice-inflight", "webservice-business", 
    # "webservice-eftposservices", "webservice-crew", "webservice-ops", "webservice-pax", 
    #  "webservice-seating", "webservice-fly", "webservice-checkin",
    #  "webservice-comms", "webservice-departure", "webservice-auth", 
    # "webservice-availability","webservice-baggage",
    # "webservice-boarding", "webservice-logpay"
# ]

# Read organization, project, and PAT from environment variables
organization = os.getenv("ADO_ORG")
project = os.getenv("ADO_PROJECT")
pat_token = os.getenv("ADO_PAT_TOKEN")

# Proxy settings (modify accordingly)
proxies = {
    "http": os.getenv("HTTP_PROXY"),
    "https": os.getenv("HTTPS_PROXY")
}

# Construct the ADO REST API URL for creating a User Story work item
url = f"https://dev.azure.com/{organization}/{project}/_apis/wit/workitems/$User%20Story?api-version=6.0"

# Define headers; note the Content-Type required for a JSON patch document
headers = {
    "Content-Type": "application/json-patch+json"
}

# List of applications to create tickets for (with associated feature IDs)

applications = [ 
                  ("webservice-availability",694986),
                  ("webservice-baggage",742659),
                  ("webservice-boarding",694984),                  
                  ("webservice-checkin",694984),
                  ("webservice-crew",694984),
                  ("webservice-eftposservices",694984),
                  ("webservice-fly",694984),
                  ("webservice-ops",742603),
                  ("webservice-loadcontrol", 742603),
                  ("webservice-pax",694984),
                  ("webservice-seating",694986),                  
                  ("webservice-auth",743273),
                  ("webservice-departure",743273),
                  ("webservice-comms",694984),
                 ]

for app, feature_id in applications:
    # Build the JSON patch document payload with dynamic title and description
    payload = [
        {
            "op": "add",
            "path": "/fields/System.Title",
            "value": f"{app} Smoke Testing"
        },
        {
            "op": "add",
            "path": "/fields/System.State",
            "value": "To Do"
        },
        {
            "op": "add",
            "path": "/fields/System.Description",
            "value": (
                "<b>As an</b> Application Owner<br><br>"
                "<b>I need</b> to make sure that<br><br>"
                "Application deployed in Fargate PROD functions just as well a on prem<br><br>"
                "<b>So that</b> Application is functioning before the cutover<br>"
            )
        },
        {
            "op": "add",
            "path": "/fields/Microsoft.VSTS.Common.AcceptanceCriteria", "value": (
                "<ul>"
                "<li>For testring use one or more of most frequet READ/GET requests from Splunk</li>"
                "<li>Success status after hitting the curl request (PROD)</li>"
                "<li>Capture the diffrence between on-prem and ECS in terms of request duration</li>"
                "<li>Endpoint should be documented.</li>"
                "</ul>"
            )
        },
        {
            "op": "add",
            "path": "/fields/System.Tags",
            "value": "ShadowSRESquad"
        },
        {
            "op": "add",
            "path": "/fields/System.AreaPath",
            "value": "AirNZ\\Tribes\\Digital Platforms\\Platforms and tools"
        },
        {
            "op": "add",
            "path": "/fields/System.IterationPath",
            "value": "AirNZ\\Iterations\\FY26\\26.Q1\\Sprint 1"
        }
    ]

    # Use HTTP Basic Authentication where username is empty and PAT is used as the password
    response = requests.post(url, headers=headers, auth=HTTPBasicAuth("", pat_token), json=payload, proxies=proxies)

    if response.status_code in (200, 201):
        print(f"User Story for {app} created successfully!")
        data = response.json()
        print(json.dumps(data, indent=2))
        work_item_id = data['id']

        # --- Add link to Feature ---
        link_url = f"https://dev.azure.com/{organization}/{project}/_apis/wit/workitems/{work_item_id}?api-version=6.0"
        link_payload = [
            {
                "op": "add",
                "path": "/relations/-",
                "value": {
                    "rel": "System.LinkTypes.Hierarchy-Reverse",  # Makes this story a child of the feature
                    "url": f"https://dev.azure.com/{organization}/_apis/wit/workItems/{feature_id}",
                    "attributes": {
                        "comment": "Linking to parent Feature"
                    }
                }
            }
        ]
        link_response = requests.patch(link_url, headers=headers, auth=HTTPBasicAuth("", pat_token), json=link_payload, proxies=proxies)
        if link_response.status_code in (200, 201):
            print(f"User Story {work_item_id} successfully linked to Feature {feature_id}")
        else:
            print(f"Failed to link User Story to Feature: {link_response.status_code}")
            print(link_response.text)
        # --- End Feature Link ---
    else:
        print(f"Failed to create User Story for {app}:", response.status_code)
        print(response.text)
