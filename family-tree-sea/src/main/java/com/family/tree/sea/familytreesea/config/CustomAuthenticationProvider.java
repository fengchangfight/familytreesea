package com.family.tree.sea.familytreesea.config;

import com.family.tree.sea.familytreesea.entity.User;
import com.family.tree.sea.familytreesea.service.UserService;
import com.family.tree.sea.familytreesea.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    private boolean correctUserNameAndPassword(String username, String password){
        User user = getUserByPhoneOrEmail(username);

        String passwordInDB = user.getPassword();
        String encryptedPassword=null;
        try {
            encryptedPassword = SecurityUtil.generateStorngPasswordHash(password, user.getSalt());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            return false;
        }

        if(encryptedPassword.equals(passwordInDB)){
            return true;
        }else{
            return false;
        }
    }

    private User getUserByPhoneOrEmail(String entityName){
        User userByPhone = userService.findByPhone(entityName);
        User userByEmail = userService.findByEmail(entityName);
        User user = null;
        if(userByPhone!=null){
            user = userByPhone;
        }else{
            user = userByEmail;
        }
        return user;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException{
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        if(correctUserNameAndPassword(name, password)){
            User user = getUserByPhoneOrEmail(name);
            return new UsernamePasswordAuthenticationToken(user.getUsername(), password, new ArrayList<>());
        }else{
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}