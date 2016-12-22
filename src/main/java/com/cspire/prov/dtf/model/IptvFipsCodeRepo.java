package com.cspire.prov.dtf.model;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IptvFipsCodeRepo extends JpaRepository<IptvFipsCode, String> {
    @Cacheable("IptvFipsCodeByFipsKey")
    List<IptvFipsCode> findByFipsKey(String fipsKey); 
}
