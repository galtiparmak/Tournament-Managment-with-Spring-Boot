package com.dreamgames.backendengineeringcasestudy.repository;

import com.dreamgames.backendengineeringcasestudy.entity.Tournament;
import com.dreamgames.backendengineeringcasestudy.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserGroupRepository extends JpaRepository<UserGroup, String> {
    List<UserGroup> findAllByUserId(Long userId);
    List<UserGroup> findByGroupIdOrderByScoreDesc(Long groupId);
    List<UserGroup> findByGroup_Tournament(Tournament tournament);
    Optional<UserGroup> findByUserIdAndActive(Long userId, boolean active);

}
