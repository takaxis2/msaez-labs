package kafkatest;

import kafkatest.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    @Autowired ProductRepository productRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderPlaced_PrintMessage(@Payload OrderPlaced orderPlaced){

        if(!orderPlaced.validate()) return;

        System.out.println("\n\n##### listener PrintMessage : " + orderPlaced.toJson() + "\n\n");


        

        // Sample Logic //
        // Product product = new Product();
        // productRepository.save(product);

    }


}


