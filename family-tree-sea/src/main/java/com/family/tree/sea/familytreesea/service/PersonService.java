package com.family.tree.sea.familytreesea.service;

import com.family.tree.sea.familytreesea.entity.Person;
import com.family.tree.sea.familytreesea.model.PersonDetailVO;
import com.family.tree.sea.familytreesea.model.PersonInGraphVO;

import java.util.List;

public interface PersonService {
    Long save(Person person);

    void save(List<Person> person);

    Person findOne(Long id);

    List<PersonInGraphVO> getNodesByFamilyId(Long familyTreeId);

    List<PersonDetailVO> findPeopleDetailsByFamilyTreeId(Long familyTreeId);

    void deleteByPersonId(Long id);

    void deleteByFamilyTreeId(Long id);

    List<Long> findPersonIdsByFamilyTreeId(Long familyTreeId);

}
