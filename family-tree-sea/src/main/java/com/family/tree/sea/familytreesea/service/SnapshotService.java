package com.family.tree.sea.familytreesea.service;

import com.family.tree.sea.familytreesea.entity.*;

import java.util.List;

public interface SnapshotService {
    Snapshot save(Snapshot snapshot);
    void save(List<Snapshot> snapshots);
    List<SnapshotPerson> saveSnapshotPeople(List<SnapshotPerson> snapshotPeople);
    List<SnapshotRelation> saveSnapshotRelation(List<SnapshotRelation> snapshotRelations);
    List<SnapshotImageUpload> saveImageUploadHitory(List<SnapshotImageUpload> snapshotImageUploads);
    int countSnapshots4FamilyTree(Long familyTreeId);
    Snapshot findByUserAndSnapshotName(User user, String snapshotName);
    List<SnapshotPerson> findPersonBySnapshotId(Long snapshotId);
    List<SnapshotRelation> findRelationsBySnapshotId(Long snapshotId);
    List<SnapshotImageUpload> findImageUploadsBySnapshotId(Long snapshotId);
    List<Snapshot> findByUser(User user);
    Snapshot findOne(Long id);
    void deleteSnapshotRelationById(Long id);
    void deleteSnapshotImageUploadById(Long id);
    void deleteSnapshotPersonById(Long id);
    void deleteById(Long id);
}
