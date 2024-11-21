package com.example.pickplace.member.repository.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_no;

    @Column(name = "user_ID")
    private String id;

    private String name;

    @Column(name = "phone_no")
    private String phoneNumber;

    private String email;

    private char gender;

    private LocalDate birth;

    @JsonIgnore
    @Setter
    @Column(name = "pwd")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Column(nullable = false)
    private boolean enabled = true;

    public void updateProfile(String name, String phoneNumber, String email) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

}
