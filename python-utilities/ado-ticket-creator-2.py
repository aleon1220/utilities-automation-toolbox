# List of applications to create tickets for
# applications = [
    # "vwise-loadcontrolservices", "vwise-flightscheduleservices", "vwise-inflightdataservices", "vwise-businessintelligenceservices", "vwise-eftposservices", 
    # "vwise-crewingservices", "vwise-inflightopsservices", "vwise-paxservices", "vwise-seatingservices", 
    # "vwise-flightservices", "vwise-checkinservices", "vwise-commsservices", "vwise-departureservices", "vwise-authservices", 
    # "vwise-availabilityservices","vwise-baggageservices","vwise-boardingservices","vwise-logpaymentservices"
# ]

import requests
import json
import os
from requests.auth import HTTPBasicAuth

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


# applications = [ 
                  # ("vwise-availabilityservices",694986),
                  # ("vwise-baggageservices",742659),
                  # ("vwise-boardingservices",694984),
                  # ("vwise-businessintelligenceservices",694984),
                  # ("vwise-checkinservices",694984),
                  # ("vwise-crewingservices",694984),
                  # ("vwise-eftposservices",694984),
                  # ("vwise-flightservices",694984),
                  # ("vwise-inflightopsservices",742603),
                  # ("vwise-loadcontrolservices", 742603),
                  # ("vwise-paxservices",694984),
                  # ("vwise-seatingservices",694986),
                  # ("vwise-logpaymentservices",694986),
                  # ("vwise-authservices",743273),
                  # ("vwise-departureservices",743273),
                  # ("vwise-commsservices",694984)
                 # ]
applications = [ 
                  ("vwise-availabilityservices",694986),
                  ("vwise-baggageservices",742659),
                  ("vwise-boardingservices",694984),                  
                  ("vwise-checkinservices",694984),
                  ("vwise-crewingservices",694984),
                  ("vwise-eftposservices",694984),
                  ("vwise-flightservices",694984),
                  ("vwise-inflightopsservices",742603),
                  ("vwise-loadcontrolservices", 742603),
                  ("vwise-paxservices",694984),
                  ("vwise-seatingservices",694986),                  
                  ("vwise-authservices",743273),
                  ("vwise-departureservices",743273),
                  ("vwise-commsservices",694984),
                 ]
                  
#applications = [ ("alexei-test", 694984) ]

for app, feature_id in applications:
    # Build the JSON patch document payload with dynamic title
    # payload = [
        # {
            # "op": "add",
            # "path": "/fields/System.Title",
            # "value": f"{app} smoke testing of ECS PROD deployment"
        # },
        # {
            # "op": "add",
            # "path": "/fields/System.Description",
            # "value": (
                # "<b>As an</b> Application Owner<br><br>"
                # "<b>I need</b> to make sure that<br><br>"
                # "Application deployed in Fargate is equal to the application on Websphere, and functions just as well<br><br>"
                # "<b>So that</b> Application is functioning before the cutover<br>"
            # )
        # },
        # {
            # "op": "add",
            # "path": "/fields/Microsoft.VSTS.Common.AcceptanceCriteria", "value": (
                # "<ul>"
                # "<li>Identify most frequent <b>read request</b> (via Splunk) for the service in prod and send it using Postman to the ECS URL</li>"
                # "<li>Response should be 200</li>"
                # "<li>Response should be similar to the one in production</li>"
                # "</ul>"
            # )
        # },
        # {
            # "op": "add",
            # "path": "/fields/System.Tags",
            # "value": "ShadowSRESquad"
        # },
        # {
            # "op": "add",
            # "path": "/fields/System.AreaPath",
            # "value": "AirNZ\\Tribes\\Digital Platforms\\Platforms and tools"
        # },
        # {
            # "op": "add",
            # "path": "/fields/System.IterationPath",
            # "value": "AirNZ\\Iterations\\FY25\\25.Q4\\Sprint 25"
        # }
    # ]
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
