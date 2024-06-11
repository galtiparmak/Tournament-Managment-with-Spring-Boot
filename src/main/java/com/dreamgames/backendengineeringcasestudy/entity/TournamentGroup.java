package com.dreamgames.backendengineeringcasestudy.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "tournament_group")
public class TournamentGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @JsonManagedReference
    @OneToMany(mappedBy = "group")
    private List<UserGroup> userGroups;

    @Column(name = "competition_started", nullable = false)
    private boolean competitionStarted = false;

}


