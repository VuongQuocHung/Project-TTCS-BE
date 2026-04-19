package com.laptopshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    private String fullName;
    private String phoneNumber;
    private String address;

    @Builder.Default
    private boolean enabled = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String resetToken;
    private java.time.LocalDateTime resetTokenExpiry;

    private String provider;
    private String providerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;
}
