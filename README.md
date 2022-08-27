# SJCU Cloud Labs
- GitPod란, Git기반 형상서버인 Github 또는 Gitlab에서 VSCode(Visual Studio Code)기반 통합 IDE 도구를 제공해 주는 무료 Web서비스
- github 레포지토리 url앞에 gitpod.io/# 를 붙이면 바로 gitpod idle로 들어갈 수 있다.


## How to connect to GitPod Cloud Lab
https://gitpod.io/#https://github.com/sjcu-cloud/msaez-labs

- 접속 시, Gitpod 워크 스페이스가 생성되고 좀 더 기다리면 Cloud IDE 환경이 오픈
- 접속 후, Project IDE인 VSCode 메뉴에서 "Terminal > Terminal열기"


## Built-in Utilities

- 개발환경은 2022년 기준 ubuntu 20.04.3 LTS이며 go, java, python, node 등 기본 프레임워크는 설치되어 있다.
- 추가 설치가 필요한 Software는 Gitpod 접속과 동시에 실행되는 .gitpod.yml에 기술하여 설치 가능하다.
- 아래 4개의 목록은 .gitpod.yml에 정의되어 설치되도록 설정되어 있다. (init.sh)

- CLI기반 Web Client - Httpie (curl, POSTMAN 대용) 
```
sudo apt-get update
sudo apt-get install net-tools
sudo apt install iputils-ping
pip install httpie
```

- kubernetes utilities (kubectl)
```
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
```

- aws cli (aws)
```
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install
```

- eksctl 
```
curl --silent --location "https://github.com/weaveworks/eksctl/releases/latest/download/eksctl_$(uname -s)_amd64.tar.gz" | tar xz -C /tmp
sudo mv /tmp/eksctl /usr/local/bin
```

## Install Kafka for EDA-based Microservices Communication (if, necessary)
### Docker Compose 이용 (도커 있을 때 강추)

- Kafka 의 실행 (Docker Compose)
```
cd kafka
docker-compose up -d     # docker-compose 가 모든 kafka 관련 리소스를 받고 실행할 때까지 기다림
```
- Kafka 정상 실행 확인
```
$ docker-compose logs kafka | grep -i started    

kafka-kafka-1  | [2022-04-21 22:07:03,262] INFO [KafkaServer id=1] started (kafka.server.KafkaServer)
```
- Kafka consumer 접속
```
docker-compose exec -it kafka /bin/bash   # kafka docker container 내부 shell 로 진입

[appuser@e23fbf89f899 bin]$ cd /bin
[appuser@e23fbf89f899 bin]$ ./kafka-console-consumer --bootstrap-server localhost:9092 --topic petstore
```


### 로컬 설치 (비추)
- Kafka Download
```
wget https://dlcdn.apache.org/kafka/3.1.0/kafka_2.13-3.1.0.tgz
tar -xf kafka_2.13-3.1.0.tgz
```

- Run Kafka
```
cd kafka_2.13-3.1.0/
bin/zookeeper-server-start.sh config/zookeeper.properties &
bin/kafka-server-start.sh config/server.properties &
```

- Kafka Event 컨슈밍하기 (별도 터미널)
```
cd kafka_2.13-3.1.0/
bin/kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic petstore
```

## 자주 사용하는 명령어

```
netstat -lntp | grep :80 #포트확인
kill -9 `netstat -lntp|grep 808|awk '{ print $7 }'|grep -o '[0-9]*'`   # 80번대 마이크로서비스 모두 삭제
```

# Deploy to Kubernetes

## Docker 배포 관련

각 프로젝트 내에는 Dockerfile이 포함되어 있다. 이것을 빌드하기 위해서는 우선 maven 빌드로 jar 를 만들어준 후, jar를 Dockerfile 로 다시 빌드해준다.

```
cd order
mvn package -B
docker build -t order:v1 .
docker run order:v1
```

## Istall Kafka on Kubernetes Cluster (if, necessary)

### Helm 

Helm(패키지 인스톨러) 설치
- Helm 3.x 설치(권장)
```bash
curl https://raw.githubusercontent.com/helm/helm/master/scripts/get-helm-3 > get_helm.sh
chmod 700 get_helm.sh
./get_helm.sh
```

### Install Kafka with helm
```bash
helm repo update
helm repo add bitnami https://charts.bitnami.com/bitnami
helm install my-kafka bitnami/kafka
```
 