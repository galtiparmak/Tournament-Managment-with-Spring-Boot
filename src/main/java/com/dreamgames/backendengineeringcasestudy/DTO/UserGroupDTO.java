package com.dreamgames.backendengineeringcasestudy.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserGroupDTO {
    private String userId;
    private String userName;
    private int level;
    private int coins;
    private String country;
    private int score;
}
