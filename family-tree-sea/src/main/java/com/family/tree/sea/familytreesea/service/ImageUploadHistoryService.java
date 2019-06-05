package com.family.tree.sea.familytreesea.service;

import com.family.tree.sea.familytreesea.entity.ImageUploadHistory;

import java.util.List;

public interface ImageUploadHistoryService {
    void save(ImageUploadHistory history);
    void save(List<ImageUploadHistory> history);

    List<ImageUploadHistory> findByPersonId(Long personId);

    void deleteByPersonId(Long personId);
    void deleteByPersonIds(List<Long> personId);
    int countByObjKey(String key);

    List<ImageUploadHistory> findImageUploadHistoryByFamilyTreeId(Long familyTreeId);
}
