package com.family.tree.sea.familytreesea.repository.rdbms;

import com.family.tree.sea.familytreesea.entity.FamilyTree;
import com.family.tree.sea.familytreesea.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface FamilyTreeRepository extends JpaRepository<FamilyTree,Long> {
    Long countByCreatedBy(User user);

    List<FamilyTree> findByCreatedBy(User user);
    List<FamilyTree> findByCreatedByAndName(User user, String name);

    List<FamilyTree> findByIdIn(Collection<Long> ids);
}
