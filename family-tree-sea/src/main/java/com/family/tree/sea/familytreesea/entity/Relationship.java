package com.family.tree.sea.familytreesea.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="ft_relationship")
@Getter
@Setter
@NoArgsConstructor
public class Relationship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "family_tree_id", referencedColumnName = "id")
    private FamilyTree familyTree;

    @ManyToOne
    @JoinColumn(name = "from_person_id", referencedColumnName = "id")
    private Person fromPerson;


    @ManyToOne
    @JoinColumn(name = "to_person_id", referencedColumnName = "id")
    private Person toPerson;

    @Column(name = "type")
    private String type;

    @Column(name = "higher")
    private Integer higher;

    public Relationship(FamilyTree familyTree, Person fromPerson, Person toPerson, String type, Integer higher) {
        this.familyTree = familyTree;
        this.fromPerson = fromPerson;
        this.toPerson = toPerson;
        this.type = type;
        this.higher = higher;
    }
}
