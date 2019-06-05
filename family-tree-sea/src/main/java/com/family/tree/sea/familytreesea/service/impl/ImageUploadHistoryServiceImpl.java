package com.family.tree.sea.familytreesea.service.impl;

import com.family.tree.sea.familytreesea.entity.ImageUploadHistory;
import com.family.tree.sea.familytreesea.repository.rdbms.ImageUploadHistoryRepository;
import com.family.tree.sea.familytreesea.service.ImageUploadHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ImageUploadHistoryServiceImpl implements ImageUploadHistoryService {
    @Autowired
    private ImageUploadHistoryRepository imageUploadHistoryRepository;

    @Override
    @Transactional
    public void save(ImageUploadHistory history){
        imageUploadHistoryRepository.save(history);

    }

    @Override
    @Transactional
    public void save(List<ImageUploadHistory> history){
        imageUploadHistoryRepository.save(history);
    }

    @Override
    public List<ImageUploadHistory> findByPersonId(Long personId){
        return imageUploadHistoryRepository.findByPersonId(personId);
    }

    @Override
    public void deleteByPersonId(Long personId){
        imageUploadHistoryRepository.deleteByPersonId(personId);
    }

    @Override
    public void deleteByPersonIds(List<Long> personIds){
        imageUploadHistoryRepository.deleteByPersonIdIn(personIds);
    }

    @Override
    public int countByObjKey(String key){
        return imageUploadHistoryRepository.countByKey(key);
    }

    @Override
    public List<ImageUploadHistory> findImageUploadHistoryByFamilyTreeId(Long familyTreeId){
        return imageUploadHistoryRepository.findByFamilyTreeId(familyTreeId);
    }
}
