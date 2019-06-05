package com.family.tree.sea.familytreesea.service.impl;


import com.family.tree.sea.familytreesea.entity.User;
import com.family.tree.sea.familytreesea.repository.rdbms.UserRepository;
import com.family.tree.sea.familytreesea.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Environment env;

    @Override
    public User findByUsername(String username){
        return userRepository.findByUsername(username);
    }


    @Override
    public User findByPhone(String phone){
        return userRepository.findByPhone(phone);
    }

    @Override
    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    @Override
    public User findUserByPhoneExcludeMe(String phone, String me){
        return userRepository.findByPhoneAndUsernameNot(phone, me);
    }

    @Override
    public User findUserByEmailExcludeMe(String email, String me){
        return userRepository.findByEmailAndUsernameNot(email, me);
    }

    @Override
    public String generateUserProfileUrlByUsername(String username, String style, String defaultImg){
        String result = "";
        return result;
    }


    @Override
    public Long save(User user){
        User u = userRepository.save(user);
        return u.getId();
    }

    @Override
    public Long count(){
        return userRepository.count();
    }

}
