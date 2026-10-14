package com.kodat.complexhotelsystem.entities;

import com.kodat.complexhotelsystem.enums.PersonCategory;
import com.kodat.complexhotelsystem.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
public class Person implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String lastname;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String hashedSecret;
    @Column(nullable = false, unique = true)
    private String phone;
    @Column(nullable = false)
    private Role role;
    private PersonCategory category;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("Role:"+role.name()));
    }

    @Override
    public @Nullable String getPassword() {
        return hashedSecret;
    }
}
