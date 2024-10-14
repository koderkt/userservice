//package com.koderkt.userservice.security.services;
//
//import com.koderkt.userservice.models.User;
//import com.koderkt.userservice.repositories.UserRepository;
//import com.koderkt.userservice.security.models.CustomUserDetails;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service
//public class CustomUserDetailsService implements UserDetailsService {
//    private UserRepository userRepository;
//
//    public CustomUserDetailsService(UserRepository userRepository){
//        this.userRepository= userRepository;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Optional<User> userOptional = userRepository.findByEmail(username);
//
//        if(userOptional.isEmpty()){
//            throw new UsernameNotFoundException("User by email: " + username + " doesn't exist");
//        }
//
//        return new CustomUserDetails(userOptional.get());
//    }
//}
