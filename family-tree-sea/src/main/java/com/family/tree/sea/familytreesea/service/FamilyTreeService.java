package com.family.tree.sea.familytreesea.service;

import com.family.tree.sea.familytreesea.entity.FamilyTree;
import com.family.tree.sea.familytreesea.entity.User;

import java.util.List;
import java.util.Map;

public interface FamilyTreeService {
    Long countFamilyTreeCreatedByUser(User user);

    FamilyTree findFamilyTreeById(Long id);
    List<FamilyTree> findFamilyTreeByIds(List<Long> ids);

    Long save(FamilyTree familyTree);

    List<FamilyTree> findFamilyTreesByUser(User user);

    void deleteById(Long id);

    Map<String,Object> loadFtData(Long familyTreeId);
    Map<String,Object> loadFtDataPartial(Long familyTreeId, Long personId);

    List<FamilyTree> findByUserAndFamilyTreeName(User user, String familyTreeName);
}
