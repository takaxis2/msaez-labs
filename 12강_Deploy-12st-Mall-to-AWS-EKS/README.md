# Deploy 12st-Mall on AWS EKS(Elastic Kubernetes Service)

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
helm repo update
helm repo add bitnami https://charts.bitnami.com/bitnami
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
- docker denied 오류가 발생하면 “docker login” 명령으로 Token을 생성하여 docker에 Credential을 주입해 준다.
