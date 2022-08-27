package mall;
import mall.config.kafka.KafkaProcessor;
import mall.domain.Inventory;
import mall.domain.InventoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableBinding(KafkaProcessor.class)
@EnableFeignClients
public class ProductApplication {
    public static ApplicationContext applicationContext;

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(ProductApplication.class, args);

        InventoryRepository inventoryRepository = applicationContext.getBean(InventoryRepository.class);

        Inventory inventory = new Inventory();
        inventory.setProductId("1000");
        inventory.setProductName("TV");
        inventory.setStock(100);

        inventoryRepository.save(inventory);
    }
}