package com.kumuluz.ee.samples.jms;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Dejan Ognjenović
 * @since 2.4.0
 */
public class QueueHandler {

    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;

    private static String queueName = "KUMULUZ_QUEUE";

    public static void addToQueue(Customer customer) {

        // Create connection factory and allow all packages for test purpose
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        connectionFactory.setTrustAllPackages(true);
        Connection connection;

        try {
            // Create connection
            connection = connectionFactory.createConnection();
            connection.start();

            // create session and producer
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(queueName);
            MessageProducer producer = session.createProducer(destination);

            // Create an serializable object to send to queue
            ObjectMessage msg = session.createObjectMessage();
            msg.setObject(customer);
            msg.setJMSType(Customer.class.getName());

            // Sending to queue
            producer.send(msg);

            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    public static Customer readFromQueue() {

        // Create connection factory and allow all packages for test purpose
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        connectionFactory.setTrustAllPackages(true);
        Connection connection;

        Customer customer = null;

        try {
            // Create connection
            connection = connectionFactory.createConnection();
            connection.start();

            // create session and consumer
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(queueName);
            MessageConsumer consumer = session.createConsumer(destination);

            // retrieve message
            Message message = consumer.receive();

            // check if correct type and cast message to Customer
            if (message instanceof ObjectMessage && Customer.class.getName().equals(message.getJMSType())) {
                ObjectMessage msg = (ObjectMessage) consumer.receive();
                customer = (Customer) msg.getObject();
            } else {

            }

            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }

        return customer;
    }
}
