# Process-Level EventStorming

MSAEz 이벤트스토밍 툴을 이용하여 DDD의 개념설계에 해당하는 Process Level EventStorming 을 수행한다.

- Process Level EventStorming은 EventStorming 을 수행하는 StakeHolder 중 현업담당자(Domain Expert)가 주도적으로 수행한다. 
- 도메인(업무영역)에서 일어나는 Domain 이벤트를 중심으로 업무영역을 서브 도메인으로 가시화해 나가는 기법이다.
- 이때, 서비스 구축팀은 facilitator로서 현업담당자의 EventStorming 과정을 조율하고 모델을 공유한다.
- 우리는 본 학기동안 E-Commerce 도메인(12st-mall)을 주제로 클라우드서비스(마이크로서비스)를 학습한다. 

## 12st-Mall 시나리오 
- 고객 (Customer) 이 상품을 선택하여 주문한다. (Place an Order)
- 주문이 되면 상품 배송을 한다.
- 배송이 완료가 되면 상품의 재고량이 감소한다.

- 고객이 주문을 취소할 수 있다. (Customer can cancel order)
- 주문이 취소되면 배달이 취소된다.
- 배달이 수거되면 재고량이 증가한다.

## Process-Level EventStorming Final Model 
- 시나리오가 반영된 결과모델은 다음과 같다.
![image](https://user-images.githubusercontent.com/35618409/187015214-90018c5a-80f8-47cd-9aaa-4030ff098873.png)

- 현업담당자(Domain Expert)를 중심으로 시나리오를 만족하는 3개의 서브 도메인이 가시화되었고,
- 각 서브 도메인간의 상호 커뮤니케이션이 Type이 Pub/Sub의 약결합으로 확인된다.

시나리오에서 이벤트스토밍 모델이 정제되는 과정은 해당 영상을 참고하자. 