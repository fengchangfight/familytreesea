package com.family.tree.sea.familytreesea.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;
@Configuration
public class AppConfig {
    @Bean
    public ImmutableMap<String, String> relationMap(){
        ImmutableMap<String, String> experienceLevelMap = null;
        EncodedResource encodedResource = new EncodedResource(new ClassPathResource("relation_type.properties"), "UTF-8");
        try {
            Properties prop = PropertiesLoaderUtils.loadProperties(encodedResource);
            experienceLevelMap = Maps.fromProperties(prop);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return experienceLevelMap;
    }
}