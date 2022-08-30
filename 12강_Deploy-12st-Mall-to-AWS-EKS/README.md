# Deploy 12st-Mall on AWS EKS

- 12st-Mall 쇼핑몰을 생성한 쿠버네티스 상에 배포하고, EDA기반 비동기 통신방식으로 상호 동작하는지를 확인한다.
- 마이크로서비스 배포에 앞서, 먼저 카프카 메시징 인프라를 Kubernetes Cluster에 설치한다.


## Install Distributed Messaging Platform - Kafka


### Install K8s Package Installer Helm 
- helm이 Gitpod상에 존재하지 않을 시, helm을 설치한다. 
```
$ helm 
$ helm: command not found
```

```
curl https://raw.githubusercontent.com/helm/helm/master/scripts/get-helm-3 > get_helm.sh
chmod 700 get_helm.sh
./get_helm.sh
```

### Check k8s Context before Installing Kafka
- Helm으로 Kafka를 설치하려면 쿠버네티스 클라이언트(kubectl)가 AWS에 생성된 내 Cluster와 연결되어 있어야 한다.
- 연결확인 
```
kubectl config current-context
kubectl get all
```
- Unable to connect to the server: dial tcp: ~~~~ 어쩌구, 라고 나오면 연결이 필요하다.
- 연결하는 방법은 10강_Kubernetes-and-AWS-EKS의 README.md 중 "Configure Kubernetes Access from Gitpod"을 참조한다.

### Install Kafka with Helm
```
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update
kubectl create ns kafka
helm install my-kafka bitnami/kafka --namespace kafka
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

