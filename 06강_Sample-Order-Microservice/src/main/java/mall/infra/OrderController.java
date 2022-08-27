package mall.infra;
import mall.domain.*;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

 @RestController
 @RequestMapping(value="/orders")
 public class OrderController {
        @Autowired
        OrderRepository orderRepository;



        @RequestMapping(value = "/order",
                method = RequestMethod.POST,
                produces = "application/json;charset=UTF-8")
        public Order order(HttpServletRequest request, HttpServletResponse response,@RequestBody Order order)
                throws Exception {
                        System.out.println("##### /order/order  called #####");
                        order.order();
                        orderRepository.save(order);
                        return order;
                }
        

 }
