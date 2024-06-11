package com.dreamgames.backendengineeringcasestudy.controller;

import com.dreamgames.backendengineeringcasestudy.entity.User;
import com.dreamgames.backendengineeringcasestudy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService service;

    /**
     * Creates a new user with the specified username.
     *
     * @param userName The username of the new user.
     * @return ResponseEntity containing the created User entity.
     */
    @PostMapping("/createUser/{userName}")
    public ResponseEntity<User> createUser(@PathVariable String userName) {
        User user = service.createUser(userName);
        return ResponseEntity.ok(user);
    }

    /**
     * Retrieves a user by their user ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return ResponseEntity containing the User entity if found, or a 404 status if not found.
     */
    @GetMapping("/getUser/{userId}")
    public ResponseEntity<User> getUser(@PathVariable String userId) {
        User user = service.getUser(userId);
        if (user != null) {
            System.out.println(user.getUserName());
            return ResponseEntity.ok(user);
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retrieves all users.
     *
     * @return ResponseEntity containing a list of all User entities.
     */
    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = service.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Updates the level of the user with the specified ID.
     *
     * @param userId The ID of the user to update.
     * @return ResponseEntity containing the updated User entity if found, or a 404 status if not found.
     */
    @PostMapping("/updateUser/{userId}")
    public ResponseEntity<User> updateLevel(@PathVariable String userId) {
        User updatedUser = service.updateLevel(userId);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * For testing purposes only. Updates all users to a specific state.
     *
     * @return ResponseEntity containing a message indicating that all users have been updated.
     */
    @PostMapping("/test")
    public ResponseEntity<String> updateUsers() {
        service.test();
        return ResponseEntity.ok("All the users are updated");
    }
}

