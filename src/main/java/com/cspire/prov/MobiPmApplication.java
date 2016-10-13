package com.cspire.prov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication(scanBasePackages={"com.cspire.prov","com.cspire.prov.framework"})
@EnableCaching
@EnableScheduling
public class MobiPmApplication{
    private static final Logger log = LoggerFactory.getLogger(MobiPmApplication.class);

    public static void main(String[] args) {
        try{
            SpringApplication.run(MobiPmApplication.class, args);
        }catch(Exception e){
            log.error("Failed to start MobiTv Provisioning Manager");
            System.out.println("Failed to start MobiTv Provisioning Manager");
            System.exit(1);
        }
        
        System.out.println("MobiTv Provisioning Manager Succsessfuly started");
        log.info("MobiTv Provisioning Manager Successfully started");
    }
}
