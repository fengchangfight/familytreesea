package com.family.tree.sea.familytreesea.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="ft_tree")
@Getter
@Setter
@NoArgsConstructor
public class FamilyTree {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name="description")
    private String description;

    @ManyToOne
    @JoinColumn(name="created_by", referencedColumnName = "id")
    private User createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on")
    private Date createdOn;
}
