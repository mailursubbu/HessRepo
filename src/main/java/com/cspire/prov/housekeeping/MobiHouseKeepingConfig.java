package com.cspire.prov.housekeeping;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class MobiHouseKeepingConfig {
    @Bean
    public MobiHouseKeepingService mobiHouseKeepingSer(){
        MobiHouseKeepingService apMaxHouseKeepingSer =
                new MobiHouseKeepingService();
        return apMaxHouseKeepingSer;
    }
}
