package com.family.tree.sea.familytreesea.repository.rdbms;

import com.family.tree.sea.familytreesea.entity.ImageUploadHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageUploadHistoryRepository extends JpaRepository<ImageUploadHistory, Long> {
    List<ImageUploadHistory> findByPersonId(Long personId);

    void deleteByPersonId(Long personId);

    void deleteByPersonIdIn(List<Long> personIds);

    int countByKey(String key);

    @Query(value = "select i from ImageUploadHistory i where i.person.familyTree.id=:familyTreeId")
    List<ImageUploadHistory> findByFamilyTreeId(@Param("familyTreeId")Long familyTreeId);
}
