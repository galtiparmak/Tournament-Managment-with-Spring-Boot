package com.dreamgames.backendengineeringcasestudy.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "level", nullable = false)
    private int level;

    @Column(name = "coins", nullable = false)
    private int coins;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Column(name = "in_tournament", nullable = false)
    private boolean inTournament;

    @Column(name = "reward_claimed", nullable = false)
    private boolean rewardClaimed;
}

