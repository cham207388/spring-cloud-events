.PHONY: compose-up compose-down

compose-up:
	docker compose up -d

compose-down:
	docker compose down -v

# order
order-image:
	docker image build -t order-service ./order-service

order-build:
	cd order && ./gradlew build