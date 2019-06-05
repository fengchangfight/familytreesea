package com.family.tree.sea.familytreesea.repository.rdbms;

import com.family.tree.sea.familytreesea.entity.Snapshot;
import com.family.tree.sea.familytreesea.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SnapshotRepository extends JpaRepository<Snapshot, Long> {
    int countByOriginFtId(Long familyTreeId);
    Snapshot findByCreatedByAndName(User user, String snapshotName);
    @Query(value = "select s from Snapshot s where s.createdBy=:user order by s.createdTime desc")
    List<Snapshot> findByCreatedByOrderByCreatedTimeDesc(@Param("user")User user);

    List<Snapshot> findByOriginFtId(Long id);
}
