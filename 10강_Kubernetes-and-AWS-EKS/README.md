# Kubernetes(AWS EKS) Provisioning

Academy Learner Lab을 통해 아마존 AWS에 Kubernetes를 설치하고 쿠버네티스 클라이언트와 연동하는 내용이다. 

## Connect to AWS Learner Lab
- AWS Academy Lab(https://awsacademy.instructure.com/)에 접속한다.
- 대쉬보드 > 모듈 > Learner Lab - Associate Services 클릭한다.
![image](https://user-images.githubusercontent.com/35618409/187118228-d5a56653-ca93-440f-a855-18a72630c12e.png)
- 아래와 같이 터미널이 출력된다.
![image](https://user-images.githubusercontent.com/35618409/187118347-117ab92b-450d-4e6b-a3da-c5f4d5e90e91.png)
- 터미널 상단의 "▶Start Lab"을 클릭하여 터미널을 활성화한다.
- 터미널 상단의 "AWS" 링크를 클릭하여 AWS Console에 접속한다.
![image](https://user-images.githubusercontent.com/35618409/187118466-12b742e2-7323-494a-8572-df2c22aa53b9.png)
  - 접속지역은 미국동부(버지니아 북부, us-east-1)이다. 화면 우측상단에서 확인가능
- 화면 중앙의 서비스 검색란에 Kubernetes를 입력하고 검색된 Elastic Kubernetes Service를 선택한다.
![image](https://user-images.githubusercontent.com/35618409/187118637-0a60e652-cffa-44f3-a8ff-cb2a931e6bb0.png)
- '클러스터 추가' > '생성'를 클릭한다.


## Setting Kubernetes Cluster Information

### 클러스터 구성
- 클러스터 이름을 영문으로 입력한다.
  - 예시: gdhong-eks
  - gdhong 대신에 나의 정보로 수정한다. (Sample: lily-eks)
- Kubernetes 버전과 Role을 디폴트설정으로 두고 '다음'을 클릭한다.
![image](https://user-images.githubusercontent.com/35618409/187119325-0578886e-d4ea-40b7-8b93-c0ae911b905c.png)

### 네트워킹 지정
- VPC를 기본값으로 둔다. 
- 서브넷 구성에서 목록을 열어 us-east-1a, us-east-1b, us-east-1c만 선택되도록 한다.
![image](https://user-images.githubusercontent.com/35618409/187119661-d464d3f7-4072-44e4-a61b-d5802ae6efa9.png)

- 보안그룹 설명에서 새보안 그룹을 생성(Ctrl + 'VPC콘솔' 클릭) 한다.
![image](https://user-images.githubusercontent.com/35618409/187119829-afbd28a7-11e8-4faa-a246-30cb20d328d2.png)

- 오픈된 창에서 보안그룹 생성을 클릭한다.
- 보안그룹 이름에 나만의 SecurityGroup명을 입력한다. (예시, gdhong-securitygroup)
- 인바운드 규칙에서 '규칙 추가'를 클릭한다.
- 유형에서 '모든 트래픽', 소스 유형은 'Anywhere-IPv4'를 선택한다.
- 보안그룹 생성을 클릭해 정상적으로 생성되면 창을 닫는다.
>> 보안그룹 Reload를 눌러 방금 생성한 보안그룹을 지정한다.
>> 나머지 설정을 Default로 두고 '다음'을 클릭한다.

### 로깅 구성
>> 모두 비활성화 상태에서 '다음'을 클릭한다.

### 검토 및 생성
>> 설정 확인 후, 최종 생성을 위해	'생성'을 클릭한다.


★ [3/6] 5~10분 후 Kubernetes 클러스터 생성이 완료된다. ----------
	> 클러스터 메뉴에 생성된 gdhong-eks 목록이 확인된다.
	> gdhong-eks를 클릭해 'Compute(컴퓨팅)' 탭을 눌러 노드그룹을 추가한다. 



★ [4/6] 클러스터에 노드 그룹 추가 ------------------------------	
	> 'Compute(컴퓨팅)' 탭의 '노드그룹추가' 를 클릭한다.

	> 노드 그룹 구성
		>> 그룹명에는 gdhong-eks-NodeGroup 을 입력한다.
			- 템플릿 : 클러스터명-NodeGroup
		>> 노드 IAM은 LabRole을 선택한다.
		>> Kubernetes 레이블에서 레이블 추가를 눌러서, 
			- 키 에는 'worker', 값 에는 'gdhong-NodeGroup'을 입력한다. (필수)

	> 컴퓨팅 및 조정 구성 설정
		>> 쿠버네티스 클러스터를 구성하는 워크노드(VM, EC2) 관련 설정이다.
		>> 모든 값을 default로 둔다.

	> 네트워킹 지정
		>> VPC 서브넷 설정을 기본으로 둔다.

	> 검토 및 생성
		>> 설정 확인 후, 최종 생성을 위해 	'생성'을 클릭한다.


★ [5/6] 5~10분 후 Kubernetes 클러스터의 워크노드 생성이 완료된다. ----------
	> 클러스터 메뉴에 생성된 gdhong-eks 목록이 확인된다.
	> gdhong-eks를 클릭해 'Compute(컴퓨팅)' 탭의 생성된 워크노드을 확인한다. 



★ [6/6] 쿠버네티스 클라이언트를 설치 및 클러스터 접속 ---------------------------------
	> Academy Lab상에 AWS 쿠버네티스 스텍인 eksctl과 kubectl 을 설치한다.

	> eksctl 설치 - 아래 순으로 AWS 클러스터 컨트롤러를 Lab에 설치한다.
		>> curl --silent --location "https://github.com/weaveworks/eksctl/releases/latest/download/eksctl_$(uname -s)_amd64.tar.gz" | tar xz -C /tmp
		>> mkdir -p $HOME/bin && cp /tmp/eksctl $HOME/bin && export PATH=$PATH:$HOME/bin
		>> echo 'export PATH=$PATH:$HOME/bin' >> ~/.bashrc


	> kubectl 설치 - 아래 순으로 쿠버네티스 클라이언트를 Lab에 설치한다.
		>> curl -o kubectl https://amazon-eks.s3.us-west-2.amazonaws.com/1.21.2/2021-07-05/bin/linux/amd64/kubectl
		>> chmod +x ./kubectl
		>> mkdir -p $HOME/bin && cp ./kubectl $HOME/bin/kubectl && export PATH=$PATH:$HOME/bin
		>> echo 'export PATH=$PATH:$HOME/bin' >> ~/.bashrc


	> kubernetes Cluster 접속 및 테스트
		>> [1/6] 단계에서 aws 클라이언트가 접속한 리전을 재확인한다.
		>> [2/6] 단계에서 생성한 클러스터 이름을 입력한다.
		>> Lab에 설치된 aws 클라언트를 사용하여 쿠버네티스 서버와 클라이언트를 SSO한다.

		>> aws eks --region us-east-1 update-kubeconfig --name gdhong-eks
			- 정상완료되면,
			- Updated context arn:aws:eks:us-east-1:~~cluster/gdhong ~~~/.kube/config
			- 메시지가 나타난다.

		>> 설정확인
			- kubectl get all 입력시, 아래 내용이 조회되면 테스트가 성공한 것이다.
			- service/kubernetes   ClusterIP   10.100.0.1   <none>        443/TCP   58m
