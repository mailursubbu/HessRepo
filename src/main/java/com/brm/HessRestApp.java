package com.brm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication(scanBasePackages={"com.brm"})
@EnableCaching
@EnableScheduling
public class HessRestApp{
    private static final Logger log = LoggerFactory.getLogger(HessRestApp.class);

    public static void main(String[] args) {
        try{
            SpringApplication.run(HessRestApp.class, args);
        }catch(Exception e){
            log.error("Failed to start HessRestApp",e);
            System.out.println("Failed to start HessRestApp");
            System.exit(1);
        }
        
        System.out.println("HessRestApp Succsessfuly started");
        log.info("HessRestApp Successfully started");
    }
}
