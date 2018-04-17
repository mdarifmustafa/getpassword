package com.whodesire.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"com.whodesire.first", "com.whodesire.config", "com.whodesire.util"})
public class SpringAppConfig {

}
