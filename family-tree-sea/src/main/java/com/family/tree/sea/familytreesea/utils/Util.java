package com.family.tree.sea.familytreesea.utils;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Util {
    public static String currentUserName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            return currentUserName;
        }else{
            return null;
        }
    }

    public static String trunc(String src, int limit) {
       return src.substring( 0, Math.min(src.length(), limit));
    }


    public static boolean checkURLExist(String url){
        URL u = null;
        try {
            u = new URL( url);
            HttpURLConnection huc =  ( HttpURLConnection )  u.openConnection ();
            huc.setRequestMethod ("GET");  //OR  huc.setRequestMethod ("HEAD");
            huc.connect () ;
            int code = huc.getResponseCode() ;
            if(200==code){
                return true;
            }
            return false;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
