package madking.serverless;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import madking.serverless.controller.PingController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@SpringBootApplication
// We use direct @Import instead of @ComponentScan to speed up cold starts
// @ComponentScan(basePackages = "rms.serverless.controller")
@Import({PingController.class})
@EnableWebMvc
public class Application {

    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        return AmazonDynamoDBClientBuilder.standard().build();
    }

    @Bean
    public DynamoDBMapper dynamoDBMapper(AmazonDynamoDB amazonDynamoDB) {
        return new DynamoDBMapper(amazonDynamoDB);
    }

    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.
                standard()
                .build();
    }

    @Bean
    public AWSSecurityTokenService awsSecurityTokenService() {
        return AWSSecurityTokenServiceClientBuilder
                .standard()
                .build();
    }


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}