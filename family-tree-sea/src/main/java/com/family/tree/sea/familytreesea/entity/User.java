package com.family.tree.sea.familytreesea.entity;

import com.family.tree.sea.familytreesea.utils.LicenseUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name="ft_user")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="username", columnDefinition = "VARCHAR(200)")
    private String username;

    @Column(name="nickname", columnDefinition = "VARCHAR(200)")
    private String nickname;

    @Column(name="email", columnDefinition = "VARCHAR(100)")
    private String email;

    @Column(name="salt", columnDefinition = "VARCHAR(100)")
    private String salt;

    @Column(name="phone", columnDefinition = "VARCHAR(45)")
    private String phone;

    @Column(name="password", columnDefinition = "VARCHAR(200)")
    private String password;

    @Transient
    private String passwordConfirm;

    @ManyToMany
    @JoinTable(name = "ft_user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @Column(name = "license_code")
    private String licenseCode;

    @Column(name = "register_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date registerDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(username, user.username) &&
                Objects.equals(nickname, user.nickname) &&
                Objects.equals(email, user.email) &&
                Objects.equals(salt, user.salt) &&
                Objects.equals(phone, user.phone) &&
                Objects.equals(password, user.password) &&
                Objects.equals(passwordConfirm, user.passwordConfirm) &&
                Objects.equals(roles, user.roles) &&
                Objects.equals(licenseCode, user.licenseCode) &&
                Objects.equals(registerDate, user.registerDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, nickname, email, salt, phone, password, passwordConfirm, roles, licenseCode, registerDate);
    }

    public int getMaxFamilyCountFromLicenseCode(LicenseUtil licenseUtil, String licenseCode){
        Map<String,String> licenseContent = licenseUtil.parseLicenseCode(licenseCode);
        if(licenseContent.containsKey("max_family_count")){
            return Integer.valueOf(licenseContent.get("max_family_count"));
        }else{
            return 0;
        }
    }
}
