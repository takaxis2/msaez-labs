# Design-Level EventStorming

MSAEz 이벤트스토밍 툴을 이용하여 DDD의 구체적설계에 해당하는 Design Level EventStorming 을 수행한다.

- Design Level EventStorming은 현업담당자가 수행한 EventStorming 모델을 기반으로 서브 도메인(마이크로서비스)별 구축을 담당하는 각 수행팀(DBA, DevOps 팀, UI/UX)에서 실시한다. 
- 가시화된 서브 도메인 구현에 필요한 테크니컬한 기술요소를 설계수준에서 EventStorming Model 추가한다.
- 또한, 서브 도메인(마이크로서비스)간 통신에 필요한 커뮤니케이션(Interface) 프로토콜을 상호 조율하는 과정이 포함된다.
- 기본적인 테크니컬 설계요소까지 가미된 EventStorming 모델은 Implementation 단계시 Business Logic 등을 구체화하여 구현 완료된다.

## Process-Level EventStorming 모델이 없는 경우,
- 크롬브라우저로 https://labs.msaez.io/ 사이트에 접속하고 GIT 계정으로 로그인한다.
- 아래 쇼핑몰 Process 모델 주소에 접속한다.
- https://labs.msaez.io/#/35618409/storming/4e78411ffe9f2abd2faf073f9e026e30
- 출력된 Process-Level 모델의 상단 'FORK' 메뉴를 눌러 내 저장소로 복제하고 이를 활용하자.
 ![image](https://user-images.githubusercontent.com/35618409/187016809-4c1eaf85-f9dd-4334-822c-e76da26e3723.png)


## 12st-Mall 시나리오 
- 고객 (Customer) 이 상품을 선택하여 주문한다. (Place an Order)
- 주문이 되면 상품 배송을 한다.
- 배송이 완료가 되면 상품의 재고량이 감소한다.

- 고객이 주문을 취소할 수 있다. (Customer can cancel order)
- 주문이 취소되면 배달이 취소된다.
- 배달이 수거되면 재고량이 증가한다.

## Design-Level EventStorming Final Model 
- 시나리오가 반영된 결과모델은 다음과 같다.
![image](https://user-images.githubusercontent.com/35618409/187017511-cd1fcc6a-2ecf-4f91-9c3a-134e4f84618b.png)


12st-mall의 Process Model에서 Design Level 이벤트스토밍 모델이 정제되는 과정은 해당 영상을 참고하자. 
