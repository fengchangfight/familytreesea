package com.family.tree.sea.familytreesea.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserVO {
    private Long id;
    private String nickName;
    private String phone;
    private String email;
    private String profileImage;

    public UserVO(Long id, String nickName, String phone, String email, String profileImage) {
        this.id = id;
        this.nickName = nickName;
        this.phone = phone;
        this.email = email;
        this.profileImage = profileImage;
    }
}
