package com.noob.spring.beanDefinition;

import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
@Component
@Slf4j
public class WebServiceServiceScannerConfigurer2 implements BeanFactoryAware, ImportBeanDefinitionRegistrar {

	private BeanFactory beanFactory;

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

		List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
		if (log.isDebugEnabled()) {
			packages.forEach(pkg -> log.debug("Using auto-configuration base package '{}'", pkg));
		}

		ClassPathCustomizerClientScanner scanner = new ClassPathCustomizerClientScanner(registry);
		scanner.setAnnotationClass(CustomizerClient.class); // 指定扫描class
		scanner.registerFilters();
		scanner.scan(StringUtils.toStringArray(packages));
	}

	// 在ConfigurationClassPostProcessor.postProcessBeanDefinitionRegistry执行过程中，会提前创建ImportBeanDefinitionRegistrar对象，并执行BeanFactoryAware
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;

	}
}
