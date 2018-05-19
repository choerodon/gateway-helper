# gateway-helper
> Gateway authentication, stream-limiting. authenticating and limiting the requests from api-gateway, create JWT and return to api-gateway. At the same time, you can modify the helperService field of the route on the manager-service management service page, and configure the request to use the default gateway-helper or the customized gateway-helper for authentication and current limiting. At the same time, the service does not need to be restarted after the modification.

![流程图](screenshot/flow_chart.png)

## Feature
- Add a custom gateway-helper demo

## Requirements
- This project is an eureka client project. To run in local, we needs to cooperate with register-server, and the online operation needs to cooperate with go-register-server.
- Need to cooperate with api-gateway to complete the gateway functions such as authentication, flow limitation, routing and so on.

## To get the code

```
git clone https://github.com/choerodon/gateway-helper.git
```

## Installation and Getting Started
- run register-server
- run msqyl，ensure that the database table of iam_service has been initialized
- Go to the project directory and run mvn spring-boot:run or GatewayHelperApplication in idea

## Usage
- Manually generate a mirror

   Pull the source code to execute mvn clean install. Generate the app.jar in the target directory, copy it to the src/main/docker directory, and there will be the dockerfile. Execute the docker build to generate the image.
- Use existing mirror

- Create a new deployment on k8s after mirroring. You can refer to the code directory deployment file written in the code.

## Dependencies
- go-register-server: Registration service
- config-server: Configuration service
- kafka
- mysql：iam_service database

## Reporting Issues

If you find any shortcomings or bugs, please describe them in the Issue.
    
## How to Contribute
Pull requests are welcome! Follow this link for more information on how to contribute.

## Note
- Need to cooperate with api-gateway to complete the gateway functions such as authentication, flow limitation, routing and so on.