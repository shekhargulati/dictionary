package com.shekhar.dictionary.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.shekhar.dictionary.dao.DictionaryDao;

@Configuration
@ComponentScan(basePackageClasses=DictionaryDao.class)
public class DictionaryConfig {

	
}
