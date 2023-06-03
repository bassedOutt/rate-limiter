package com.example.ratelimiter.scanner;

import com.example.ratelimiter.limit.RateLimited;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Configuration
public class AnnotationScanner {

    @Bean("methodRateLimits")
    public Map<String, RateLimited> scan() {
        Map<String, RateLimited> methodAnnotationMap = new HashMap<>();

        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(RateLimited.class));

        Set<BeanDefinition> candidates = scanner.findCandidateComponents("");
        for (BeanDefinition beanDefinition : candidates) {
            try {
                Class<?> beanClass = Class.forName(beanDefinition.getBeanClassName());
                Annotation[] classAnnotations = beanClass.getDeclaredAnnotations();
                for (Method method : beanClass.getDeclaredMethods()) {
                    Annotation[] methodAnnotations = method.getDeclaredAnnotations();
                    for (Annotation methodAnnotation : methodAnnotations) {
                        if (methodAnnotation.annotationType().equals(RateLimited.class)) {
                            // Retrieve the value from the annotation and map it to the method name
                            methodAnnotationMap.put(method.getName(), (RateLimited) methodAnnotation);
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return methodAnnotationMap;
    }
}
