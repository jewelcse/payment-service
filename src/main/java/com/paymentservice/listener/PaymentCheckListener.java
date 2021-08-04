package com.paymentservice.listener;

import com.paymentservice.config.PaymentConfig;
import com.paymentservice.dto.OrderDto;
import com.paymentservice.model.Payment;
import com.paymentservice.model.OrderStatus;
import com.paymentservice.model.PaymentStatus;
import com.paymentservice.service.PaymentServiceImpl;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class PaymentCheckListener {

    private RabbitTemplate rabbitTemplate;
    private PaymentServiceImpl paymentService;

    @Autowired
    public PaymentCheckListener(RabbitTemplate rabbitTemplate,
                                PaymentServiceImpl paymentService){
        this.rabbitTemplate = rabbitTemplate;
        this.paymentService = paymentService;
    }


    @RabbitListener(queues = PaymentConfig.ORDER_CREATE_QUEUE)
    public void checkCredits(OrderDto orderDto){

        String bankAccountNumber = orderDto.getAccountNumber();
        double totalCost = orderDto.getAmount();
        // bank api called with account number and total cost

        String transId = "1010";

        Payment payment = new Payment();
        payment.setAmount(orderDto.getAmount());
        payment.setCustomerId(orderDto.getCustomerId());
        payment.setTransactionId(transId);

        Boolean result = false;

        if (result) {

            orderDto.setStatus(OrderStatus.COMPLETED);

            rabbitTemplate.convertAndSend(
                    PaymentConfig.ORDER_BILLED_QUEUE,
                    orderDto);

            payment.setStatus(PaymentStatus.SUCCESSFUL);
            System.out.println("[Order Receive and Payment Successful] " + orderDto);
        }else {
            orderDto.setStatus(OrderStatus.CANCELED);
            payment.setStatus(PaymentStatus.DECLINED);
            rabbitTemplate.convertAndSend(
                    PaymentConfig.ORDER_BILLED_QUEUE,
                    orderDto);

            System.out.println("[Order Receive but Payment Declined] " + orderDto);

        }

        paymentService.savePayment(payment);
    }
}
