package com.spring.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

	// Loggerlar kuruluyor
	private Logger theLogger = Logger.getLogger(getClass().getName());
	
	// Spring için controller kuruluyor
	@Pointcut("execution(* com.spring.controller.*.*(..))")
	private void loggingForController() {}
	
	@Pointcut("execution(* com.spring.dao.*.*(..))")
	private void loggingForDAO() {}
	
	@Pointcut("execution(* com.spring.service.*.*(..))")
	private void loggingForService() {}
	
	@Pointcut("loggingForController() || loggingForDAO() || loggingForService()")
	private void loggingForAppFlow() {}
	
	// add @Before advice
	@Before("loggingForAppFlow()")
	public void before(JoinPoint theJoinPoint) {
		
		// Çağırılan method görüntüleniyor
		String theMethod = theJoinPoint.getSignature().toShortString();
		theLogger.info("======>> in @Before: calling method: " + theMethod);
		
		// Args alınıyor
		Object[] args = theJoinPoint.getArgs();
		
		// döngü ve bağımsız değişkenleri görüntüleniyor
		for(Object tempArg : args) {
			theLogger.info("=====>> argument: " + tempArg);
		}
		
	}
	
	// ekleniyor @AfterReturning 
	@AfterReturning(
			pointcut="loggingForAppFlow()",
			returning="result")
	public void afterReturning(JoinPoint theJoinPoint, Object result) {
		
		// Görüntüleme yöntemi geri döndürülüyor
		String theMethod = theJoinPoint.getSignature().toShortString();
		theLogger.info("======>> in @Before: calling method: " + theMethod);
		
		// Data dödürülmesi görüntüleniyor
		theLogger.info("=====>> result: " + result);
		
	}
	
}
