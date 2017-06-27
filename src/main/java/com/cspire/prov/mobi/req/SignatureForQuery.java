package com.cspire.prov.mobi.req;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cspire.prov.framework.exceptions.InvalidConfig;

@Component
public class SignatureForQuery {

    private static final Logger log = LoggerFactory.getLogger(SignatureForQuery.class);
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    @Value("${mobi.query.path}")
    String mobiProvPath;
    
    @Value("${mobi.partnerId}")
    String mobiPartnerId;
    
    @Value("${mobi.secretKey}")
    String mobiSecretKey;
    
    /*
    mobi.partner=cspire
    		mobi.operator=cspire
    		mobi.secretKey="0f0d4f0e3d501af466f1d59831a7bbc440292c6d"
    		mobi.billingSystem=Omnia
    		mobi.partnerId=cspire
    	*/	
    private String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private String generateSig(String mobiEndpoint, long ts) {
        try {
            String message = mobiEndpoint + ":" + mobiPartnerId + ":" + ts;
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(mobiSecretKey.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            String hash = bytesToHex(sha256_HMAC.doFinal(message.getBytes()));
            log.trace("Signature Generated:{}",hash);
            return hash;
        } catch (Exception e) {
            log.error("Signature generation failed",e);
            throw new InvalidConfig("Signature generation failed",e);
        }
    }
    
    public String generateSigWithTs(String exterrnalId,long ts) {
        /*
         * URI : /partner/v1/notification/purchase/{operator}/{billing_system}/exterrnal_id}/purchases 
         * {operator} = cspire 
         * {billing_system} = omnia
         * {external_id} = 646259-86826 -- external_id would change based on the
         * account being operated on.
         * 
         */
        String mobiEndpoint = mobiProvPath.replace("{external_id}", exterrnalId);
        return generateSig(mobiEndpoint, ts);        
    }
}