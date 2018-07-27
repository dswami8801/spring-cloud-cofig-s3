package com.esphere.cloud.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Properties;

import org.springframework.core.io.Resource;

public class S3Resource implements Resource {

	private InputStream inputStream;

	private String filename;

	private Properties properties;

	public S3Resource(InputStream inputStream, String filename) {
		super();
		this.inputStream = inputStream;
		this.filename = filename;
	}

	public S3Resource(String filename) {
		super();
		this.filename = filename;
	}

	public S3Resource(InputStream inputStream) {
		super();
		this.inputStream = inputStream;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return inputStream;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public boolean isReadable() {
		return true;
	}

	@Override
	public boolean isOpen() {
		return true;
	}

	@Override
	public URL getURL() throws IOException {
		return null;
	}

	@Override
	public URI getURI() throws IOException {
		return null;
	}

	@Override
	public File getFile() throws IOException {
		return null;
	}

	@Override
	public long contentLength() throws IOException {
		return 0;
	}

	@Override
	public long lastModified() throws IOException {
		return 0;
	}

	@Override
	public Resource createRelative(String relativePath) throws IOException {
		return null;
	}

	@Override
	public String getFilename() {
		return filename;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	@Override
	public String getDescription() {
		return null;
	}
}