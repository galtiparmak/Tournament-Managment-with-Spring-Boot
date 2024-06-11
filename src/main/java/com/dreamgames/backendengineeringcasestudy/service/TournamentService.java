package com.dreamgames.backendengineeringcasestudy.service;

import com.dreamgames.backendengineeringcasestudy.DTO.GroupLeaderboardDTO;
import com.dreamgames.backendengineeringcasestudy.DTO.TournamentGroupDTO;
import com.dreamgames.backendengineeringcasestudy.DTO.UserGroupDTO;
import com.dreamgames.backendengineeringcasestudy.entity.Tournament;
import com.dreamgames.backendengineeringcasestudy.entity.TournamentGroup;
import com.dreamgames.backendengineeringcasestudy.entity.User;
import com.dreamgames.backendengineeringcasestudy.entity.UserGroup;
import com.dreamgames.backendengineeringcasestudy.repository.TournamentGroupRepository;
import com.dreamgames.backendengineeringcasestudy.repository.TournamentRepository;
import com.dreamgames.backendengineeringcasestudy.repository.UserGroupRepository;
import com.dreamgames.backendengineeringcasestudy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing Tournaments.
 */
@Service
public class TournamentService {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TournamentGroupRepository tournamentGroupRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    /**
     * Allows a user to enter the current tournament.
     *
     * @param userId The ID of the user entering the tournament.
     * @return true if the user successfully enters the tournament, false otherwise.
     */
    public boolean enterTournament(String userId) {
        User user = userRepository.findByUserId(userId);
        Tournament tournament = getCurrentTournament();

        if (tournament == null) {
            return false;  // No active tournament found
        }

        if (user != null && user.getLevel() >= 20 && user.getCoins() >= 1000 && !user.isInTournament() && user.isRewardClaimed()) {
            user.setCoins(user.getCoins() - 1000);
            user.setInTournament(true);
            user.setRewardClaimed(false);
            userRepository.save(user);

            TournamentGroup group = findOrCreateGroup(tournament, user);
            UserGroup userGroup = new UserGroup();
            userGroup.setUser(user);
            userGroup.setGroup(group);
            userGroup.setScore(0);
            userGroupRepository.save(userGroup);
            return true;
        }
        return false;
    }

    /**
     * Creates a new daily tournament. Scheduled to run at midnight UTC every day.
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "UTC")
    public void createDailyTournament() {
        createNewTournament("Daily Tournament");
    }

    /**
     * Ends the current tournament. Scheduled to run at 20:00 UTC every day.
     */
    @Scheduled(cron = "0 0 20 * * *", zone = "UTC")
    public void endCurrentTournament() {
        Tournament currentTournament = getCurrentTournament();
        if (currentTournament != null) {
            System.out.println(currentTournament.getTournamentName() + " has ended");

            // Update end time of the current tournament
            currentTournament.setEndTime(new Date());
            tournamentRepository.save(currentTournament);

            // Fetch all tournament groups for the current tournament
            List<TournamentGroup> groups = currentTournament.getGroups();

            // Update users in each group
            for (TournamentGroup group : groups) {
                List<UserGroup> userGroups = group.getUserGroups();
                for (UserGroup userGroup : userGroups) {
                    User user = userGroup.getUser();
                    user.setInTournament(false);
                    // Update the user in the repository
                    userRepository.save(user);
                }
            }
        }
    }

    /**
     * Creates a new tournament with the specified name.
     *
     * @param tournamentName The name of the new tournament.
     * @return The created Tournament entity.
     */
    public Tournament createNewTournament(String tournamentName) {
        // Set up new tournament times
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Istanbul"));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startTime = calendar.getTime();

        calendar.set(Calendar.HOUR_OF_DAY, 20);
        Date endTime = calendar.getTime();

        // Create new tournament
        Tournament tournament = new Tournament();
        tournament.setTournamentName(tournamentName);
        tournament.setStartTime(startTime);
        tournament.setEndTime(endTime);
        System.out.println("Tournament created");
        return tournamentRepository.save(tournament);
    }

    /**
     * Retrieves the current active tournament.
     *
     * @return The current Tournament entity, or null if no active tournament is found.
     */
    public Tournament getCurrentTournament() {
        Date now = new Date();
        List<Tournament> tournaments = tournamentRepository.findAll();
        for (Tournament tournament : tournaments) {
            if (tournament.getStartTime().before(now) && tournament.getEndTime().after(now)) {
                return tournament;
            }
        }
        return null;
    }

