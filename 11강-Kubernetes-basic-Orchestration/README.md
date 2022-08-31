# Kubernetes basic Orchestration 

 AWS 상에 만들어진 Kubernetes 서버에서 기본적인 쿠버네티스 오퍼레이션을 통해 쿠버네티스를 이해한다. 
 
- GitPod 환경에서 쿠버네티스 오퍼레이션을 수행하기 위해서는 GitPod에 있는 Client와 Kubernetes Server간 연결이 필요하다.
- 연결하는 방법은 10강_Kubernetes-and-AWS-EKS의 README.md 중 "Configure Kubernetes Access from Gitpod"을 참조한다.


### Container Orchestration 무작정 따라하기 

#### 배송서비스 생성하기 

- 배송 서비스 배포 및 확인하기

```
kubectl apply -f https://raw.githubusercontent.com/acmexii/demo/master/edu/delivery-rediness-v1.yaml
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


#### 배송서비스 삭제해 보기 

```
kubectl get pod
kubectl delete pod [order Pod 객체] 
kubectl get pod
```

- Pod를 삭제해도 새로운 Pod로 서비스가 재생성됨을 확인


#### 클라우드 외부에서도 접근 가능하도록 노출하기

```
kubectl expose deploy delivery --type=LoadBalancer --port=8080
kubectl get service -w
```

- Service 정보의 External IP가 Pending 상태에서 IP정보로 변경시까지 대기하기
- 엔드포인트를 통해 서비스 확인 - http://(IP정보):8080/orders
- Ctrl + C를 눌러 모니터링 모드 종료하기 


#### 배송서비스 업데이트(v2) 하기

```
kubectl get deploy -o wide
kubectl apply -f https://raw.githubusercontent.com/acmexii/demo/master/edu/delivery-no-rediness-v2.yaml
kubectl get deploy -o wide
```

- 배송서비스에 적용된 Image가 apexacme/order에서 apexacme/order:v2로 업데이트 되었음을 확인


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

