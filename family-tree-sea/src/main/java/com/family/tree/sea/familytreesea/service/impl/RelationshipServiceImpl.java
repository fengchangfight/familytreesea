package com.family.tree.sea.familytreesea.service.impl;

import com.family.tree.sea.familytreesea.entity.Person;
import com.family.tree.sea.familytreesea.entity.Relationship;
import com.family.tree.sea.familytreesea.model.PersonInGraphVO;
import com.family.tree.sea.familytreesea.model.RelationExcelVO;
import com.family.tree.sea.familytreesea.model.RelationVO;
import com.family.tree.sea.familytreesea.repository.rdbms.RelationshipRepository;
import com.family.tree.sea.familytreesea.service.PersonService;
import com.family.tree.sea.familytreesea.service.RelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RelationshipServiceImpl implements RelationshipService {
    @Autowired
    private RelationshipRepository relationshipRepository;

    @Autowired
    private PersonService personService;

    @Override
    public Long save(Relationship relationship){
        relationshipRepository.save(relationship);
        return relationship.getId();
    }

    @Override
    public void save(List<Relationship> relationships){
        relationshipRepository.save(relationships);
    }

    @Override
    public Relationship findRelationByFromPersonAndToPerson(Long fromId, Long toId){
        return relationshipRepository.findByFromPersonIdAndToPersonId(fromId, toId);
    }

    @Override
    public List<RelationExcelVO> findRelationExcelByFamilyTreeId(Long familyTreeId){
        List<Relationship> relations =  relationshipRepository.findByFamilyTreeId(familyTreeId);
        return relations.stream().map(r->{return new RelationExcelVO(r.getFromPerson().getName(), r.getToPerson().getName(), r.getType());}).collect(Collectors.toList());
    }

    @Override
    public List<RelationVO> findRelationsByFamilyTreeId(Long familyTreeId){
        List<Relationship> relations =  relationshipRepository.findByFamilyTreeId(familyTreeId);
        return relations.stream().map(r->{return new RelationVO(r.getId(), r.getFromPerson().getId(), r.getToPerson().getId(), r.getType(), r.getHigher());}).collect(Collectors.toList());
    }

    @Override
    public List<RelationVO> getLinksByFamilyTreeIdAndPersonId(Long familyTreeId, Long personId){
        List<Relationship> relations =  relationshipRepository.findByFamilyTreeIdAndPersonId(familyTreeId, personId);
        return relations.stream().map(r->{return new RelationVO(r.getId(), r.getFromPerson().getId(), r.getToPerson().getId(), r.getType(), r.getHigher());}).collect(Collectors.toList());
    }

    @Override
    public List<PersonInGraphVO> getNodesByFamilyIdAndPersonId(Long familyTreeId, Long personId){
        Set<Person> personSet = new HashSet<>();

        List<Relationship> relations =  relationshipRepository.findByFamilyTreeIdAndPersonId(familyTreeId, personId);
        for(Relationship r: relations){
            personSet.add(r.getFromPerson());
            personSet.add(r.getToPerson());
        }
        Person p = personService.findOne(personId);
        personSet.add(p);
        List<Person> list = new ArrayList<Person>(personSet);
        return list.stream().map(r->{return new PersonInGraphVO(r.getId(), r.getName(), r.getSex(), r.getRank());}).collect(Collectors.toList());
    }

    @Override
    public void deleteRelationByFromPersonId(Long personId){
        relationshipRepository.deleteByFromPersonId(personId);
    }

    @Override
    public void deleteRelationByFromPersonIdAndToPersonId(Long srcId, Long targetId){
        relationshipRepository.deleteByFromPersonIdAndToPersonId(srcId, targetId);
    }

    @Override
    public List<RelationVO> findRelationAmongNodes(List<Long> personIds){
        List<Relationship> relationships = relationshipRepository.findByFromPersonIdInAndToPersonIdIn(personIds, personIds);
        return relationships.stream().map(r->{return new RelationVO(r.getId(), r.getFromPerson().getId(), r.getToPerson().getId(), r.getType(), r.getHigher());}).collect(Collectors.toList());
    }

    @Override
    public void deleteRelationByToPersonId(Long personId){
        relationshipRepository.deleteByToPersonId(personId);
    }

    @Override
    public void deleteRelationByFamilyTreeId(Long familyTreeId){
        relationshipRepository.deleteByFamilyTreeId(familyTreeId);
    }
}
