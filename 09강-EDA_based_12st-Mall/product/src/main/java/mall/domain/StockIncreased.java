package mall.domain;

import mall.domain.*;
import mall.infra.AbstractEvent;


public class StockIncreased extends AbstractEvent {

    private Long id;
    private String productId;
    private String productName;
    private Integer stock;

    public StockIncreased(){
        super();
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
}
