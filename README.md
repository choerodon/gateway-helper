# Gateway Helper
Permissions check, traffic restrictions service. Authenticating and limiting the requests from `api-gateway`, create JWT and return to `api-gateway`. You can specify which `gateway-helper` to go through `manager-service`, and configure the request to use the default `gateway-helper` or the customized `gateway-helper` for authentication and traffic restriction. At the same time, the service does not need to be restarted after the modification.

![流程图](screenshot/flow_chart.png)

## Feature
- Add a custom `gateway-helper` demo

## Requirements
- This service is an eureka client service. To run in local, we need to cooperate with `eureka-server`, and the online operation needs to cooperate with `go-register-server`.
- Need to cooperate with `api-gateway` to complete the gateway functions such as authentication, rateLimit.

## Installation and Getting Started
- run `eureka-server`
- run `mysql`，ensure that the database table of `iam_service` has been initialized
- Go to the project directory and run `mvn spring-boot:run`

## Dependencies
- `go-register-server`: Registration service
- `config-server`: Configuration service
- `mysql`：`iam_service` database
- `redis`: cache

## Links

* [Change Log](./CHANGELOG.zh-CN.md)

## How to Contribute
Pull requests are welcome! [Follow](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md) to know for more information on how to contribute.

## Note
- Need to cooperate with `api-gateway` to complete the gateway functions such as authentication, traffic restriction.