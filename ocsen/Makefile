default:
	@echo "=============Building Local Service============="
	./mvnw clean install
	docker build -f Dockerfile -t ocsen-user-service .

build: default
	@echo "=============Build And Starting Service Locally============="
	docker-compose up -d

up:
	@echo "=============Starting Service Locally============="
	docker-compose up -d

logs:
	docker-compose logs -f

down:
	docker-compose down

test:
	go test -v -cover ./...

clean: down
	@echo "=============Cleaning Up============="
	rm -f ocsen-user-service
	docker system prune -f
	docker volume prune -f
