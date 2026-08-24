package com.jhy.devpulse.domain.member.entity;

import com.jhy.devpulse.common.entity.BaseEntity;
import com.jhy.devpulse.domain.project.entity.Project;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "members")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 30)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MemberStatus status = MemberStatus.ACTIVE;

    @OneToMany(mappedBy = "member")
    @Builder.Default
    private List<Project> projects = new ArrayList<>();

    public static Member create(
            String email,
            String password,
            String name) {
        return Member.builder()
                .email(email)
                .password(password)
                .name(name)
                .role(Role.USER)
                .status(MemberStatus.ACTIVE)
                .build();
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void deactivate() {
        this.status = MemberStatus.INACTIVE;
    }
}
