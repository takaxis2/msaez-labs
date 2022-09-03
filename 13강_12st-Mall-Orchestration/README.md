# 12st-Mall Orchestration with Kubernetes

AWS 상에 배포된 12st-Mall을 활용하여 "자동 인스턴스 확장(Auto Scaling)"과 "무정지 배포(Zero downtime Deploy)"를 GitPod환경에서 실습한다. 
 
- 사전 환경
  - AWS에 Kubernetes Cluster가 실행되고 있어야 한다.
  - Gitpod based 랩환경 사전설정 필수
[(Gitpod-based Lab Environments 참고링크)](https://github.com/acmexii/msaez-labs/tree/main/06%EA%B0%95_Sample-Order-Microservice#configure-web-based-rumtime-environments)

  - Gitpod에서 Kubectl과 Kubernetes Cluster 연결 필수
[(Set Kubectl target Context 참고링크)](https://github.com/acmexii/msaez-labs/tree/main/10%EA%B0%95_Kubernetes-and-AWS-EKS#configure-kubernetes-access-from-gitpod)


## Auto Scaling 

주문 요청이 쇄도할때, 쿠버네티스가 Order 마이크로서비스의 컨테이너 개수를 자동으로 조정하여 Workload를 분산 수행한다.

### 주문서비스 배포하기 

- 현재, 실행중인 서비스 객체를 조회하고 삭제한다.
```
kubectl get all
kubectl delete deploy,service --all
```

- 주문서비스 배포하기
- 아래 YAML Spec.을 복사하여 order-deploy.yaml 파일로 작성한다.
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
          image: apexacme/order:v1
          ports:
            - containerPort: 8080
          resources:
            requests:
              cpu: 200m
```

- 작성된 order-deploy.yaml 을 실행하고, order Service 를 생성한다.
```
kubectl apply -f order-deploy.yaml
kubectl expose deploy order --port=8080
kubectl get all
```

### 주문서비스에 Auto Scaling 설정하기 

```
kubectl autoscale deployment order --cpu-percent=50 --min=1 --max=10
```

- 주문서비스에 부하(Workload) 주기
- 아래 스크립트를 실행하여 부하발생기(Siege Container) 생성
```
kubectl apply -f - <<EOF
apiVersion: v1
kind: Pod
metadata:
  name: siege
spec:
  containers:
  - name: siege
    image: apexacme/siege-nginx
EOF
```

### 주문서비스에 부하발생 후, Auto Scaling 결과 확인하기
```
kubectl exec -it siege -- /bin/bash
siege -c20 -t40S -v http://order:8080/orders
exit
```
- 늘어난 Order Pod 인스턴스 확인
```
kubectl get pod -l app=order
```


## Zero downtime Deploy 

배송서비스가 Rollout(새 버전의 배송 컨테이너가 생성)될 때, 쿠버네티스가 실시간 Downtime 없는 서비스 업데이트를 지원한다.

### 배송서비스 배포하기 
```
kubectl apply -f https://raw.githubusercontent.com/acmexii/demo/master/edu/delivery-rediness-v1.yaml
kubectl expose deploy delivery --port=8080
```

### 무정지배포 설정이 없는 배송서비스 v2로 테스트

- 현재 배포된 버전(v1)확인하기 
```
kubectl get deploy -o wide
```

- Siege 컨테이너 내부에서 배송서비스 길게(80 초) 접속해 놓기
```
kubectl exec -it siege -- /bin/bash
siege -v -c1 -t50S http://delivery:8080/deliveries
```

- 새로운 터미널에서 새 버전(v2)의 배송서비스 배포하기
- (무정지배포 설정이 없는 배송서비스 v2 - 웹상에서 확인해 보기)
```
kubectl apply -f https://raw.githubusercontent.com/acmexii/demo/master/edu/delivery-no-rediness-v2.yaml
```

### 무정지 배포 실패
- 배송서비스에 길게(50 초) 접속하고 있는 Siege 터미널에서 Network 오류가 발생한다.
- 즉, 새 버전으로 전개되는 과정에 클라이언트로 오류가 발현된다.


### 무정지배포 설정이 추가된 배송서비스 v3로 테스트

- 현재 배포된 버전(v2)확인하기 
```
kubectl get deploy -o wide
```

- Siege 컨테이너 내부에서 배송서비스 길게(80 초) 접속해 놓기
```
kubectl exec -it siege -- /bin/bash
siege -v -c1 -t50S http://delivery:8080/deliveries
```

- 새로운 터미널에서 새 버전(v3)의 배송서비스 배포하기
- (무정지배포 설정이 추가된 배송서비스 v3 - 웹상에서 확인해 보기)
```
kubectl apply -f https://raw.githubusercontent.com/acmexii/demo/master/edu/delivery-rediness-v3.yaml
```

### 무정지 배포 실현
- 배송서비스에 길게(50 초) 접속하고 있는 Siege 터미널에서 Network 오류가 발현하지 않는다.
- Siege Script 수행결과 Availibility가 100%로 나타난다.

- 즉, 새 버전으로 전개되는 과정에 무정지 배포(Zero downtime Deploy)가 실현되었다.



