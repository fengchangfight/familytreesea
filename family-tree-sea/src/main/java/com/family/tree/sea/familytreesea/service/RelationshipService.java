package com.family.tree.sea.familytreesea.service;

import com.family.tree.sea.familytreesea.entity.Relationship;
import com.family.tree.sea.familytreesea.model.PersonInGraphVO;
import com.family.tree.sea.familytreesea.model.RelationExcelVO;
import com.family.tree.sea.familytreesea.model.RelationVO;

import java.util.List;

public interface RelationshipService {
    Long save(Relationship relationship);
    void save(List<Relationship> relationships);

    Relationship findRelationByFromPersonAndToPerson(Long fromId, Long toId);

    List<RelationVO> findRelationsByFamilyTreeId(Long familyTreeId);
    List<RelationVO> getLinksByFamilyTreeIdAndPersonId(Long familyTreeId, Long personId);

    /**
     *
     * @param familyTreeId
     * @param personId
     * @return return list of nodes containing the specific person Id, and all the IDs that has relation with this person, only id, name, sex properties are returned because those are what the graph need
     */
    List<PersonInGraphVO> getNodesByFamilyIdAndPersonId(Long familyTreeId, Long personId);

    List<RelationExcelVO> findRelationExcelByFamilyTreeId(Long familyTreeId);

    void deleteRelationByFromPersonId(Long personId);
    void deleteRelationByFromPersonIdAndToPersonId(Long srcId, Long targetId);

    /**
     * find all relations among given nodes
     * @param personIds
     * @return
     */
    List<RelationVO> findRelationAmongNodes(List<Long> personIds);

    void deleteRelationByToPersonId(Long personId);
    void deleteRelationByFamilyTreeId(Long id);
}
