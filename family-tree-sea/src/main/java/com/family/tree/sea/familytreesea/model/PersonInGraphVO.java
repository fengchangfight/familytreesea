package com.family.tree.sea.familytreesea.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PersonInGraphVO {
    private Long id;
    private String name;
    private Integer sex;
    private Integer level;
    private Integer leafDescendantCount;
    private Integer directDescendantCount;
    private Integer rank;


    public PersonInGraphVO(Long id, String name, Integer sex) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.level = 0;
        this.leafDescendantCount = 0;
        this.directDescendantCount = 0;
        this.rank = 0;
    }

    public PersonInGraphVO(Long id, String name, Integer sex, Integer rank) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.level = 0;
        this.leafDescendantCount = 0;
        this.directDescendantCount = 0;
        this.rank = rank;
    }
}
