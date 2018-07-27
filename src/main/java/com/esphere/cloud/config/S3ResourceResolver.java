package com.esphere.cloud.config;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(S3EnvironmentRepository.class)
public class S3ResourceResolver {

	private static final Logger LOGGER = LoggerFactory.getLogger(S3ResourceResolver.class);

	@Autowired
	private S3FileReader s3FileReader;

	public List<S3Resource> listResources(String name, String profile, S3Config s3Config) {

		String fileName = name + "-" + profile;
		List<S3Resource> resources = new ArrayList<>();
		List<String> s3Objects = s3FileReader.listBucketObjects(s3Config.getBucket());

		List<String> serviceResources = Arrays.asList(fileName + ".properties", fileName + ".yml", name + ".properties",
				name + ".yml", "application-" + profile + ".properties", "application-" + profile + ".yml");

		serviceResources.forEach(sr -> {
			if (!s3Objects.contains(sr)) {
				return;
			}
			S3Resource cached = S3ConfigCache.get(sr);
			if (cached == null) {
				InputStream inputStream = s3FileReader.readStream(s3Config.getBucket(), sr);
				S3Resource resource = new S3Resource(inputStream, sr);
				resources.add(resource);

			} else {
				LOGGER.debug("Returning form cache: {}", sr);
				resources.add(cached);
			}

		});
		LOGGER.debug("Following resources are resolved : {}", serviceResources);
		return resources;

	}

}
