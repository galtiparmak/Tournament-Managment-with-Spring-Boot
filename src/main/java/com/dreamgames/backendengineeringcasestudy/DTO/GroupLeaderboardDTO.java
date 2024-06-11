package com.dreamgames.backendengineeringcasestudy.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GroupLeaderboardDTO {
    private String userId;
    private String userName;
    private String country;
    private int score;

}

