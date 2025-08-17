package com.example.counter.auth.security;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserContext {
    private Long userId;
    private String username;
    
    public static UserContext of(Long userId, String username) {
        return new UserContext(userId, username);
    }
}
