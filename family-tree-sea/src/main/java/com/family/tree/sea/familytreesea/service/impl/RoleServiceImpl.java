package com.family.tree.sea.familytreesea.service.impl;

import com.family.tree.sea.familytreesea.entity.Role;
import com.family.tree.sea.familytreesea.repository.rdbms.RoleRepository;
import com.family.tree.sea.familytreesea.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Role findById(Long id){
        return roleRepository.findOne(id);
    }
}
