package com.family.tree.sea.familytreesea.repository.rdbms;

import com.family.tree.sea.familytreesea.entity.SnapshotRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SnapshotRelationRepository extends JpaRepository<SnapshotRelation, Long> {
    List<SnapshotRelation> findBySnapshotId(Long snapshotId);
    void deleteBySnapshotId(Long id);
}
