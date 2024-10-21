package com.koderkt.userservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.koderkt.userservice.dtos.SendEmailEventDto;
import com.koderkt.userservice.models.Token;
import com.koderkt.userservice.models.User;
import com.koderkt.userservice.repositories.TokenRepository;
import com.koderkt.userservice.repositories.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.nio.file.OpenOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private TokenRepository tokenRepository;
    private KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper;
    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
                       TokenRepository tokenRepository, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenRepository = tokenRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public User signUp(String fullName, String email, String password) {
        User user = new User();
        user.setName(fullName);
        user.setEmail(email);
        user.setHashedPassword(bCryptPasswordEncoder.encode(password));
        User savedUser = userRepository.save(user);

        SendEmailEventDto sendEmailEventDto = new SendEmailEventDto();
        sendEmailEventDto.setTo(savedUser.getEmail());
        sendEmailEventDto.setFrom("");
        sendEmailEventDto.setSubject("user created successful");
        sendEmailEventDto.setBody("Thanks for signing up...");

        try {
            kafkaTemplate.send(
                "sendEmail",
                    objectMapper.writeValueAsString(sendEmailEventDto));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return user;
    }

    public Token login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
//            TODO: User not exist exception
            return null;
        }
        User user = userOptional.get();
        if (!bCryptPasswordEncoder.matches(password, user.getHashedPassword())) {
//            TODO: Password  not matching exist exception
            return null;
        }

        LocalDateTime currentDateTime = LocalDateTime.now();

        LocalDateTime expiryDateTime = currentDateTime.plusDays(30);
        Date expiryDate = Date.from(expiryDateTime.atZone(ZoneId.systemDefault()).toInstant());

        Token token = new Token();
        token.setUser(user);
        token.setExpiryAt(expiryDate);

        token.setValue(RandomStringUtils.randomAlphanumeric(128));
        Token savedToken = tokenRepository.save(token);
        return savedToken;
    }


    public void logout(String token) {
        Optional<Token> tokenOptional = tokenRepository.findByValueAndIsDeleted(token, false);
        if (tokenOptional.isEmpty()) {
//            TODO: TokenDoesNotExistOrAlreadyExpiredException
            return;
        }
        Token token1 = tokenOptional.get();

        token1.setDeleted(true);
        tokenRepository.save(token1);
        return;
    }

    public User validateToken(String value) {
        Optional<Token> tokenOptional = tokenRepository.
                findByValueAndIsDeletedAndExpiryAtGreaterThan(value, false, new Date());

        if (tokenOptional.isEmpty()) {
//            TODO: TokenDoesNotExistOrAlreadyExpiredException
            return null;
        }

        return tokenOptional.get().getUser();
    }
}
