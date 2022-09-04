# EDA based 12st-Mall Testing
주문, 배송, 상품 및 게이트웨이 마이크로서비스를 로컬에서 실행하고 Kafka Messaging Platform을 경유해 시나리오에 맞는 EDA 통신이 진행되는 것을 확인한다.

- 사전 환경
  > Gitpod based 랩환경 사전설정 필수
[(Gitpod-based Lab Environments 참고링크)](https://github.com/acmexii/msaez-labs/tree/main/06%EA%B0%95_Sample-Order-Microservice#configure-web-based-rumtime-environments)

## Kafka Server Start
- 새로운 터미널을 오픈한다.
- Docker Compose로 Kafka 서버를 실행한다.
```
cd kafka
docker-compose up -d     # docker-compose 가 모든 kafka 관련 리소스를 받고 실행할 때까지 기다림
```

- Kafka 정상 실행 확인
```
$ docker-compose logs kafka | grep -i started    

kafka-kafka-1  | [2022-04-21 22:07:03,262] INFO [KafkaServer id=1] started (kafka.server.KafkaServer)
```

- Kafka 서버에 접속후 Consumer 실행하여 도메인 이벤트 모니터링 
```
docker-compose exec -it kafka /bin/bash   # kafka docker container 내부 shell 로 진입
cd /bin
./kafka-console-consumer --bootstrap-server http://localhost:9092 --topic mall --from-beginning
```

## 12st-Mall Microservice Start

### 주문 마이크로서비스 기동
- 새로운 터미널을 오픈해 아래 Scripts로 주문서비스를 8081 포트로 기동한다.
```bash
cd 08강-EDA_based_12st-Mall_Testing
cd order
mvn spring-boot:run
```

### 배송 마이크로서비스 기동
- 새로운 터미널을 오픈해 아래 Scripts로 배송서비스를 8082 포트로 기동한다.
```bash
cd 08강-EDA_based_12st-Mall_Testing
cd delivery
mvn spring-boot:run
```

### 상품 마이크로서비스 기동
-  새로운 터미널을 오픈해 아래 Scripts로 상품서비스를 8083 포트로 기동한다.
```bash
cd 08강-EDA_based_12st-Mall_Testing
cd product
mvn spring-boot:run
```

### REST APIs Call according to the Scenario
- 새로운 터미널을 오픈해 상품의 재고 정보를 확인한 뒤, Order Microservice에 주문을 요청한다.
```
http GET http://localhost:8083/inventories
http POST http://localhost:8081/orders customerId=1000 productId=1000 productName=TV qty=10 
http GET http://localhost:8081/orders
http GET http://localhost:8083/inventories
```
- Kafka Consumer 창에서 Publish 된 도메인 이벤트를 확인한다.
- 주문 후, 상품(ProductId: 1000)의 재고가 90개로 줄어든게 확인된다.
- Order Microservice에 주문취소을 요청한다.
```
http DELETE http://localhost:8081/orders/1
http GET http://localhost:8081/orders
http GET http://localhost:8083/inventories
```
- Kafka Consumer 창에서 Publish 된 도메인 이벤트를 확인한다.
- 주문취소 후, 상품(ProductId: 1000)의 재고가 다시 100개로 늘어난게 확인된다.


### 게이트웨이 마이크로서비스 기동

- 새로운 터미널을 오픈해 아래 Scripts로 게이트웨이서비스를 8088 포트로 기동한다.
```bash
cd 08강-EDA_based_12st-Mall_Testing
cd gateway
mvn spring-boot:run
```

- 게이트웨이를 경유하는 주문 요청을 Order Microservice에 전송한다.
```
http POST http://localhost:8088/orders customerId=1000 productId=1000 productName=TV qty=10 
http GET http://localhost:8088/orders
http GET http://localhost:8088/inventories
```
- Kafka Consumer 창에서 Publish 된 도메인 이벤트가 확인한다.
- 주문 후, 상품(ProductId: 1000)의 재고가 90개로 줄어든게 확인된다.
