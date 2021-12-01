package com.flash.config;

import com.flash.service.utilities.KafkaHelper;
import com.flash.service.utilities.S3Helper;
import com.flash.service.utilities.database.MongoHelper;
import com.flash.service.utilities.database.MysqlHelper;
import com.flash.service.utilities.database.PostgresqlHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Component
public class Configuration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;
    @Value("${spring.datasource.url}")
    private String postgresUri;
    @Value("${spring.datasource.username}")
    private String postgresUserName;
    @Value("${spring.datasource.password}")
    private String postgresPassword;
    @Value("${s3.accesskey}")
    private String s3AccessKey;
    @Value("${s3.secretKey}")
    private String s3SecretKey;
    @Value("${s3.bucketName}")
    private String s3BucketName;
    @Value("${kafka.bootstrapServers}")
    private String kafkaBootstrapServers;
    @Value("${spring.datasource.mysql.url}")
    private String mysqlUri;
    @Value("${spring.datasource.mysql.username}")
    private String mysqlUserName;
    @Value("${spring.datasource.mysql.password}")
    private String mysqlPassword;


    @PostConstruct
    public boolean setConfigParameters(){
        MongoHelper.mongoUri=mongoUri;
        PostgresqlHelper.postgresUri=postgresUri;
        PostgresqlHelper.username=postgresUserName;
        PostgresqlHelper.password=postgresPassword;
        S3Helper.accessKey=s3AccessKey;
        S3Helper.secretKey=s3SecretKey;
        S3Helper.bucketName=s3BucketName;
        KafkaHelper.bootstrapServers=kafkaBootstrapServers;
        MysqlHelper.mysqlUri=mysqlUri;
        MysqlHelper.username=mysqlUserName;
        MysqlHelper.password=mysqlPassword;
        return true;
    }

}
