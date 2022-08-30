# Kubernetes basic Orchestration 

- AWS 상에 만들어진 Kubernetes 서버에 GitPod 환경에서 접속하여 운영과 관련된 Command를 실행해 본다.
- 나의 Cloud Lab GitPod환경에 먼저 접속한다.


### Container Orchestration 무작정 따라하기 

#### 주문서비스 생성하기 

- 도커 허브에 저장된 주문 이미지으로 서비스 배포 및 확인하기

```
kubectl create deploy order --image=apexacme/order
kubectl get all
```

- Docker Hub에 Push한 나만의 이미지로 쿠버네티스에 배포해 보기
```
- kubectl get all : 생성된 객체(Pod, Deployment, ReplicaSet) 확인
- kubectl get deploy -o wide : 배포에 사용된 이미지 확인
- kubectl get pod -o wide : 파드가 생성된 워크노드 확인
```

- kubectl get pod 에서 Pod의 상태(STATUS)가 Running 이 아닌 경우
```
  - Trouble Shooting #1 : kubectl describe [Pod 객체]
  - Trouble Shooting #2 : kubectl logs -f [Pod 객체]
  - Trouble Shooting #1 : kubectl exec -it [Pod 객체] -- /bin/sh 
```
[참고사이트](http://www.msaschool.io/operation/checkpoint/check-point-one/)


#### 주문서비스 삭제해 보기 

```
kubectl get pod
kubectl delete pod [order Pod 객체] 
kubectl get pod
```

- Pod를 삭제해도 새로운 Pod로 서비스가 재생성됨을 확인


#### 클라우드 외부에서도 접근 가능하도록 노출하기

```
kubectl expose deploy order --type=LoadBalancer --port=8080
kubectl get service -w
```

- Service 정보의 External IP가 Pending 상태에서 IP정보로 변경시까지 대기하기
- 엔드포인트를 통해 서비스 확인 - http://(IP정보):8080/orders
- Ctrl + C를 눌러 모니터링 모드 종료하기 


#### 주문서비스 업데이트(v2) 하기

```
kubectl get deploy -o wide
kubectl set image deploy order order=apexacme/order:v2
kubectl get deploy -o wide
```

- 주문서비스에 적용된 Image가 apexacme/order에서 apexacme/order:v2로 업데이트 되었음을 확인


#### 주문서비스 롤백(RollBack) 하기

```
kubectl rollout undo deploy order
kubectl get deploy -o wide
```

- 주문서비스에 적용된 Image가 apexacme/order로 롤백되었음을 확인



#### 주문서비스 인스턴스 확장(Scale-Out) 하기 (수동)

```
kubectl scale deploy order --replicas=3
kubectl get pod
```

- 주문서비스의 인스턴스(Pod)가 3개로 확장됨을 확인


#### YAML 기반 서비스 배포하기

- Cloud IDE 메뉴 > File > Folder > YAML 입력
- 생성한 폴더 하위에 아래 파일 생성
- Cloud IDE 메뉴 > File > New File > order.yaml 입력 
- 아래 내용 복사하여 붙여넣기

```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-by-yaml
  labels:
    app: order
spec:
  replicas: 2
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
          image: apexacme/order:latest
          ports:
            - containerPort: 8080        
```

- 입력 후, 저장
```
- kubectl apply -f order.yaml 
- kubectl get all 
```


### 상세설명
<iframe width="100%" height="100%" src="https://www.youtube.com/embed/r8oRinKA01o" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
