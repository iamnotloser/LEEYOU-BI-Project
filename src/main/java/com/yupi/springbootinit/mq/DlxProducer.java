package com.yupi.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DlxProducer {
    private static final String EXCHANGE_NAME = "direct2_exchange";
    private static final String DEAD_EXCHANGE_NAME = "dlx_exchange";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            //声明死信交换机
            channel.exchangeDeclare(DEAD_EXCHANGE_NAME, "direct");
            Map<String, Object> args = new HashMap<String, Object>();
            args.put("x-dead-letter-exchange", "dlx_exchange");
            //声明死信队列
            String queueName = "老板的工作队列";
            channel.queueDeclare(queueName, false, false, false, args);
            channel.queueBind(queueName, DEAD_EXCHANGE_NAME, "boss");
            String queueName2 = "外包的工作队列";
            channel.queueDeclare(queueName2, false, false, false, args);
            channel.queueBind(queueName2, DEAD_EXCHANGE_NAME, "waibao");
            DeliverCallback laobandeliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
//                //拒绝消息
//                channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
                System.out.println(" [laoban] Received '" +
                        delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
            };
            DeliverCallback waibaodeliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
//                //拒绝消息
//                channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
                System.out.println(" [waibao] Received '" +
                        delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
            };
            channel.basicConsume(queueName, false, laobandeliverCallback, consumerTag -> {
            });
            channel.basicConsume(queueName2, false, waibaodeliverCallback, consumerTag -> {
            });
            Scanner sc = new Scanner(System.in);
            while (sc.hasNext()) {
                String userInput = sc.nextLine();
                String[] strings = userInput.split(" ");
                if (strings.length < 1) {
                    continue;
                }
                String message = strings[0];
                String routingKey = strings[1];
                channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
                System.out.println(" [x] Sent '" + message + " with routing " + routingKey + "'");
            }


        }
    }

}