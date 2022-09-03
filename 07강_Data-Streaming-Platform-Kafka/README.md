# Data Streaming Platform Kafka
- Apache Kafka는 실시간으로 기록 스트림을 게시, 구독, 저장 및 처리할 수 있는 분산 데이터 스트리밍 플랫폼 
- 최근 EDA기반 마이크로서비스 통신에 필수 Stack인 Kafka에 대해 학습한다.

- Web based 랩환경 사전설정 필수
[(Web-based Lab Environments 참고링크)](https://github.com/acmexii/msaez-labs/tree/main/06%EA%B0%95_Sample-Order-Microservice#configure-web-based-rumtime-environments)

## Preparation for Kafka Lab
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

## Kafka Topic handling
- 카프카 서버에 접속하여 터미널에서 토픽을 생성해 본다.
```
docker-compose exec -it kafka /bin/bash   # kafka docker container 내부 shell 로 진입
cd /bin
./kafka-topics --bootstrap-server http://localhost:9092 --topic topic_example --create --partitions 1 --replication-factor 1
```

- 토픽 목록 조회
```
./kafka-topics --bootstrap-server http://localhost:9092 --list 
```

## Topic Message handling
- Producer를 실행하여 토픽에 이벤트 메시지 발행하기 
```
./kafka-console-producer --broker-list http://localhost:9092 --topic topic_example

```

- VSCode의 새로운 터미널을 오픈한다.
- Kafka 서버에 접속후 Consumer 실행하여 퍼블리시된 이벤트 모니터링 
```
docker-compose exec -it kafka /bin/bash   # kafka docker container 내부 shell 로 진입
cd /bin
./kafka-console-consumer --bootstrap-server http://localhost:9092 --topic topic_example --from-beginning
```

- Producer 창에 Message를 입력하면, Consumer 창에서 Message가 조회된다.
- 각 터미널에서 Ctrl + c를 눌러 프로세스를 종료한다.

## Kafka Scaling - Kafka Partition vs. Consumers
- kafka에서 하나의 Partition은 반드시 하나의 Consumer가 매칭되어 메시지를 소비한다. 
- Topic내 Partiton 수보다 동일한 Group의 Consumer 수가 많다면, 잉여 Consumer은 partition에 binding 되지 못해 message를 Polling 하지 못하는 현상이 일어난다. 
- 아래의 Instruction을 따라 일부 Consumer가 메시지를 polling 하지 못하는 현상을 확인한다. 

### 마이크로서비스 기동
- 주문서비스가 Publish하는 도메인 이벤트를 두 상품서비스가 Subscribe하여 소비하는 샘플이다.
- 사용하는 Kafka Topic은 topic_example 이며, 현재 1개의 partition으로 설정되어 있다.
- 주문 마이크로서비스 파티션 개수와 동일하나, 상품 마이크로서비스는 파티션 보다 많은 2개를 기동된다.

- 새로운 터미널에서 Order 서비스 시작
```bash
cd 07강_Data-Streaming-Platform-Kafka
cd order
mvn spring-boot:run
```

- 새로운 터미널에서 Product1 서비스 시작
```bash
cd 07강_Data-Streaming-Platform-Kafka
cd product1
mvn spring-boot:run
```

- 새로운 터미널에서 Product2 서비스 시작
```bash
cd 07강_Data-Streaming-Platform-Kafka
cd product2
mvn spring-boot:run
```

### 로그를 통한 마이크로서비스 매칭 파티션 확인
- Product1 서비스와는 달리 Product2 마이크로서비스의 Console 창을 통해 파티션 할당이 일어나지 않았음을 확인할 수 있다.

### Kafka API를 통한 마이크로서비스 매칭 파티션 확인 
- Kafka 서버의 터미널에서 토픽정보와 Consumer 그룹정보를 확인한다.
```
./kafka-topics --bootstrap-server localhost:9092 --topic topic_example --describe
./kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group product
```


## Kafka Partition Scale out 
- Kafka Partition을 확장한다. 

```sh 
./kafka-topics --zookeeper localhost:2181 --alter --topic topic_example -partitions 2
```

- Product2 마이크로서비스를 재시작하거나 2~3분 정도 기다리면 Partition Rebalancing이 일어나면서 Product2 서비스도 partition assigned로 로깅되면서 message를 Polling할 수 있는 상태로 변경된다.
```
./kafka-topics --bootstrap-server localhost:9092 --topic topic_example --describe
./kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group product
```

> Partition 0,1 각각에 Product 마이크로서비스가 매핑된 것을 확인할 수 있다.

- Order 서비스에 POST로 메시지를 발행하면 Product 1, Product 2 서비스가 차례로 메시지를 수신한다. 

```
http POST http://localhost:8081/orders message=1st-Order
http POST http://localhost:8081/orders message=2nd-Order
http POST http://localhost:8081/orders message=3rd-Order
http POST http://localhost:8081/orders message=4th-Order
```
