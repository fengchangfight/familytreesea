package com.family.tree.sea.familytreesea.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class FamilyTreeInfoVO {
    private Long id;
    private String name;
    private String description;
    private String createdBy;

    public FamilyTreeInfoVO(Long id, String name, String description, String createdBy) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
    }
}
