package es.lavanda.tmdb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_MESSAGES = "agent-tmdb-feed-films";
    public static final String QUEUE_MESSAGES_DLQ = "agent-tmdb-feed-films-dlq";

    public static final String QUEUE_MESSAGES_SHOWS = "agent-tmdb-feed-shows";
    public static final String QUEUE_MESSAGES_SHOWS_DLQ = "agent-tmdb-feed-shows-dlq";
    
    public static final String EXCHANGE_MESSAGES = "lavandadelpatio-exchange";

    @Bean
    DirectExchange messagesExchange() {
        return new DirectExchange(EXCHANGE_MESSAGES);
    }

    @Bean
    Binding bindingMessages() {
        return BindingBuilder.bind(messagesQueue()).to(messagesExchange()).with(QUEUE_MESSAGES);
    }

    @Bean
    Queue messagesQueue() {
        return QueueBuilder.durable(QUEUE_MESSAGES).withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", QUEUE_MESSAGES_DLQ).build();
    }

    @Bean
    Queue deadLetterQueue() {
        return QueueBuilder.durable(QUEUE_MESSAGES_DLQ).build();
    }

    @Bean
    Binding bindingMessagesShows() {
        return BindingBuilder.bind(messagesQueue()).to(messagesExchange()).with(QUEUE_MESSAGES_SHOWS);
    }

    @Bean
    Queue messagesShowsQueue() {
        return QueueBuilder.durable(QUEUE_MESSAGES_SHOWS).withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", QUEUE_MESSAGES_SHOWS_DLQ).build();
    }

    @Bean
    Queue deadLetterShowsQueue() {
        return QueueBuilder.durable(QUEUE_MESSAGES_SHOWS_DLQ).build();
    }
}
