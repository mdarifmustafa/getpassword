package com.whodesire.config;

import com.whodesire.util.PropertiesUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

@Component
@Scope("prototype")
// @PropertySource("classpath:config/boot_helper.properties")
public class ResourceHelper {

	public ResourceHelper() {
		getPasswordInserted();
		getResourceVerified();
	}

	// @Value("#{new Boolean('${PasswordInserted}')}")
	private Boolean passwordInserted;
	private Boolean resourceVerified;

	public Boolean getPasswordInserted() {
		if (passwordInserted == null) {
			PropertiesUtil utils;
			try {
				utils = new PropertiesUtil("/config/primary.properties");
				passwordInserted = new Boolean(
						utils.getProperty("passwordInserted"));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return passwordInserted;
	}

	public void setPasswordInserted(Boolean passwordInserted) {
		this.passwordInserted = passwordInserted;
	}

	public Boolean getResourceVerified() {
		if (resourceVerified == null) {
			PropertiesUtil utils;
			try {
				utils = new PropertiesUtil("/config/primary.properties");
				resourceVerified = new Boolean(utils.getProperty("resourceVerified"));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return resourceVerified;
	}

	public void setResourceVerified(Boolean resourceVerified) {

		this.resourceVerified = resourceVerified;
	}

	@Override
	public String toString() {
		return "BootHelper [passwordInserted=" + passwordInserted + ", resourceVerified=" + resourceVerified + "]";
	}

}
