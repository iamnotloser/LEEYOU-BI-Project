package com.yupi.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class MultipleConsumer {

  private static final String TASK_QUEUE_NAME = "multi_queue";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    final Connection connection = factory.newConnection();
      for (int i = 0;i<2;i++) {
          final Channel channel = connection.createChannel();

          channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
          System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

          channel.basicQos(1);
          //定义了如何处理消息
          int finalI1 = i;
          DeliverCallback deliverCallback = (consumerTag, delivery) -> {
              String message = new String(delivery.getBody(), "UTF-8");

              int finalI = finalI1;
              System.out.println(" [x] Received '" +"编号"+finalI+ message + "'");
              try {
                  //处理工作
                  Thread.sleep(20000);
                  channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
              } catch (InterruptedException e) {
                  channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
                  throw new RuntimeException(e);
              } finally {
                  System.out.println(" [x] Done");
                  channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
              }
          };
          channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> { });
      }

  }


}