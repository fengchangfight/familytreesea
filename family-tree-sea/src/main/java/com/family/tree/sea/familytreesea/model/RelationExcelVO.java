package com.family.tree.sea.familytreesea.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RelationExcelVO {
    private String fromPersonName;
    private String toPersonName;
    private String type;

    public RelationExcelVO(String fromPersonName, String toPersonName, String type) {
        this.fromPersonName = fromPersonName;
        this.toPersonName = toPersonName;
        this.type = type;
    }
}
