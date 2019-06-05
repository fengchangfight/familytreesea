package com.family.tree.sea.familytreesea.repository.rdbms;

import com.family.tree.sea.familytreesea.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findById(Long id);

    User findByUsername(String userName);
    User findByEmail(String email);
    User findByPhone(String phone);

    User findByUsernameAndPassword(String username, String password);

    List<User> findFirst10ByPhoneLikeAndUsernameNot(String phone, String username);
    User findByPhoneAndUsernameNot(String phone, String username);
    User findByEmailAndUsernameNot(String email, String username);
    List<User> findFirst10ByEmailLikeAndUsernameNot(String email, String username);
    List<User> findFirst10ByNicknameLikeAndUsernameNot(String nickname, String username);

}
