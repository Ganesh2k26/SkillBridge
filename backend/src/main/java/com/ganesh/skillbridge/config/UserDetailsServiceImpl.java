package com.ganesh.skillbridge.config;

import com.ganesh.skillbridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Dedicated UserDetailsService — completely separate from AuthService.
 * This breaks the circular dependency:
 *   AuthService no longer implements UserDetailsService,
 *   so SecurityConfig/JwtFilter don't depend on AuthService at all.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmailIgnoreCase(email.trim().toLowerCase())
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}
