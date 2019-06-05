package com.family.tree.sea.familytreesea.controller;

import com.family.tree.sea.familytreesea.config.PathConstants;
import com.family.tree.sea.familytreesea.entity.Role;
import com.family.tree.sea.familytreesea.entity.User;
import com.family.tree.sea.familytreesea.service.RoleService;
import com.family.tree.sea.familytreesea.service.UserService;
import com.family.tree.sea.familytreesea.utils.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.bind.annotation.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping(PathConstants.REGISTER)
public class RegisterController implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        // do whatever you need here
        try {
            this.initReg();
        } catch (InvalidKeySpecException e) {
            LOG.error("===Init reg failed...");
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            LOG.error("===Init reg failed...");
            e.printStackTrace();
        }
    }

    public void initReg() throws InvalidKeySpecException, NoSuchAlgorithmException {
        System.out.println(SecurityUtil.generateStorngPasswordHash("admin","OrZCA2rdc1j2ZoX5"));
        User user = new User();

        if(userService.findByUsername("admin@admin.com")!=null){
            // do nothing
            LOG.error("User already exists");
        }else{
            user.setSalt("OrZCA2rdc1j2ZoX5");
            user.setUsername("admin@admin.com");
            user.setPhone("18516123752");
            user.setNickname("admin");
            user.setEmail("admin@admin.com");
            user.setRegisterDate(new Date());
            user.setPassword(SecurityUtil.generateStorngPasswordHash("admin","OrZCA2rdc1j2ZoX5"));
            Set<Role> roles = new HashSet<>();
            Role role = roleService.findById(2L);
            roles.add(role);
            user.setRoles(roles);
            // default to 3
            user.setLicenseCode("ebCEW0X2LeorXLu07raP31FyF+UsopReCTZMg+JKsJWZdZ5EQ9BEGckZkvvLdy/KtEuvMJMS0+2s+q8KqKEuCHeXgPL/fZQamMzzoL1SqMkRvrETWBRttFl95Z/hIxz38V8/g5DPvT70+XVlEzzB/MKAYGLsXtT9J8WmU12WDlE=");
            userService.save(user);
        }
    }

}
