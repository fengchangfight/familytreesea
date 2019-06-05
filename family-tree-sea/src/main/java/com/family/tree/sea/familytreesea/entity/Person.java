package com.family.tree.sea.familytreesea.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="ft_person")
@Getter
@Setter
@NoArgsConstructor
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "sex")
    private Integer sex;

    @ManyToOne
    @JoinColumn(name = "family_tree_id", referencedColumnName = "id")
    private FamilyTree familyTree;

    @Column(name = "life_description")
    private String lifeDescription;

    @Column(name = "short_description")
    private String shortDescription;

    @Column(name = "born")
    private String born;

    @Column(name = "death")
    private String death;

    @Column(name = "profile_img_path")
    private String profileImagePath;

    @Column(name = "address")
    private String address;

    @Column(name = "phone")
    private String phone;

    @Column(name="zibei")
    private String zibei;

    @Column(name="rank")
    private Integer rank;

    public Person(String name, Integer sex, FamilyTree familyTree, String lifeDescription, String shortDescription, String born, String death, String profileImagePath, String address, String phone, String zibei, Integer rank) {
        this.name = name;
        this.sex = sex;
        this.familyTree = familyTree;
        this.lifeDescription = lifeDescription;
        this.shortDescription = shortDescription;
        this.born = born;
        this.death = death;
        this.profileImagePath = profileImagePath;
        this.address = address;
        this.phone = phone;
        this.zibei = zibei;
        this.rank = rank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        return new EqualsBuilder()
                .append(id, person.id)
                .append(name, person.name)
                .append(sex, person.sex)
                .append(familyTree, person.familyTree)
                .append(lifeDescription, person.lifeDescription)
                .append(shortDescription, person.shortDescription)
                .append(born, person.born)
                .append(death, person.death)
                .append(profileImagePath, person.profileImagePath)
                .append(address, person.address)
                .append(phone, person.phone)
                .append(zibei, person.zibei)
                .append(rank, person.rank)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(sex)
                .append(familyTree)
                .append(lifeDescription)
                .append(shortDescription)
                .append(born)
                .append(death)
                .append(profileImagePath)
                .append(address)
                .append(phone)
                .append(zibei)
                .append(rank)
                .toHashCode();
    }
}
