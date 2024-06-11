package com.dreamgames.backendengineeringcasestudy.controller;

import com.dreamgames.backendengineeringcasestudy.DTO.GroupLeaderboardDTO;
import com.dreamgames.backendengineeringcasestudy.DTO.TournamentGroupDTO;
import com.dreamgames.backendengineeringcasestudy.entity.Tournament;

import com.dreamgames.backendengineeringcasestudy.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tournament")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    /**
     * Creates a new tournament with the specified name.
     *
     * @param tournamentName The name of the new tournament.
     * @return ResponseEntity containing the created Tournament entity.
     */
    @PostMapping("/createTournament/{tournamentName}")
    public ResponseEntity<Tournament> createTournament(@PathVariable String tournamentName) {
        Tournament tournament = tournamentService.createNewTournament(tournamentName);
        System.out.println("Tournament " + tournamentName + " started");
        return ResponseEntity.ok(tournament);
    }

    /**
     * Ends the current active tournament.
     *
     * @return ResponseEntity containing a message indicating the tournament has ended.
     */
    @PostMapping("/end")
    public ResponseEntity<String> endTournament() {
        tournamentService.endCurrentTournament();
        return ResponseEntity.ok("Tournament ended");
    }

    /**
     * Allows a user to enter the current tournament.
     *
     * @param userId The ID of the user entering the tournament.
     * @return ResponseEntity containing a success or failure message.
     */
    @PostMapping("/enterTournament/{userId}")
    public ResponseEntity<String> enterTournament(@PathVariable String userId) {
        boolean success = tournamentService.enterTournament(userId);
        if (success) {
            return ResponseEntity.ok("User entered tournament successfully.");
        }
        return ResponseEntity.badRequest().body("User could not enter tournament.");
    }

    /**
     * Checks if there is any active tournament.
     *
     * @return ResponseEntity containing the name of the active tournament or a message indicating no active tournament.
     */
    @GetMapping("/activeTournament")
    public ResponseEntity<String> isThereAnyTournament() {
        Tournament tournament = tournamentService.getCurrentTournament();
        if (tournament != null) {
            return ResponseEntity.ok(tournament.getTournamentName());
        }
        return ResponseEntity.badRequest().body("No active tournament");
    }

    /**
     * Retrieves the active tournament groups along with their users.
     *
     * @return ResponseEntity containing a list of TournamentGroupDTO objects.
     */
    @GetMapping("/getGroups")
    public ResponseEntity<List<TournamentGroupDTO>> getActiveTournamentGroupsWithUsers() {
        List<TournamentGroupDTO> groups = tournamentService.getActiveTournamentGroupsWithUsers();
        return ResponseEntity.ok(groups);
    }

    /**
     * Completes a level for the specified user.
     *
     * @param userId The ID of the user completing the level.
     * @return ResponseEntity containing a message indicating the level was completed successfully.
     */
    @PostMapping("/completeLevel/{userId}")
    public ResponseEntity<String> completeLevel(@PathVariable String userId) {
        tournamentService.completeLevel(userId);
        return ResponseEntity.ok("Level completed successfully.");
    }

    /**
     * Allows a user to claim their reward after a tournament has ended.
     *
     * @param userId The ID of the user claiming the reward.
     * @return ResponseEntity containing a success or failure message.
     */
    @PostMapping("/claimReward/{userId}")
    public ResponseEntity<String> claimReward(@PathVariable String userId) {
        boolean success = tournamentService.claimReward(userId);
        if (success) {
            return ResponseEntity.ok("Reward claimed successfully.");
        }
        return ResponseEntity.badRequest().body("Reward can not be claimed");
    }

    /**
     * Retrieves the leaderboard for a specific group in the current tournament.
     *
     * @param groupId The ID of the group for which to fetch the leaderboard.
     * @return ResponseEntity containing a list of GroupLeaderboardDTO objects.
     */
    @GetMapping("/groupLeaderboard/{groupId}")
    public ResponseEntity<List<GroupLeaderboardDTO>> getGroupLeaderboard(@PathVariable Long groupId) {
        List<GroupLeaderboardDTO> leaderboard = tournamentService.getGroupLeaderboard(groupId);
        return ResponseEntity.ok(leaderboard);
    }

    /**
     * Retrieves the leaderboard of countries based on the scores of users in the current tournament.
     *
     * @return ResponseEntity containing a map where the key is the country name and the value is the total score of users from that country.
     */
    @GetMapping("/countryLeaderboard")
    public ResponseEntity<Map<String, Integer>> getCountryLeaderboard() {
        Map<String, Integer> leaderboard = tournamentService.getCountryLeaderboard();
        return ResponseEntity.ok(leaderboard);
    }

    /**
     * Retrieves the rank of a specific user in the current tournament.
     *
     * @param userId The ID of the user whose rank is to be fetched.
     * @return ResponseEntity containing the rank of the user in the tournament.
     */
    @GetMapping("/userRank/{userId}/{tournamentId}")
    public ResponseEntity<Integer> getUserRank(@PathVariable String userId, @PathVariable Long tournamentId) {
        int rank = tournamentService.getUserRank(userId, tournamentId);
        return ResponseEntity.ok(rank);
    }
}