package com.example.product;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;

@Configuration
@EnableAutoConfiguration
public class ApplicationConfig  extends AbstractCouchbaseConfiguration {

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
	protected List<String> bootstrapHosts() {
		return nodes;
	}

	@Override
	protected String getBucketName() {
		return bucket;
	}
	
	@Override
	protected String getBucketPassword() {
		return password;
	}
	
}


