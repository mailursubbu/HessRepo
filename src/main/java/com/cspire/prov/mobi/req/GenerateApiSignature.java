package com.cspire.prov.mobi.req;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class GenerateApiSignature {

    /*final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {

        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String generateSig(String mobiEndpoint,String PartnerId,long ts) {
        try {
            String secret = "secret_key";
            String message = mobiEndpoint+":"+PartnerId+":"+ts;
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            String hash = bytesToHex(sha256_HMAC.doFinal(message.getBytes()));
            System.out.println(hash);
            return hash;
        } catch (Exception e) {
            System.out.println("Error");
        }
        return null;
    }
    
    public static void main(String[] args){
        GenerateApiSignature.generateSig("http://localhost:9010","123",766789);
    }*/
}
