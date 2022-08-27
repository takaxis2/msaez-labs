package mall.infra;

import mall.config.kafka.KafkaProcessor;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import mall.domain.*;

@Service
public class PolicyHandler{
    @Autowired InventoryRepository inventoryRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverDeliveryStarted_StockDecrease(@Payload DeliveryStarted deliveryStarted){

        if(!deliveryStarted.validate()) return;
        DeliveryStarted event = deliveryStarted;
        System.out.println("\n\n##### listener StockDecrease : " + deliveryStarted.toJson() + "\n\n");
        // Sample Logic //
        Inventory inventory =  inventoryRepository.findByProductId(deliveryStarted.getProductId())
            .orElseThrow(() -> new EntityNotFoundException("Entity does not found.!!"));
            
        inventory.setStock(inventory.getStock() - deliveryStarted.getQty());
        inventory.setCmdType("minus");

        inventoryRepository.save(inventory);
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverDeliveryStarted_StockIncrease(@Payload DeliveryCanceled deliveryCanceled){

        if(!deliveryCanceled.validate()) return;
        DeliveryCanceled event = deliveryCanceled;
        System.out.println("\n\n##### listener StockDecrease : " + deliveryCanceled.toJson() + "\n\n");
        // Sample Logic //
        Inventory inventory =  inventoryRepository.findByProductId(deliveryCanceled.getProductId())
            .orElseThrow(() -> new EntityNotFoundException("Entity does not found.!!"));

        inventory.setStock(inventory.getStock() + deliveryCanceled.getQty());
        inventory.setCmdType("plus");

        inventoryRepository.save(inventory);
    }

}


