package com.family.tree.sea.familytreesea.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserSelectVO {
    private Long id;
    private String nickname;
    private String profilePath;

    public UserSelectVO(Long id, String nickname, String profilePath) {
        this.id = id;
        this.nickname = nickname;
        this.profilePath = profilePath;
    }
}
