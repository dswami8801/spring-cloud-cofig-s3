package com.esphere.cloud.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

@Component
@ConditionalOnBean(S3EnvironmentRepository.class)
public class S3FileReader {

	@Autowired
	private S3Config s3Config;

	BasicAWSCredentials awsCreds = null;

	private static final Logger LOGGER = LoggerFactory.getLogger(S3FileReader.class);

	public Properties loadProperties(S3Resource s3Resource) throws IOException {
		String filename = s3Resource.getFilename();
		Properties properties = null;
		if ((properties = s3Resource.getProperties()) != null) {
			return properties;
		}
		if (s3Resource.getInputStream() == null) {
			properties = new Properties();
			s3Resource.setProperties(properties);
			S3ConfigCache.set(filename, s3Resource);
			return properties;
		}
		if (filename.endsWith(".yml")) {
			properties = readYaml(s3Resource);
		} else {
			properties = readProperties(s3Resource);
		}
		s3Resource.setProperties(properties);
		if (s3Config.getCacheEnabled())
			S3ConfigCache.set(filename, s3Resource);
		return properties;

	}

	private Properties readProperties(S3Resource inputStreamResource) {
		Properties properties = new Properties();
		try {
			String line = "";
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStreamResource.getInputStream()));
			while ((line = br.readLine()) != null) {
				if (StringUtils.hasText(line) && !line.startsWith("#"))
					properties.put(line.split("=")[0], line.split("=")[1]);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;

	}

	@SuppressWarnings("rawtypes")
	private Properties readYaml(S3Resource s3ObjectInputStream) {
		Properties properties = new Properties();

		YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
		try {
			org.springframework.core.env.PropertySource applicationYamlPropertySource = loader.load("properties",
					s3ObjectInputStream, null);
			Map<String, Object> source = ((MapPropertySource) applicationYamlPropertySource).getSource();
			properties.putAll(source);
			return properties;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return properties;

	}

	public InputStream readStream(String bucket, String filename) {
		LOGGER.debug("Reading s3 stream for :{}", filename);
		S3ObjectInputStream inputStream = null;
		try {
			AmazonS3 s3Client = new AmazonS3Client(awsCreds);

			if (!s3Client.doesObjectExist(bucket, filename)) {
				return null;
			}
			S3Object s3object = s3Client.getObject(bucket, filename);
			inputStream = s3object.getObjectContent();
		} catch (AmazonServiceException e) {
			e.printStackTrace();
		} catch (AmazonClientException e) {
			e.printStackTrace();
		}

		return inputStream;

	}

	public List<String> listBucketObjects(String bucket) {
		LOGGER.debug("Listing s3 objects for bucket :{}", bucket);
		try {
			AmazonS3 s3Client = new AmazonS3Client(awsCreds);
			ObjectListing objectListing = s3Client.listObjects(bucket);
			return objectListing.getObjectSummaries().stream().map(os -> os.getKey()).collect(Collectors.toList());
		} catch (AmazonServiceException e) {
			e.printStackTrace();
		} catch (AmazonClientException e) {
			e.printStackTrace();
		}
		return Collections.EMPTY_LIST;

	}

	@PostConstruct
	private void init() {
		awsCreds = new BasicAWSCredentials(s3Config.getAwsKey(), s3Config.getAwsSecret());
	}

}
