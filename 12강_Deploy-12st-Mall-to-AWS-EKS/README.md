# Deploy 12st-Mall on AWS EKS

- 12st-Mall 쇼핑몰을 생성한 쿠버네티스 상에 배포하고, EDA기반 비동기 통신방식으로 상호 동작하는지를 확인한다.
- 마이크로서비스 배포에 앞서, 먼저 메시징 인프라인 카프카를 Kubernetes Cluster에 설치한다.

- 사전 환경
  > AWS에 Kubernetes Cluster가 실행되고 있어야 한다.

  > Gitpod based 랩환경 사전설정 필수
[(Gitpod-based Lab Environments 참고링크)](https://github.com/acmexii/msaez-labs/tree/main/06%EA%B0%95_Sample-Order-Microservice#configure-web-based-rumtime-environments)

  > Gitpod에서 Kubectl과 Kubernetes Cluster 연결 필수
[(Set Kubectl target Context 참고링크)](https://github.com/acmexii/msaez-labs/tree/main/10%EA%B0%95_Kubernetes-and-AWS-EKS#configure-kubernetes-access-from-gitpod)

## Install Distributed Messaging Platform - Kafka

- 네임스페이스 kafka를 생성한 다음, 해당 네임스페이스에 Kafka를 설치한다.

### Create Namespace
```
kubectl create namespace kafka
```

### Install Kafka with Deployment YAML
- 아래 커맨드를 복사하여 Kafka 설치 전, Zookeeper를 먼저 설치한다.
```
kubectl apply -f https://raw.githubusercontent.com/acmexii/demo/master/edu/zookeeper.yaml -n kafka
```
- 이어서, 아래 커맨드로 Kafka를 먼저 설치한다.
```
kubectl apply -f https://raw.githubusercontent.com/acmexii/demo/master/edu/zookeeper.yaml -n kafka
```

- 잠시뒤, 아래 Command로 Kubernetes에 설치된 Kafka Stack을 확인할 수 있다.
```
kubectl get all -n kafka
```

## Deploy 12st-Mall Microsevices to Kubernetes

### 주문 마이크로서비스 배포
```
cd order
mvn package -B -DskipTests
docker build -t Docker-ID/order:v1 .
docker push Docker-ID/order:v1
```
- docker denied 오류가 발생하면 “docker login” 명령으로 Token을 생성하여 docker에 Credential을 주입하고 다시 Push한다.
```
docker login
```

- kubernetes 하위 폴더로 이동하여 deployment.yml 파일의 배포 Spec. 중 19라인을 내 이미지 정보로 수정하고 저장한다.
```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order
  labels:
    app: order
spec:
  replicas: 1
  selector:
    matchLabels:
      app: order
  template:
    metadata:
      labels:
        app: order
    spec:
      containers:
        - name: order
          image: username/order:latest    # Replace this value with my Docker Image Name.
          ports:
            - containerPort: 8080
```

- 12st-Mall 을 위한 네임스페이스를 생성하고 주문서비스를 배포한다.
```
kubectl create namespace mall
kubectl apply -f deployment.yml -n mall
kubectl apply -f service.yaml -n mall
```

### Deploy Delivery, Product Microservice 
- 주문서비스와 동일한 방식으로 나머지 서비스를 배포한다.
- 네임스페이스 mall은 추가적으로 생성하지 않아도 된다.
```
cd oooooo
mvn package -B -DskipTests
docker build -t Docker-ID/????:v1 .
docker push Docker-ID/????:v1
```

### Deploy Gateway Microservice 
- 모든 마이크로서비스들의 단일 진입점인 게이트웨이(Gateway Service)도 배포한다.
```
cd oooooo
mvn package -B -DskipTests
docker build -t Docker-ID/gateway:v1 .
docker push Docker-ID/gateway:v1
```

## Testing 12st-Mall 

상품마이크로서비스를 실행하면 초기 상품(No. 1000)의 재고가 100개로 등록된다.
게이트웨이 마이크로서비스의 End point를 추출하고, 모든 요청은 게이트웨이를 경유해 각 서비스로 유입되도록 한다.

### Acquire Gateway Endpoint
- Kubectl 커맨드로 게이트웨이 서비스의 EXTERNAL-IP를 복사한다. (서비스포트는 8080 임)
```
kubectl get service -n mall
```

### Request REST Calls (Order, Cancel Order)

- 주문생성
```
http POST http://GATEWAY-EXTERNAL-IP:8080/orders productId=1000 productName=TV qty=5 customerId=5
```
- 상품재고 확인
```
http GET http://GATEWAY-EXTERNAL-IP:8080/inventories
```
- 주문취소
```
http DELETE http://GATEWAY-EXTERNAL-IP:8080/orders/1
```
- 상품재고 확인
```
http GET http://GATEWAY-EXTERNAL-IP:8080/inventories
```

### Domain Events monitoring via Kafka Client

- 카프카 클라이언트를 실행하여 각 서비스에서 Publish된 도메인 이벤트 확인
```
kubectl run my-kafka-client --restart='Never' --image docker.io/bitnami/kafka:2.8.0-debian-10-r0 --namespace kafka --command -- sleep infinity
kubectl exec --tty -i my-kafka-client --namespace kafka -- bash
kafka-console-consumer.sh --bootstrap-server my-kafka.kafka.svc.cluster.local:9092 --topic mall --from-beginning
```

