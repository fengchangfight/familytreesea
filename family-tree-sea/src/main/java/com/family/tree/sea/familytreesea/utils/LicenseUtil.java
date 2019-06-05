package com.family.tree.sea.familytreesea.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

@Component
public class LicenseUtil {

    private Cipher cipher=null;

    public void initCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        if(cipher==null){
            this.cipher = Cipher.getInstance("RSA");
        }
    }

    public PublicKey getPublic() throws Exception {
        System.out.println("===before read public Key...");
        InputStream is = new ClassPathResource("publicKey").getInputStream();
        byte[] keyBytes = IOUtils.toByteArray(is);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    public final String PUBLIC_SIGN = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCLzDT8VG3Fzd+nT5dgGOjlJDe/41sE+XDJJNh/WH8jVji0sP4j5Bem6M21baBF529kYOnEnW3ZwBw/HPs4bX5hiiG5deUGCk+vQIEhiUjqsIKkNzEZSh8LWq51G8cnGZ31Hprfpyi+rh6XbQAx9GB4j2sCmgavA0ipFvydveYK3QIDAQAB";

    public String decryptText(String msg, PublicKey key) throws InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        initCipher();
        this.cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.decodeBase64(msg)), "UTF-8");
    }

    private Map<String,String> parseMingwen(String mingwen){
        Map<String,String> result = new HashMap<>();

        String[] lines = mingwen.split("\n");
        for(String line: lines){
            String[] lineArr = line.split("\t");
            if(lineArr.length!=2){
                throw new RuntimeException("This license is invalid...");
            }else{
                String key = lineArr[0];
                String value = lineArr[1];
                result.put(key,value);
            }
        }
        if(!result.containsKey("max_family_count") || !result.containsKey("start_date") || !result.containsKey("end_date") || !result.containsKey("username") ){
            throw new RuntimeException("license not containing the required keys(max_family_count, start_date, end_date, username)");
        }

        return result;
    }

    public Map<String,String> parseLicenseCode(String licenseCode){
        try {
            PublicKey publicKey = this.getPublic();

            System.out.println("===Public key is:");
            System.out.println(publicKey);
            System.out.println(".....");

            byte[] baseBytes = publicKey.getEncoded();

            String base64encodedString = Base64.encodeBase64String(baseBytes);
            System.out.println("===show sign:");
            System.out.println(base64encodedString);
            if(PUBLIC_SIGN.equals(base64encodedString)){
                // legal public key, do the parse
                String mingwen =  decryptText(licenseCode, publicKey);
                return parseMingwen(mingwen);
            }else{
                throw new RuntimeException("Illegal public key...");
            }
        } catch (Exception e) {
            throw new RuntimeException("Illegal license code...");
        }
    }
}
