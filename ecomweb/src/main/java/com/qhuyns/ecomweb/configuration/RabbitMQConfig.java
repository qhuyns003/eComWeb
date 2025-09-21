package com.qhuyns.ecomweb.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

// 1 queue nhieu message -> bind 1 queue voi nhieu cap exchange + key
// 1 message nhieu queue nghe -> bind exchange + key do voi nhieu queue
// chi nen dung event driven khi muon phat broadcast den nhieu sẻrvice, con neu 1 service thi command(rest) cho nhanh
// ban chat event driven de xay dung loose coupling cho web site, kp sua lai code cu khi co service moi
// ngoai ra con giup giam latency khi k can cho response
// giup sap he thong thi message trong queue van con, chay lai dc
// scale tu nhien bang cach chia nho nghiep vu cho cac service
@Configuration
public class RabbitMQConfig {
    // exchange va key dat theo publisher
    // queue dat theo consummer

    public static final String SHOP_EXCHANGE = "shop.exchange";
    public static final String DLQ_EXCHANGE = "dlq.exchange";
    public static final String USER_EXCHANGE = "user.exchange";

    // === Queues === producer k quan tam queue
    public static final String USER_SERVICE_SHOP_CREATION_FAILED_QUEUE = "user-service.shop-creation-failed.queue";
    public static final String DLQ_QUEUE = "dlq.queue";
    public static final String USER_SERVICE_SHOP_CREATED_QUEUE = "user-service.shop-created.queue";
    public static final String MAIL_SERVICE_USER_CREATED_QUEUE = "mail-service.user-created.queue";
    // === Routing Keys ===
    public static final String SHOP_CREATION_FAILED = "shop.creation.failed";
    public static final String SHOP_CREATED = "shop.created";
    public static final String DLQ = "dlq";
    public static final String USER_CREATED = "user.created";



    // danh cho listener
    @Bean
    DirectExchange exchange() {
        return new DirectExchange(DLQ_EXCHANGE);
    }
    @Bean
    Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ_QUEUE).build();
    }
    @Bean
    Binding binding(Queue deadLetterQueue, DirectExchange exchange) {
        return BindingBuilder.bind(deadLetterQueue).to(exchange).with(DLQ);
    }


    @Bean
    public DirectExchange shopExchange() {
        return new DirectExchange(SHOP_EXCHANGE);
    }
    @Bean
    public Queue userServiceShopCreationFailedQueue() {

        return QueueBuilder.durable(USER_SERVICE_SHOP_CREATION_FAILED_QUEUE)
                .withArgument("x-dead-letter-exchange", DLQ_EXCHANGE ) // exchange mặc định
                .withArgument("x-dead-letter-routing-key", DLQ) // đẩy sang DLQ khi fail
                .build();
    }
    @Bean
    public Binding bindingShopCreationFailed(Queue userServiceShopCreationFailedQueue, DirectExchange shopExchange) {
        return BindingBuilder.bind(userServiceShopCreationFailedQueue)
                .to(shopExchange)
                .with(SHOP_CREATION_FAILED);
    }


    @Bean
    public Queue userServiceShopCreatedQueue() {

        return QueueBuilder.durable(USER_SERVICE_SHOP_CREATED_QUEUE)
                .build();
    }
    @Bean
    public Binding bindingShopCreated(Queue userServiceShopCreatedQueue, DirectExchange shopExchange) {
        return BindingBuilder.bind(userServiceShopCreatedQueue)
                .to(shopExchange)
                .with(SHOP_CREATED);
    }


    @Bean
    public DirectExchange userExchange() {
        return new DirectExchange(USER_EXCHANGE);
    }
    @Bean
    public Queue mailServiceUserCreatedQueue() {

        return QueueBuilder.durable(MAIL_SERVICE_USER_CREATED_QUEUE)
                .withArgument("x-dead-letter-exchange", DLQ_EXCHANGE ) // exchange mặc định
                .withArgument("x-dead-letter-routing-key", DLQ) // đẩy sang DLQ khi fail
                .build();
    }
    @Bean
    public Binding bindingUserCreated(Queue mailServiceUserCreatedQueue, DirectExchange userExchange) {
        return BindingBuilder.bind(mailServiceUserCreatedQueue)
                .to(userExchange)
                .with(USER_CREATED);
    }











    // cau hinh giup serilizable vaf deseriliazlbe object thanh json, giong o tang controller thi dc impl san lop serializebale
    // Converter: chỉ cần Jackson, không quan tâm __TypeId__
    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate để publish
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    // ContainerFactory cho listener
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);

        // tránh loop nếu consumer throw exception
        // neu queue config dlq thi se gui vao dlq
        // con k se drop luon message va k requeue
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

}

