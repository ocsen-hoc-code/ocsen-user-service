# ocsen-user-service
OcSen UserService  manage user on OcSen Microservice

Introduce

- OcSen UserService support mobile, it not use Spring boot Web Security library to manage user.
- OcSen UserService will generate JWT for all other service in OcSen Microservice and use Redis to manage JWT logout.
- OcSen UserService help Zuul Serivice not need verify JWT and share Security, Zuul Service will more ram for other important task.

Require:

- Install docker on your local (Link: https://docs.docker.com/install/)

Run graylog

- Enable `mongodb`, `elasticsearch`, `graylog` in docker-compose.yml
- If you would like run on IDE please change `<graylogHost>graylog</graylogHost>` to  `<graylogHost>127.0.0.1</graylogHost>` in logback.xml file.

Warning: If you would like use graylog to write log for system, your pc have cpu have least INTEL CORE I3 and Ram 8GB.
         If you run app with docker it will take few second to start apllication.

Run on IDE

- Disable `ocsen-user-service` in docker-compose.yml
- Change value `spring.datasource.url`, `spring.redis.host` in appliction.properties file.

Run OcSen User Service

- Run cmd `make build` for first running, it will help build application and create docker images
- Run cmd `make down` if you would like stop server
- Run cmd `make up` if you would like start server
- Run cmd `make clean` if you would like remove docker container

Api Document: http://127.0.0.1:8081/swagger-ui.html

PS: Please ignore alert test case failed on travis because some issue with travis environment but test case on local still working correctly. I will fix it soon.
