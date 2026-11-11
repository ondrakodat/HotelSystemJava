package com.kodat.complexhotelsystem.entities;

import com.kodat.complexhotelsystem.enums.PersonCategory;
import com.kodat.complexhotelsystem.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(indexes = {
        @Index(name = "idx_person_email", columnList = "email")
})
    /**
    Class for representing user of our program typicaly someone who is going to book a hotel room or sign in for some hotel activity
    */
public class Person implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String firstname;
    @Column(nullable = false)
    private String lastname;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String secret;
    @Column(nullable = false, unique = true)
    private String phone;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
    @Enumerated(EnumType.ORDINAL)
    private PersonCategory category = PersonCategory.ADULT;
    @OneToOne
    @JoinColumn(name = "token_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private ResetPasswordToken token;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_"+role.name()));
    }

    @Override
    public @Nullable String getPassword() {
        return secret;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public String getFirstname(){
        return firstname;
    }
}
