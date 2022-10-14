package mall.domain;

import mall.DeliveryApplication;
import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import mall.domain.*;

@Entity
@Table(name="Delivery_table")
public class Delivery  {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String orderId;
    private Integer qty;
    private String productId;
    private String status;
    private String customerId;    

    @PostPersist
    public void onPostPersist(){
        DeliveryStarted deliveryStarted = new DeliveryStarted();
        BeanUtils.copyProperties(this, deliveryStarted);
        deliveryStarted.publishAfterCommit();

    }
    @PreRemove
    public void onPreRemove(){
        DeliveryCanceled deliveryCanceled = new DeliveryCanceled();
        BeanUtils.copyProperties(this, deliveryCanceled);
        deliveryCanceled.publishAfterCommit();

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public static DeliveryRepository repository(){
        DeliveryRepository deliveryRepository = DeliveryApplication.applicationContext.getBean(DeliveryRepository.class);
        return deliveryRepository;
    }

    public static void cancelDelivery(OrderCanceled orderCanceled){
        Delivery delivery = repository().findByOrderId(String.valueOf(orderCanceled.getId()))
            .orElseThrow(() -> new EntityNotFoundException("Entity does not found.!!"));

        repository().delete(delivery);
    }

    public static void startDelivery(Orderplaced orderplaced){
        Delivery delivery = new Delivery();
        delivery.setOrderId(String.valueOf(orderplaced.getId()));
        delivery.setProductId(orderplaced.getProductId());
        delivery.setQty(orderplaced.getQty());
        delivery.setCustomerId(orderplaced.getCustomerId());
        delivery.setStatus(DeliveryStarted.class.getSimpleName());

        repository().save(delivery);
    }


}
