package com.dreamgames.backendengineeringcasestudy.DTO;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TournamentGroupDTO {
    private Long groupId;
    private List<UserGroupDTO> users;
    private boolean competitionStarted;
}
