package com.family.tree.sea.familytreesea.repository.rdbms;

import com.family.tree.sea.familytreesea.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
