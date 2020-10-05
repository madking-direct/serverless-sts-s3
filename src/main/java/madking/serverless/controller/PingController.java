package madking.serverless.controller;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
import com.amazonaws.services.securitytoken.model.Credentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class PingController {
    private static final Logger logger = LoggerFactory.getLogger(PingController.class);

    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private AWSSecurityTokenService awsSecurityTokenService;

    private String stsAssumeRoleArn = "arn:aws:iam::111111111111:role/AmazonS3STSFullAccess";
    private String bucket = "exportdata";

    @RequestMapping(path = "/path/to/resource", method = RequestMethod.POST)
    public Map<String, String> ping() throws IOException {
        Map<String, String> pong = new HashMap<>();
        pong.put("pong", "Hello, World!");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String key = simpleDateFormat.format(new Date()) + "/data.csv";

        AmazonS3 s3FromAssumeRole = amazonS3FromAssumeRole(awsSecurityTokenService);
        S3Object s3Object = s3FromAssumeRole.getObject(bucket, key);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(s3Object.getObjectContent()));
        String buffer = "";
        while (buffer != null) {
            buffer = bufferedReader.readLine();
            logger.info(buffer);
        }

        return pong;
    }


    private AmazonS3 amazonS3FromAssumeRole(AWSSecurityTokenService awsSecurityTokenService) {

        AssumeRoleResult roleResponse =
                awsSecurityTokenService.assumeRole(new AssumeRoleRequest()
                        .withRoleArn(stsAssumeRoleArn)
                        .withRoleSessionName("cross_acct_lambda"));

        Credentials credentials = roleResponse.getCredentials();

        BasicSessionCredentials basicSessionCredentials =
                new BasicSessionCredentials(
                        credentials.getAccessKeyId(),
                        credentials.getSecretAccessKey(),
                        credentials.getSessionToken());

        AWSStaticCredentialsProvider awsStaticCredentialsProvider =
                new AWSStaticCredentialsProvider(basicSessionCredentials);

        return AmazonS3ClientBuilder
                .standard()
                .withRegion("eu-west-2")
                .withCredentials(awsStaticCredentialsProvider)
                .build();
    }

}
