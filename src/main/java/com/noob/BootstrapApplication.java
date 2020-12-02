package com.noob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

import com.noob.spring.beanDefinition.CustomizerClientScannerConfigurer2.BeanDefinitionRegistrarForImport;
@EnableAsync
@SpringBootApplication
//@Import(BeanDefinitionRegistrarForImport.class)
public class BootstrapApplication {
	public static void main(String[] args) {
		SpringApplication.run(BootstrapApplication.class, args);
	}
}
