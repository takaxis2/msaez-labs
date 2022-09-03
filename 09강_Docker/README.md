# Docker

Docker는 Runtime상에서 Host OS의 Kernel을 공유해 많은 수의 컨테이너 인스턴스를 운영하도록 지원하는 컨테이너 기반 가상화 솔루션이다.
마이크로서비스는 Docker Image로 빌드되고, Image가 실행된 형상을 Container라고 한다.
마이크로서비스가 컨테이너로 실행되었을 때, 해당 컨테이너가 제공하는 서비스에 접속가능하다.

- 사전 환경
  > Gitpod based 랩환경 사전설정 필수
[(Gitpod-based Lab Environments 참고링크)](https://github.com/acmexii/msaez-labs/tree/main/06%EA%B0%95_Sample-Order-Microservice#configure-web-based-rumtime-environments)

## Docker Lab 무작정 따라하기   

- Docker Lab을 위해서는 도커허브 계정이 필요하다. 
- https://hub.docker.com 접속
  - 가입(Sign-Up) 및 E-Mail verification 수행  
  
- 내 GitPod 환경에 접속하여 설치된 Docker Runtime을 활용해 Lab을 수행한다.

### 이미지 기반 컨테이너 생성해 보기

```
docker image ls
docker run --name my-nginx -d -p 8080:80 nginx:latest
docker run --name my-new-nginx -d -p 8081:80 nginx:latest

docker image ls
docker container ls
```  

- 생성된 컨테이너 서비스 확인
```
http GET :localhost:8080
http GET :localhost:8081
```


### 컨테이너와 이미지 삭제하기

- 삭제하려는 이미지를 사용하는 컨테이너 정리가 우선

```
docker container ls 
docker container stop my-nginx
docker container stop my-new-nginx
docker container rm my-nginx
docker container rm my-new-nginx
docker image rm nginx
docker image ls
```


### 이미지 빌드 & 이미지 저장소(Hub.docker.com)에 푸시하기

도커 이미지를 생성하려면, 소스코드와 이미지 빌드에 필요한 스크립트(Dockerfile)가 필요하다.

- 소스코드(html) 생성  
```
vi index.html
# 아래 내용 입력 후, 저장종료(:wq)
Hi~ My name is Hong Gil-Dong...~~~
```

- 빌드 스크립트(Dockerfile) 생성
```
vi Dockerfile
```
- 아래 내용 입력 후, 저장종료(:wq)
```
FROM nginx
COPY index.html /usr/share/nginx/html/
```

- 이미지 빌드하기
```
docker build -t MY-DockerHub-ID/welcome:v1 .
docker image ls
```

- 이미지 원격 저장소에 푸시하기
```
docker login 
docker push MY-DockerHub-ID/welcome:v1
```  

#### Docker Hub에 생성된 이미지 확인  

- https://hub.docker.com 접속
- repositories 메뉴 Reload 후 Push된 이미지 확인


#### Docker Hub 이미지 기반 컨테이너 생성  

```
docker image rm MY-DockerHub-ID/welcome:v1
docker run --name=welcome -d -p 8080:80 MY-DockerHub-ID/welcome:v1
```  
