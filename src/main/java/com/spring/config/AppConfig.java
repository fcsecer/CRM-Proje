package com.spring.config;

import java.beans.PropertyVetoException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Configuration
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan("com.spring")
@PropertySource({ "classpath:persistence-mysql.properties", "classpath:security-persistence-mysql.properties" })
public class AppConfig implements WebMvcConfigurer {

	@Autowired
	private Environment env;
	
	private Logger logger = Logger.getLogger(getClass().getName());
	
	// ViewResolver için ekleme yapýlýyor

	@Bean
	public ViewResolver viewResolver() {
		
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		
		viewResolver.setPrefix("/WEB-INF/view/");
		viewResolver.setSuffix(".jsp");
		
		return viewResolver;
	}
	
	@Bean
	public DataSource myDataSource() {
		
		// Havuz baðlantýsý oluþturuluyor
		ComboPooledDataSource myDataSource = new ComboPooledDataSource();

		// jdbc driver set ediliyor
		try {
			myDataSource.setDriverClass("com.mysql.jdbc.Driver");		
		}
		catch (PropertyVetoException exc) {
			throw new RuntimeException(exc);
		}
		
		// url ve kullanýcý günlük ekleniyor.(Verilerinokunduðundan emin olmak için)
		logger.info("jdbc.url=" + env.getProperty("jdbc.url"));
		logger.info("jdbc.user=" + env.getProperty("jdbc.user"));
		
		// veritabaný baðlantý özelliklerini ayarlanýyor
		myDataSource.setJdbcUrl(env.getProperty("jdbc.url"));
		myDataSource.setUser(env.getProperty("jdbc.user"));
		myDataSource.setPassword(env.getProperty("jdbc.password"));
		
		// Baðlantý havuzu ayarlanýyor
		myDataSource.setInitialPoolSize(getIntProperty("connection.pool.initialPoolSize"));
		myDataSource.setMinPoolSize(getIntProperty("connection.pool.minPoolSize"));
		myDataSource.setMaxPoolSize(getIntProperty("connection.pool.maxPoolSize"));		
		myDataSource.setMaxIdleTime(getIntProperty("connection.pool.maxIdleTime"));

		return myDataSource;
	}
	
	private Properties getHibernateProperties() {

		// Hazýrda bekletme özellikleri ayarlanýyor
		Properties props = new Properties();

		props.setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
		props.setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
		
		return props;				
	}

	// Güvenlik veri kaynaðý tanýmlanýyor
	
	@Bean
	public DataSource securityDataSource() {
		
		// Havuz baðlantýsý oluþturuluyor
		ComboPooledDataSource securityDataSource
									= new ComboPooledDataSource();
				
		// jdbc classý set ediliyor
		
		try {
			securityDataSource.setDriverClass(env.getProperty("security.jdbc.driver"));
		} catch (PropertyVetoException exc) {
			throw new RuntimeException(exc);
		}
		
		/* 
		 Veri okuduðumuzdan emin olmak için baðlantý parçacýklarý günlük kaydedilir
		  */
		
		logger.info(">>> security.jdbc.url=" + env.getProperty("security.jdbc.url"));
		logger.info(">>> security.jdbc.user=" + env.getProperty("security.jdbc.user"));
		
		
		// veritabaný baðlantý özelliklerini ayarlanýyor
		
		securityDataSource.setJdbcUrl(env.getProperty("security.jdbc.url"));
		securityDataSource.setUser(env.getProperty("security.jdbc.user"));
		securityDataSource.setPassword(env.getProperty("security.jdbc.password"));
		
		// Baðlantý havuzu ayarlanýyor
		
		securityDataSource.setInitialPoolSize(
				getIntProperty("security.connection.pool.initialPoolSize"));

		securityDataSource.setMinPoolSize(
				getIntProperty("security.connection.pool.minPoolSize"));

		securityDataSource.setMaxPoolSize(
				getIntProperty("security.connection.pool.maxPoolSize"));

		securityDataSource.setMaxIdleTime(
				getIntProperty("security.connection.pool.maxIdleTime"));
		
		return securityDataSource;
	}
	
	/* 
	 Yardýmcý meyhoda ihtiyaç var
	 Çevre etkenler int e çevriliyor
	 
	  */
	
	private int getIntProperty(String propName) {
		
		String propVal = env.getProperty(propName);
		
		// Ýnt e çevriliyor
		int intPropVal = Integer.parseInt(propVal);
		
		return intPropVal;
	}	
	
	@Bean
	public LocalSessionFactoryBean sessionFactory(){
		
		// Oturum faktörü oluþturuluyor
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		
		//Özellikler ekleniyor
		sessionFactory.setDataSource(myDataSource());
		sessionFactory.setPackagesToScan(env.getProperty("hibernate.packagesToScan"));
		sessionFactory.setHibernateProperties(getHibernateProperties());
		
		return sessionFactory;
	}
	
	@Bean
	@Autowired
	public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
		
		// Oturuma dayalý iþlem yöneticisi ayarlanýyor
		HibernateTransactionManager txManager = new HibernateTransactionManager();
		txManager.setSessionFactory(sessionFactory);

		return txManager;
	}	
	
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
          .addResourceHandler("/resources/**")
          .addResourceLocations("/resources/"); 
    }	
}