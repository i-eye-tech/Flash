
package com.flash.service.utilities;

import com.flash.service.BaseApiManager;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;


public class KafkaHelper {
    private static final Logger logger = LoggerFactory.getLogger(BaseApiManager.class);
    public static String bootstrapServers;
    ReportHelper reportHelper;
    String groupId;
    final Consumer<Long, String> kafkaConsumer;

    private static final Map<String, KafkaHelper> kafkaInstanceMap = new HashMap<>();

    private KafkaHelper(String groupId, ReportHelper reportHelper) {
        this.reportHelper = reportHelper;
        this.groupId = groupId;
        this.kafkaConsumer = createConsumer(groupId);
    }

    public static KafkaHelper getInstance(String groupId, ReportHelper reportHelper) // considering mongo server at all time should be same
    {
        if (kafkaInstanceMap.containsKey(groupId))
            return kafkaInstanceMap.get(groupId);
        kafkaInstanceMap.put(groupId, new KafkaHelper(groupId, reportHelper));
        return kafkaInstanceMap.get(groupId);
    }

    public Consumer<Long, String> createConsumer(String grp_id) {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, grp_id);
//        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new KafkaConsumer<>(properties);
    }


    public String pollData(String topic) {
        kafkaConsumer.subscribe(Collections.singletonList(topic));
        ConsumerRecords<Long, String> records = kafkaConsumer.poll(Duration.ofMillis(1000));
        kafkaConsumer.commitSync();

        if (records.isEmpty()) {
            logger.error("Null Kafka Value");
            return null;
        }
        String value = records.iterator().next().value();
        System.out.println("Kafka Value " + value);
        return value;
    }

    public List<String> poll(String topic) {
        List<String> records = new ArrayList<>();
        final int giveUp = 3;
        int noRecordsCount = 0;
        kafkaConsumer.subscribe(Collections.singletonList(topic));
        while (true) {
            final ConsumerRecords<Long, String> consumerRecords =
                    kafkaConsumer.poll(1000);
            if (consumerRecords.count() == 0) {
                noRecordsCount++;
                if (noRecordsCount > giveUp) break;
                else continue;
            }
            consumerRecords.forEach(record -> {
                System.out.printf("Consumer Record:(%d, %s, %d, %d)\n",
                        record.key(), record.value(),
                        record.partition(), record.offset());
                records.add(record.value());
            });
            kafkaConsumer.commitAsync();
        }
        return records;
    }

    public static void main(String[] args) {
        KafkaHelper.bootstrapServers = "127.0.0.1:9092";
        KafkaHelper kafkaHelper = new KafkaHelper("new_group", ReportHelper.getInstance("tdrfyu"));
//        while(true)
//        System.out.println("KafkaValue "+kafkaHelper.pollData( "subscriptions-topic"));
        System.out.println(kafkaHelper.poll("subscriptions-topic"));
    }
}