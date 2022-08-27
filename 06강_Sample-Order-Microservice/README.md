# Implementation of Order Microservice

- Process-Level과 Design-Level 이벤트스토밍을 거쳐 설계된 모델은 이벤트스토밍 Sticker별 Spring-boot 언어의 구현체로 매핑된다. 
- 이번 학습에서는 12st-Mall 중, Order Microservice의 구현체 코드를 살펴보고 웹 실행환경에서 실행해 본다.
- 실행에 앞서, 로컬에 웹기반 런타임 환경(Gitpod)을 구성한다. 


## Configure Web-based Rumtime Environments
- GitPod란, Git기반 형상서버인 Github, 또는 Gitlab상에서 VSCode(Visual Studio Code) 통합 IDE 도구를 제공해 주는 무료 Web서비스
- github 레포지토리 url앞에 gitpod.io/# 를 붙이면 바로 gitpod idle로 들어갈 수 있다.

### Step #1.
- Gihub 가입 : https://github.com 도메인에 회원가입(sign-up) 후, 반드시 입력한 메일주소에서 확인까지 수행

### Step #2.
- 클라우드서비스(Microservices) Lab을 내 GitHub 계정으로 복사
  - 먼저, Chrome 브라우저를 실행하고 Github에 로그인한다.
  - https://github.com/acmexii/msaez-labs에 접속 후, 해당 리소스를 Fork하여 나의 Git계정으로 복사한다.
![image](https://user-images.githubusercontent.com/35618409/187021900-f0285913-5fab-4ab0-9fe9-a9a4d75a2618.png)
  - Fork가 완료되면 Github URL이 내 계정의 리소스로 페이지가 전환된다.

### Step #3.
- 클라우드서비스(Microservices) Github에 접속: https://github.com/MY-GIT-ACCOUNT/msaez-labs
  - Github 페이지가 로딩되고 나면, 도메인 URL 앞에 https://gitpod.io/# 을 추가 후, 새창에서 재접속해 본다.
  - https://gitpod.io/#https://github.com/MY-GIT-ACCOUNT/msaez-labs

### Step #4.
- Gitpod 인증화면이 나타나며 Gihub 계정정보를 입력한다.
![image](https://user-images.githubusercontent.com/35618409/187013335-cee187a1-cd43-4752-b881-424af1a9f2f9.png)
- Gitpod 로고가 중앙에 나타나며 접속이 진행된다.
- 성공적으로 접속이 이루어지면 VSCode 통합 IDE가 나타난다.
![image](https://user-images.githubusercontent.com/35618409/187012423-53229178-9221-492f-bf75-b493e99782be.png)
- 왼쪽(Explorer) 영역에는 Cloud Lab Gihub 리소스 목록이, 오른쪽에는 편집기와 터미널이 위치해 있다.

## Step #5.
- 본 학기에 사용할 12st-mall 마이크로서비스는 Spring-boot 언어를 사용한다.
- VSCode에 Java 언어와 연관된 Extention Pack(Plugin)을 설치한다.  
  - 왼쪽(Explorer) 영역에서 06강 폴더를 선택 후, Ctrl + p를 누른다.
  - 팝업 창에서 Order.java를 검색하여 조회된 결과 중, 06강 하위 리소스를 선택하면 오른쪽 편집기상에 열리는데, 이때 VSCode가 추천하는 Plugin을 설치한다.
  - 조금 뒤, 편집기에 로딩된 Order.java 리소스에 설치한 확장팩이 적용된다. (일부 코드의 컬러가 바뀜)
![image](https://user-images.githubusercontent.com/35618409/187012911-455568d1-e20f-4d30-9ac6-03e32fd1de08.png)



## Running Order Microservice
- Gitpod 웹환경에서 먼저 Order 마이크로서비스가 사용하는 분산 메시지 큐인 Kafka를 실행한다. 
```
cd kafka
docker-compose up -d     # docker-compose 가 모든 kafka 관련 리소스를 받고 실행할 때까지 기다림
```

- 주문 마이크로서비스를 실행하고, REST 커맨드를 통해 주문과 주문취소 요청을 보낸다.
- Kakfa 내 메시지 저장소인 Topic(name : mall)에 각각 커맨드에 대해 OrderPlaced, OrderCanceled 도메인 이벤트가 Publish됨을 확인한다.

### Order Microservice 실행
- 새로운 Terminal을 연다.
```
cd 06강_Sample-Order-Microservice
mvn spring-boot:run
```

### Order Microservice에 Command 전송
```
http GET http://localhost:8080
http GET http://localhost:8080/orders
http POST http://localhost:8080/orders customerId=1000 customerName=gdhong productId=1000 qty=10 address=seoul
http GET http://localhost:8080/orders
http DELETE http://localhost:8080/orders/1
http GET http://localhost:8080/orders
```

### Command로 인해 Publish된 Domain Events 확인
- Kafka consumer 접속 및 mall Topic 조회
```
docker-compose exec -it kafka /bin/bash   # kafka docker container 내부 shell 로 진입
[appuser@e23fbf89f899 bin]$ cd /bin
[appuser@e23fbf89f899 bin]$ ./kafka-console-consumer --bootstrap-server localhost:9092 --topic mall --from-beginning
```
