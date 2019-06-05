package com.family.tree.sea.familytreesea.repository.rdbms;

import com.family.tree.sea.familytreesea.entity.SnapshotImageUpload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SnapshotImageUploadHistoryRepository extends JpaRepository <SnapshotImageUpload,Long>{
    int countByObjKey(String key);
    List<SnapshotImageUpload> findBySnapshotId(Long snapshotId);
    void deleteBySnapshotId(Long id);
}
