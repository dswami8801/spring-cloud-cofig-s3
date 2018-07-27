package com.esphere.cloud.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.cloud.config.server.s3")
public class S3Config {

	private boolean enabled;

	private String bucket;

	private Boolean cacheEnabled;

	private Boolean eagerLoad;

	private String awsKey;

	private String awsSecret;

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public Boolean getCacheEnabled() {
		return cacheEnabled;
	}

	public void setCacheEnabled(Boolean cacheEnabled) {
		this.cacheEnabled = cacheEnabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getEagerLoad() {
		return eagerLoad;
	}

	public void setEagerLoad(Boolean eagerLoad) {
		this.eagerLoad = eagerLoad;
	}

	public String getAwsKey() {
		return awsKey;
	}

	public void setAwsKey(String awsKey) {
		this.awsKey = awsKey;
	}

	public String getAwsSecret() {
		return awsSecret;
	}

	public void setAwsSecret(String awsSecret) {
		this.awsSecret = awsSecret;
	}

	@Override
	public String toString() {
		return "S3Config [bucket=" + bucket + ", cacheEnabled=" + cacheEnabled + "]";
	}

}