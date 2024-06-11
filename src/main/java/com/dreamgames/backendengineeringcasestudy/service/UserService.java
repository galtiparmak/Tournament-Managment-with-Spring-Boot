package com.dreamgames.backendengineeringcasestudy.service;

import com.dreamgames.backendengineeringcasestudy.entity.User;
import com.dreamgames.backendengineeringcasestudy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * Service class for managing User entities.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    private final String[] countries = {"Turkey", "United States", "United Kingdom", "France", "Germany"};
    private final Random random = new Random();

    /**
     * Creates a new user with the specified username.
     *
     * @param userName The username of the new user.
     * @return The created User entity.
     */
    public User createUser(String userName) {
        User user = new User();
        user.setUserId(generateUniqueUserId());
        user.setUserName(userName);
        user.setLevel(1);
        user.setCoins(5000);
        user.setCountry(countries[random.nextInt(countries.length)]);
        user.setInTournament(false);
        user.setRewardClaimed(true);
        return repository.save(user);
    }

    /**
     * Retrieves a user by their user ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return The User entity with the specified ID, or null if no such user exists.
     */
    public User getUser(String userId) {
        return repository.findByUserId(userId);
    }

    /**
     * Retrieves all users.
     *
     * @return A list of all User entities.
     */
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    /**
     * Updates the level and coins of the user with the specified ID.
     *
     * @param userId The ID of the user to update.
     * @return The updated User entity, or null if no such user exists.
     */
    public User updateLevel(String userId) {
        User user = repository.findByUserId(userId);
        if (user != null) {
            user.setLevel(user.getLevel() + 1);
            user.setCoins(user.getCoins() + 25);
            return repository.save(user);
        }
        return null;
    }

    /**
     * Generates a unique user ID.
     *
     * @return A unique user ID.
     */
    private String generateUniqueUserId() {
        // Implement a method to generate a unique user ID
        return "USER" + System.currentTimeMillis();
    }

    /**
     * For testing purposes only. Updates all users to level 25 with 5000 coins.
     */
    public void test() {
        List<User> allUsers = repository.findAll();
        for (User user : allUsers) {
            user.setLevel(25);
            user.setCoins(5000);
            repository.save(user);
        }
        System.out.println("All the users are updated");
    }

}

