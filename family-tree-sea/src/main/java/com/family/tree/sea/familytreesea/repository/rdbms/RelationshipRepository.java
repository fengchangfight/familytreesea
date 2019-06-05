package com.family.tree.sea.familytreesea.repository.rdbms;

import com.family.tree.sea.familytreesea.entity.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, Long> {
    Relationship findByFromPersonIdAndToPersonId(Long fromPersonId, Long toPersonId);

    List<Relationship> findByFromPersonIdInAndToPersonIdIn(List<Long> fromPersonIds, List<Long> toPersonIds);

    List<Relationship> findByFamilyTreeId(Long familyTreeId);

    @Query(value = "select r from Relationship r where familyTree.id=:familyTreeId and fromPerson.id=:personId or toPerson.id=:personId")
    List<Relationship> findByFamilyTreeIdAndPersonId(@Param("familyTreeId") Long familyTreeId, @Param("personId") Long personId);

    void deleteByFromPersonId(Long id);

    void deleteByFromPersonIdAndToPersonId(Long fromId, Long toId);

    void deleteByToPersonId(Long id);

    void deleteByFamilyTreeId(Long id);
}
