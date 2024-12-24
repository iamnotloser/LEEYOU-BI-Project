package com.yupi.springbootinit.mq;

import com.rabbitmq.client.*;

public class DirectConsumer {

    private static final String EXCHANGE_NAME = "direct_exchange";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel1 = connection.createChannel();
        Channel channel2 = connection.createChannel();
        channel1.exchangeDeclare(EXCHANGE_NAME, "direct");

        String queueName = "小鱼的工作队列";
        channel1.queueDeclare(queueName, true, false, false, null);
        channel1.queueBind(queueName, EXCHANGE_NAME, "xiaoyu");


        String queueName2 = "小美的工作队列";
        channel2.queueDeclare(queueName2, false, false, false, null);
        channel2.queueBind(queueName2, EXCHANGE_NAME, "xiaomei");


        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback1 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [小鱼] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [小美] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
        channel1.basicConsume(queueName, true, deliverCallback1, consumerTag -> {
        });
        channel1.basicConsume(queueName2, true, deliverCallback2, consumerTag -> {
        });
    }
}