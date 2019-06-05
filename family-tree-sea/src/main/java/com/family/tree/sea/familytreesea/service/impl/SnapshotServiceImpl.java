package com.family.tree.sea.familytreesea.service.impl;

import com.family.tree.sea.familytreesea.entity.*;
import com.family.tree.sea.familytreesea.repository.rdbms.SnapshotImageUploadHistoryRepository;
import com.family.tree.sea.familytreesea.repository.rdbms.SnapshotPersonRepository;
import com.family.tree.sea.familytreesea.repository.rdbms.SnapshotRelationRepository;
import com.family.tree.sea.familytreesea.repository.rdbms.SnapshotRepository;
import com.family.tree.sea.familytreesea.service.SnapshotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SnapshotServiceImpl implements SnapshotService {
    @Autowired
    private SnapshotRepository snapshotRepository;

    @Autowired
    private SnapshotPersonRepository snapshotPersonRepository;

    @Autowired
    private SnapshotImageUploadHistoryRepository snapshotImageUploadHistoryRepository;

    @Autowired
    private SnapshotRelationRepository snapshotRelationRepository;

    @Override
    public Snapshot save(Snapshot snapshot) {
        return snapshotRepository.save(snapshot);
    }

    @Override
    public void save(List<Snapshot> snapshots){
        snapshotRepository.save(snapshots);
    }

    @Override
    public List<SnapshotPerson> saveSnapshotPeople(List<SnapshotPerson> snapshotPeople){
        return snapshotPersonRepository.save(snapshotPeople);
    }

    @Override
    public  List<SnapshotRelation> saveSnapshotRelation(List<SnapshotRelation> snapshotRelations){
        return snapshotRelationRepository.save(snapshotRelations);
    }

    @Override
    public List<SnapshotImageUpload> saveImageUploadHitory(List<SnapshotImageUpload> snapshotImageUploads){
        return snapshotImageUploadHistoryRepository.save(snapshotImageUploads);
    }

    @Override
    public int countSnapshots4FamilyTree(Long familyTreeId){
        return snapshotRepository.countByOriginFtId(familyTreeId);
    }

    @Override
    public Snapshot findByUserAndSnapshotName(User user, String snapshotName){
        return snapshotRepository.findByCreatedByAndName(user, snapshotName);
    }


    @Override
    public List<SnapshotPerson> findPersonBySnapshotId(Long snapshotId){
        return snapshotPersonRepository.findBySnapshotId(snapshotId);
    }

    @Override
    public List<SnapshotRelation> findRelationsBySnapshotId(Long snapshotId){
        return snapshotRelationRepository.findBySnapshotId(snapshotId);
    }

    @Override
    public List<SnapshotImageUpload> findImageUploadsBySnapshotId(Long snapshotId){
        return snapshotImageUploadHistoryRepository.findBySnapshotId(snapshotId);
    }

    @Override
    public List<Snapshot> findByUser(User user){
        return snapshotRepository.findByCreatedByOrderByCreatedTimeDesc(user);
    }

    @Override
    public Snapshot findOne(Long id){
        return snapshotRepository.findOne(id);
    }

    @Override
    public void deleteSnapshotRelationById(Long id){
        snapshotRelationRepository.deleteBySnapshotId(id);
    }

    @Override
    public void deleteSnapshotImageUploadById(Long id){
        snapshotImageUploadHistoryRepository.deleteBySnapshotId(id);
    }

    @Override
    public void deleteSnapshotPersonById(Long id){
        snapshotPersonRepository.deleteBySnapshotId(id);
    }

    @Override
    public void deleteById(Long id){
        snapshotRepository.delete(id);
    }

}
