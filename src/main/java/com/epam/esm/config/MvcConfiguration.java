package com.epam.esm.config;

import com.epam.esm.dao.GiftDAO;
import com.epam.esm.dao.GiftTagDAO;
import com.epam.esm.dao.TagDAO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.sql.DataSource;
import java.sql.Driver;

@Configuration
@ComponentScan(basePackages="com.epam.esm")
@EnableWebMvc
public class MvcConfiguration extends WebMvcConfigurerAdapter{

    @Bean
    public ViewResolver getViewResolver(){
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
    }

    @Bean
    public TagDAO tagDao(){
        return new TagDAO(dataSource());
    }

    @Bean
    public GiftDAO giftDao(){
        return new GiftDAO(dataSource(), tagDao());
    }

    @Bean
    public GiftTagDAO giftTagDAO(){
        return new GiftTagDAO(dataSource());
    }

    @Bean
    public DataSource dataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Driver.class.getName());
        dataSource.setUrl("jdbc:postgresql://localhost:5432/gift_certificates_system");
        dataSource.setUsername("postgres");
        dataSource.setPassword("root");
        if(dataSource == null){
            throw new RuntimeException("data Source is null");
        }
        return dataSource;
    }


}
