package com.family.tree.sea.familytreesea.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="ft_img_upload_history")
@Getter
@Setter
@NoArgsConstructor
public class ImageUploadHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="upload_by", referencedColumnName = "id")
    private User uploadBy;

    @ManyToOne
    @JoinColumn(name="person_id", referencedColumnName = "id")
    private Person person;

    @Column(name = "obj_key")
    private String key;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="upload_time")
    private Date uploadTime;

    public ImageUploadHistory(User uploadBy, Person person, String key, Date uploadTime) {
        this.uploadBy = uploadBy;
        this.person = person;
        this.key = key;
        this.uploadTime = uploadTime;
    }
}
