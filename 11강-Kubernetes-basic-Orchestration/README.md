# Kubernetes basic Orchestration 

 AWS 상에 만들어진 Kubernetes 서버에서 쿠버네티스 오퍼레이션을 통해 쿠버네티스를 이해한다. 
 
- GitPod 환경에서 쿠버네티스 오퍼레이션을 수행하기 위해서는 GitPod에 있는 Client와 Kubernetes Server간 연결이 필요하다.

- Gitpod based 랩환경 사전설정 필수
[(Gitpod-based Lab Environments 참고링크)](https://github.com/acmexii/msaez-labs/tree/main/06%EA%B0%95_Sample-Order-Microservice#configure-web-based-rumtime-environments)

- Gitpod에서 Kubectl과 Kubernetes Cluster 연결 필수
[(Set Kubectl target Context 참고링크)](https://github.com/acmexii/msaez-labs/tree/main/10%EA%B0%95_Kubernetes-and-AWS-EKS#configure-kubernetes-access-from-gitpod)


## Container Orchestration 무작정 따라하기 

배송 마이크로서비스를 대상으로 쿠버네티스에 배포하고, 오케스트레이션을 수행한다.

### 배송서비스 생성하기 

- 배송 서비스 배포 및 확인하기
```
kubectl create deploy delivery --image=ghcr.io/acmexii/delivery-rediness:v1
```

```
kubectl get all : 생성된 객체(Deployment, ReplicaSet, Pod) 확인
kubectl get deploy -o wide : 배포에 사용된 이미지 확인
kubectl get pod -o wide : 파드가 생성된 워크노드 확인
```

- kubectl get pod 에서 Pod의 상태(STATUS)가 Running 이 아닌 경우
```
Trouble Shooting #1 : kubectl describe [Pod 객체]
Trouble Shooting #2 : kubectl logs -f [Pod 객체]
Trouble Shooting #1 : kubectl exec -it [Pod 객체] -- /bin/sh 
```
[참고사이트](http://www.msaschool.io/operation/checkpoint/check-point-one/)


### 배송서비스 삭제해 보기 

```
kubectl get pod
kubectl delete pod [Delivery Pod 객체] 
kubectl get pod
```

- Pod를 삭제해도 새로운 Pod로 서비스가 재생성됨을 확인 (Self-Healing)


### 클라우드 외부에서도 접근 가능하도록 노출하기

```
kubectl expose deploy delivery --type=LoadBalancer --port=8080
kubectl get service -w
```
- Service 정보의 External IP가 Pending 상태에서 IP정보로 변경시까지 대기하기
- 엔드포인트를 통해 서비스 확인 - http://(EXTERNAL IP정보):8080/deliveries
- Ctrl + C를 눌러 모니터링 모드 종료하기 


### 배송서비스 업데이트(v2) 하기

```
kubectl get deploy -o wide
kubectl set image deploy delivery delivery=ghcr.io/acmexii/delivery-rediness:v2
kubectl get deploy -o wide
```

- 배송서비스에 적용된 Image가 ghcr.io/acmexii/delivery-rediness:v1에서 ghcr.io/acmexii/delivery-rediness:v2로 업데이트 되었음을 확인


### 배송서비스 롤백(RollBack) 하기

```
kubectl rollout undo deploy delivery
kubectl get deploy -o wide
```

- 배송서비스에 적용된 Image가 다시 ghcr.io/acmexii/delivery-rediness:v1로 롤백되었음을 확인


### 배송서비스 인스턴스 확장(Scale-Out) 하기 (수동)

```
kubectl scale deploy delivery --replicas=3
kubectl get pod
```

- 배송서비스의 인스턴스(Pod)가 3개로 확장됨을 확인

### Declarative 한 배포방식인 YAML을 통해 배송서비스 배포하기

- 아래 YAML Spec.을 복사하여 delivery-deploy.yaml 파일로 작성한다.
```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: delivery
  labels:
    app: delivery
spec:
  replicas: 1
  selector:
    matchLabels:
      app: delivery
  template:
    metadata:
      labels:
        app: delivery
    spec:
      containers:
        - name: delivery
          image: ghcr.io/acmexii/delivery-rediness:v3
          ports:
            - containerPort: 8080
          readinessProbe:
            httpGet:
              path: '/actuator/health'
              port: 8080
            initialDelaySeconds: 15
            timeoutSeconds: 2
            successThreshold: 1
            periodSeconds: 5
            failureThreshold: 3
```

- 작성된 YAML파일로 배송서비스 v3를 배포하고 확인한다.
```
kubectl get deploy -o wide
kubectl apply -f delivery-deploy.yaml
kubectl get deploy -o wide
```


