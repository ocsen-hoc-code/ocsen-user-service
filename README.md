# ocsen-user-service
OcSen UserSerice  manage user on OcSen Microservice

Require:
- Install docker on your local (Link: https://docs.docker.com/install/)

Run graylog
- Enable `mongodb`, `elasticsearch`, `graylog` in docker-compose.yml

Warning: If you would like use graylog to write log for system, your pc have cpu have least INTEL CORE I3 and Ram 8GB.

Run on PC
- Disable `ocsen-user-service` in docker-compose.yml
- Change value `spring.datasource.url`, `spring.redis.host` in appliction.properties file.

Run OcSen User Service

- Run cmd `make build` for first running, it will help build application and create docker images
- Run cmd `make down` if you would like stop server
- Run cmd `make up` if you would like start server
- Run cmd `make clean` if you would like remove docker container

Api Document: http://127.0.0.1:8081/swagger-ui.html


PS: Please ignore alert test case failed on travis because some issue with travis environment but test case on local still working correctly. I will fix it soon.
