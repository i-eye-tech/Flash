package com.flash.service.utilities;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class S3Helper {
    private static final Logger logger = LoggerFactory.getLogger(S3Helper.class);
    private static S3Helper instance = null;
    public static String accessKey;
    public static String secretKey;
    public static String bucketName;
    private S3Helper(){

    }

    public static S3Helper getInstance(){
        if (instance==null)
            instance=new S3Helper();
        return instance;
    }

    public String uploadFileToS3(String filePath) {
        File file = new File(filePath);
        if (!file.exists() && !file.isFile()) {
            logger.error("Invalid File {}", filePath);
            return null;
        }
        String nameOfFile = "automation-reports/"+filePath.substring(filePath.lastIndexOf("/") + 1);
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3Client s3Client = (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
        try {
            s3Client.putObject(new PutObjectRequest(bucketName, nameOfFile, file).withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (Exception e) {
            logger.error("Exception while uploading file {} to s3, nested exception is {}",nameOfFile,e.getMessage());
        }
        return s3Client.getResourceUrl(bucketName,nameOfFile);
    }

}
