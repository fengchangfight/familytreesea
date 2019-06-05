package com.family.tree.sea.familytreesea.repository.rdbms;

import com.family.tree.sea.familytreesea.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonRepository  extends JpaRepository<Person, Long> {
    List<Person> findByFamilyTreeId(Long treeId);

    void deleteByFamilyTreeId(Long id);

    @Query(value = "select id from Person where familyTree.id=:familyTreeId")
    List<Long> findPersonIdsByFamilyTreeId(@Param("familyTreeId") Long familyTreeId);

    int countByProfileImagePath(String key);
}
