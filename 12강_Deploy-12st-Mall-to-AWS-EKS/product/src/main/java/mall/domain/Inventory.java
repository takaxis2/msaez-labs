package mall.domain;

import mall.domain.StockDecreased;
import mall.domain.StockIncreased;
import mall.ProductApplication;
import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;


@Entity
@Table(name="Inventory_table")
public class Inventory  {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String productId;
    private String productName;
    private Integer stock;
    private String cmdType;

    @PostUpdate
    public void onPostUpdate(){
        if (this.cmdType.equals("minus")) {
            StockDecreased stockDecreased = new StockDecreased();
            BeanUtils.copyProperties(this, stockDecreased);
            stockDecreased.publishAfterCommit();

        } else if (this.cmdType.equals("plus")) {
            StockIncreased stockIncreased = new StockIncreased();
            BeanUtils.copyProperties(this, stockIncreased);
            stockIncreased.publishAfterCommit();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getCmdType() {
        return cmdType;
    }

    public void setCmdType(String cmdType) {
        this.cmdType = cmdType;
    }


    public static InventoryRepository repository(){
        InventoryRepository inventoryRepository = ProductApplication.applicationContext.getBean(InventoryRepository.class);
        return inventoryRepository;
    }

    public static void stockDecrease(DeliveryStarted deliveryStarted){

        Inventory inventory = repository().findByProductId(deliveryStarted.getProductId()).get();
        inventory.setStock(deliveryStarted.getQty());

        repository().save(inventory);
    }

    public static void stockIncrease(DeliveryCanceled deliveryCanceled){

        Inventory inventory = repository().findByProductId(deliveryCanceled.getProductId()).get();
        inventory.setStock(deliveryCanceled.getQty());

        repository().save(inventory);
    }

}
