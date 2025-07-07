package com.example.counter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.*;

@RestController
@CrossOrigin(origins = "*") // Allow frontend access
public class VisitController {

    Set<String> uniqueNames = new HashSet<>();
    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/visit")
    public ResponseEntity<Void> addVisit() {
        visitRepository.logVisit();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/createUser")
    public ResponseEntity<Void> createUser() {
        visitRepository.logVisit();
        return ResponseEntity.ok().build();
    }


    // signup endpoint
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody Map<String, String> payload) {
        String username = payload.get("username").toLowerCase();
        String password = payload.get("password");
        return userRepository.findByUsername(username)
                .map(user1 -> {
                    uniqueNames.add(username);
                    return ResponseEntity.ok("User already exists");
                })
                .orElseGet(() -> {
                    User newUser = new User(username, password);
                    userRepository.save(newUser);
                    return ResponseEntity.ok("User created");
                });
    }

    @PostMapping("/login")
    public ResponseEntity<Boolean> login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username").toLowerCase();
        String password = payload.get("password");
        return userRepository.findByUsername(username)
                .map(user -> {
                    return ResponseEntity.ok(user.getPassword().equals(password));
                })
                .orElseGet(() -> {
                    return ResponseEntity.ok(false);
                });
    }

    @GetMapping("/count")
    public Map<String, Integer> getCount() {
        int count = visitRepository.getVisitCount();
        return Collections.singletonMap("count", count);
    }

    @GetMapping("/user-exists")
    public ResponseEntity<Boolean> userExists(@RequestParam String username) {
        long startTime = System.currentTimeMillis();
        boolean exists = uniqueNames.contains(username.toLowerCase());
        long endTime = System.currentTimeMillis();
        System.out.println("query time for map lookup: " + (endTime - startTime) + " ms");
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/allUsers")
    public List<User> listUsers() {
        long startTime = System.currentTimeMillis();
        List<User> users =  userRepository.findAll();
        long endTime = System.currentTimeMillis();
        System.out.println("DB query time for fetching all users: " + (endTime - startTime) + " ms");
        return users;
    }
}