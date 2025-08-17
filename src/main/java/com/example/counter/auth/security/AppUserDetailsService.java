package com.example.counter.auth.security;

import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import java.util.List;
import com.example.counter.user.UserRepository;

@Service
public class AppUserDetailsService implements UserDetailsService {
  @Autowired
  private final UserRepository repo;

  @Autowired
  public AppUserDetailsService(UserRepository repo) {
    this.repo = repo;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    var u = repo.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("Not found"));
    return User
        .withUsername(u.getUsername())
        .password(u.getPassword())
        .authorities(List.of())   // <-- no roles/authorities
        .accountLocked(false).disabled(false).build();
  }
}
