package com.esphere.cloud.config;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "spring.cloud.config.server.s3.enabled", havingValue = "true")
public class S3EnvironmentRepository implements EnvironmentRepository {

	@Autowired
	private S3Config s3Config;

	@Autowired
	private S3ResourceResolver resourceResolver;

	@Autowired
	private S3FileReader s3FileReader;

	@Override
	public Environment findOne(String application, String profile, String label) {

		Environment environment = new Environment(application, profile);
		List<S3Resource> resources = resourceResolver.listResources(application, profile, s3Config);

		resources.forEach(r -> {
			try {
				Properties properties = s3FileReader.loadProperties(r);
				if (properties.size() > 0)
					environment.add(new PropertySource(r.getFilename(), properties));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		return environment;
	}

	@PostConstruct
	public void init() {
		List<String> objects = new ArrayList<>();
		if (s3Config.getEagerLoad()) {
			objects = s3FileReader.listBucketObjects(s3Config.getBucket());
		}
		objects.forEach(o -> {
			try {
				InputStream inputStream = s3FileReader.readStream(s3Config.getBucket(), o);
				S3Resource resource = new S3Resource(inputStream, o);
				S3ConfigCache.set(o, resource);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

}