    /**
     * Finds an existing group for the user in the specified tournament or creates a new one if necessary.
     *
     * @param tournament The tournament in which the user wants to participate.
     * @param user The user to be added to a group.
     * @return The found or newly created TournamentGroup entity.
     */
    private TournamentGroup findOrCreateGroup(Tournament tournament, User user) {
        List<TournamentGroup> groups = tournamentGroupRepository.findAll();
        for (TournamentGroup group : groups) {
            if (group.getTournament().equals(tournament) && group.getUserGroups().size() < 5 &&
                    group.getUserGroups().stream().noneMatch(ug -> ug.getUser().getCountry().equals(user.getCountry()))) {
                System.out.println("User " + user.getUserId() + " added to group " + group.getId() + " in tournament " + tournament.getTournamentName());
                // Check the group size and start competition if needed
                if (group.getUserGroups().size() + 1 == 5) {
                    System.out.println("Group size will be 5 after adding user, starting competition");
                    startGroupCompetition(group);
                }
                return group;
            }
        }
        System.out.println("Group Created");
        TournamentGroup newGroup = new TournamentGroup();
        newGroup.setTournament(tournament);
        TournamentGroup savedGroup = tournamentGroupRepository.save(newGroup);
        System.out.println("Group with ID: " + savedGroup.getId() + " in tournament " + tournament.getId());
        return savedGroup;
    }

    /**
     * Starts the competition for the specified group.
     *
     * @param group The TournamentGroup entity for which the competition will be started.
     */
    private void startGroupCompetition(TournamentGroup group) {
        // Re-fetch the group from the repository to ensure it's managed
        TournamentGroup managedGroup = tournamentGroupRepository.findById(group.getId())
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Implement any necessary logic when the group competition starts
        managedGroup.setCompetitionStarted(true);
        tournamentGroupRepository.save(managedGroup);
    }

