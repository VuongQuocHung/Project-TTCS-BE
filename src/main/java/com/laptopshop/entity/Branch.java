package com.laptopshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "branches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Branch extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String address;

    private String phone;

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL)
    private List<User> employees;

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL)
    private List<Inventory> stocks;

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL)
    private List<Order> orders;
}
