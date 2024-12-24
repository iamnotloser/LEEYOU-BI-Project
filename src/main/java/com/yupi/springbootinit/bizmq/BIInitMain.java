package com.yupi.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 用于创建 程序需要的交换机和MQ 队列，只用执行一次
 */
public class BIInitMain {
    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {

            Connection connection = factory.newConnection();
            Channel channel= connection.createChannel();
            String EXCHANGE_NAME = BIMqConstant.BI_EXCHANGE;
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            String queueName = BIMqConstant.BI_QUEUE;
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, EXCHANGE_NAME, BIMqConstant.BI_ROUTING_KEY);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}