    /**
     * Retrieves the active tournament groups along with their users.
     *
     * @return A list of TournamentGroupDTO objects representing the active tournament groups and their users.
     */
    public List<TournamentGroupDTO> getActiveTournamentGroupsWithUsers() {
        Tournament activeTournament = getCurrentTournament();
        if (activeTournament != null) {
            List<TournamentGroup> groups = tournamentGroupRepository.findByTournament(activeTournament);
            return groups.stream().map(group -> {
                TournamentGroupDTO groupDTO = new TournamentGroupDTO();
                groupDTO.setGroupId(group.getId());
                groupDTO.setCompetitionStarted(group.isCompetitionStarted());
                List<UserGroupDTO> users = group.getUserGroups().stream().map(userGroup -> {
                    User user = userGroup.getUser();
                    UserGroupDTO userDTO = new UserGroupDTO();
                    userDTO.setUserId(user.getUserId());
                    userDTO.setUserName(user.getUserName());
                    userDTO.setLevel(user.getLevel());
                    userDTO.setCoins(user.getCoins());
                    userDTO.setCountry(user.getCountry());
                    userDTO.setScore(userGroup.getScore());
                    return userDTO;
                }).collect(Collectors.toList());
                groupDTO.setUsers(users);
                return groupDTO;
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * Completes a level for the specified user. If the user is in a tournament,
     * their score in the tournament is incremented.
     *
     * @param userId The ID of the user completing the level.
     */
    public void completeLevel(String userId) {
        User user = userService.updateLevel(userId);
        if (user != null) {
            if (user.isInTournament()) {
                Optional<UserGroup> optionalUserGroup = userGroupRepository.findByUserIdAndActive(user.getId(), true);
                if (optionalUserGroup.isPresent()) {
                    UserGroup userGroup = optionalUserGroup.get();
                    if (userGroup.getGroup().isCompetitionStarted()) {
                        userGroup.setScore(userGroup.getScore() + 1);
                        System.out.println("User " + userId + " completed level in tournament " + getCurrentTournament().getTournamentName());
                        userGroupRepository.save(userGroup);
                    }
                }
            }
        } else {
            System.out.println("Could not find such user");
        }
    }

    /**
     * Allows a user to claim their reward after a tournament has ended.
     *
     * @param userId The ID of the user claiming the reward.
     * @return true if the reward is successfully claimed, false otherwise.
     */
    public boolean claimReward(String userId) {
        User user = userRepository.findByUserId(userId);
        if (user != null) {
            List<UserGroup> userGroups = userGroupRepository.findAllByUserId(user.getId());
            boolean rewardClaimed = false;

            for (UserGroup userGroup : userGroups) {
                if (!userGroup.isActive()) {
                    continue;
                }

                TournamentGroup group = userGroup.getGroup();
                Tournament tournament = group.getTournament();

                // Check if the tournament has ended
                Date now = new Date();
                if (now.after(tournament.getEndTime()) && !user.isRewardClaimed()) {
                    // Fetch the leaderboard for the user's group directly
                    List<UserGroup> groupUserGroups = userGroupRepository.findByGroupIdOrderByScoreDesc(group.getId());

                    if (!groupUserGroups.isEmpty()) {
                        UserGroup firstPlace = groupUserGroups.get(0);
                        UserGroup secondPlace = groupUserGroups.size() > 1 ? groupUserGroups.get(1) : null;

                        if (firstPlace != null && firstPlace.getUser().getUserId().equals(userId)) {
                            user.setCoins(user.getCoins() + 10000);
                        } else if (secondPlace != null && secondPlace.getUser().getUserId().equals(userId)) {
                            user.setCoins(user.getCoins() + 5000);
                        }
                    }

                    user.setRewardClaimed(true);
                    user.setInTournament(false);
                    rewardClaimed = true;

                    // Mark the user group as inactive
                    userGroup.setActive(false);
                    userGroupRepository.save(userGroup);
                }
            }

            if (rewardClaimed) {
                userRepository.save(user);
                System.out.println("User " + userId + " claimed the tournament reward");
                return true;
            } else {
                System.out.println("No rewards to claim or tournament has not ended yet for user " + userId);
            }
        }
        return false;
    }

    /**
     * Retrieves the leaderboard for a specific group in the current tournament.
     *
     * @param groupId The ID of the group for which to fetch the leaderboard.
     * @return A list of GroupLeaderboardDTO objects representing the leaderboard.
     */
    public List<GroupLeaderboardDTO> getGroupLeaderboard(Long groupId) {
        System.out.println("Fetch group leaderboard");
        Tournament currentTournament = getCurrentTournament();
        if (currentTournament == null) {
            throw new IllegalStateException("No active tournament found");
        }

        // Fetch all user groups for the current tournament
        List<UserGroup> userGroups = userGroupRepository.findByGroup_Tournament(currentTournament);

        // Group user groups by their group IDs
        Map<Long, List<UserGroup>> groupUserGroupsMap = userGroups.stream()
                .collect(Collectors.groupingBy(ug -> ug.getGroup().getId()));

        // Get user groups for the specified group ID
        List<UserGroup> groupUserGroups = groupUserGroupsMap.get(groupId);

        if (groupUserGroups == null) {
            // No group found with the specified ID
            return Collections.emptyList();
        }

        return groupUserGroups.stream()
                .sorted(Comparator.comparingInt(UserGroup::getScore).reversed())
                .map(userGroup -> new GroupLeaderboardDTO(
                        userGroup.getUser().getUserId(),
                        userGroup.getUser().getUserName(),
                        userGroup.getUser().getCountry(),
                        userGroup.getScore()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the leaderboard of countries based on the scores of users in the current tournament.
     *
     * @return A map where the key is the country name and the value is the total score of users from that country, sorted by score in descending order.
     */
    public Map<String, Integer> getCountryLeaderboard() {
        Tournament currentTournament = getCurrentTournament();
        if (currentTournament == null) {
            throw new IllegalStateException("No active tournament found");
        }

        List<UserGroup> userGroups = userGroupRepository.findByGroup_Tournament(currentTournament);
        Map<String, Integer> countryScores = new HashMap<>();

        for (UserGroup userGroup : userGroups) {
            String country = userGroup.getUser().getCountry();
            countryScores.put(country, countryScores.getOrDefault(country, 0) + userGroup.getScore());
        }

        return countryScores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /**
     * Retrieves the rank of a specific user in the current tournament.
     *
     * @param userId The ID of the user whose rank is to be fetched.
     * @return The rank of the user in the tournament.
     * @throws IllegalStateException if no active tournament is found or if the user is not found in the tournament.
     */
    public int getUserRank(String userId, Long tournamentId) {
        Tournament specifiedTournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalStateException("Tournament not found"));

        List<UserGroup> allUserGroups = userGroupRepository.findByGroup_Tournament(specifiedTournament);

        List<UserGroupDTO> allUserScores = allUserGroups.stream()
                .map(ug -> new UserGroupDTO(
                        ug.getUser().getUserId(),
                        ug.getUser().getUserName(),
                        ug.getUser().getLevel(),
                        ug.getUser().getCoins(),
                        ug.getUser().getCountry(),
                        ug.getScore()
                ))
                .collect(Collectors.toList());

        quickSort(allUserScores, 0, allUserScores.size() - 1);

        for (int i = 0; i < allUserScores.size(); i++) {
            if (allUserScores.get(i).getUserId().equals(userId)) {
                return i + 1;  // Rank is 1-based
            }
        }

        throw new IllegalStateException("User not found in the tournament");
    }

    /**
     * Sorts a list of UserGroupDTO objects using the QuickSort algorithm.
     * The sorting is done in descending order based on the score.
     *
     * @param list The list of UserGroupDTO objects to be sorted.
     * @param low The starting index of the portion of the list to be sorted.
     * @param high The ending index of the portion of the list to be sorted.
     */
    private void quickSort(List<UserGroupDTO> list, int low, int high) {
        if (low < high) {
            int pi = partition(list, low, high);

            quickSort(list, low, pi - 1);
            quickSort(list, pi + 1, high);
        }
    }

    /**
     * Partitions the list of UserGroupDTO objects for QuickSort.
     * The partitioning is done in descending order based on the score.
     *
     * @param list The list of UserGroupDTO objects to be partitioned.
     * @param low The starting index of the portion of the list to be partitioned.
     * @param high The ending index of the portion of the list to be partitioned.
     * @return The partitioning index.
     */
    private int partition(List<UserGroupDTO> list, int low, int high) {
        UserGroupDTO pivot = list.get(high);
        int i = (low - 1);

        for (int j = low; j < high; j++) {
            if (list.get(j).getScore() > pivot.getScore()) { // Sorting in descending order
                i++;

                UserGroupDTO temp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, temp);
            }
        }

        UserGroupDTO temp = list.get(i + 1);
        list.set(i + 1, list.get(high));
        list.set(high, temp);

        return i + 1;
    }
}

