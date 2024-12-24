package com.yupi.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.util.HashMap;
import java.util.Map;

public class DlxConsumer {

    private static final String EXCHANGE_NAME = "direct2_exchange";
    private static final String DEAD_EXCHANGE_NAME = "dlx_exchange";
    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel1 = connection.createChannel();
        Channel channel2 = connection.createChannel();
        channel1.exchangeDeclare(EXCHANGE_NAME, "direct");

        String queueName = "小dog的工作队列";

        Map<String, Object> args = new HashMap<String, Object>();
        args.put("x-dead-letter-exchange", DEAD_EXCHANGE_NAME);
        args.put("x-dead-letter-routing-key", "waibao");
        channel1.queueDeclare(queueName, true, false, false, args);
        channel1.queueBind(queueName, EXCHANGE_NAME, "xiaodog");

        Map<String, Object> args2 = new HashMap<String, Object>();
        args2.put("x-dead-letter-exchange", DEAD_EXCHANGE_NAME);
        args2.put("x-dead-letter-routing-key", "boss");
        String queueName2 = "小cat的工作队列";
        channel2.queueDeclare(queueName2, false, false, false, args2);
        channel2.queueBind(queueName2, EXCHANGE_NAME, "xiaocat");


        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback1 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            //拒绝消息
            channel1.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
            System.out.println(" [小dog] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            //拒绝消息
            channel1.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
            System.out.println(" [小cat] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
        channel1.basicConsume(queueName, false, deliverCallback1, consumerTag -> {
        });
        channel1.basicConsume(queueName2, false, deliverCallback2, consumerTag -> {
        });


    }
}