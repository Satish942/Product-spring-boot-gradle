package com.example.product;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;

@Configuration
@EnableAutoConfiguration
@Component
public class ProductCommandLineRunner implements CommandLineRunner {

	@Value("${couchbase.nodes}")
    private List<String> nodes = new ArrayList<String>();

    @Value("${couchbase.bucket}")
    private String bucket;

    @Value("${couchbase.password}")
    private String password;

    private Cluster cluster() {
        return CouchbaseCluster.create(nodes);
    }

    @Bean
    public Bucket bucket() {
        return cluster().openBucket(bucket, password);
    }

	@Override
	public void run(String... arg0) throws Exception {
		// TODO Auto-generated method stub
	}
	
}


