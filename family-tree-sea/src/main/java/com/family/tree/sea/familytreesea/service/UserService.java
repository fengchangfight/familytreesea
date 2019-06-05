package com.family.tree.sea.familytreesea.service;


import com.family.tree.sea.familytreesea.entity.User;

public interface UserService {

    User findByUsername(String username);

    User findByEmail(String email);

    User findByPhone(String phone);

    User findUserByPhoneExcludeMe(String phone, String me);
    User findUserByEmailExcludeMe(String phone, String me);

    String generateUserProfileUrlByUsername(String username, String style, String defaultImg);
    Long save(User user);

    Long count();

}
