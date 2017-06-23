package com.cspire.prov.dtf.model;
//"IptvChannelPackageByCompCodeAndSrvArea"
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
//IptvFipsCodeByFipsKey
@CacheEvict(cacheNames={"FipsCodeByFipsKey"}, allEntries=true)
public class CacheEviction {
    private static final Logger log = LoggerFactory.getLogger(CacheEviction.class);
    
    public void cacheEvictAll(){ 
        log.trace("Evicting cached data");
    }
}
