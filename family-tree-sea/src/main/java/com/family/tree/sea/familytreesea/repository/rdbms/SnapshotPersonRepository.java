package com.family.tree.sea.familytreesea.repository.rdbms;

import com.family.tree.sea.familytreesea.entity.SnapshotPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SnapshotPersonRepository extends JpaRepository<SnapshotPerson, Long> {
    int countByProfileImagePath(String key);
    List<SnapshotPerson> findBySnapshotId(Long snapshotId);
    void deleteBySnapshotId(Long id);
}
