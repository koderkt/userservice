package com.koderkt.userservice.controllers;

import com.koderkt.userservice.dtos.LoginRequestDto;
import com.koderkt.userservice.dtos.LogoutRequestDto;
import com.koderkt.userservice.dtos.SignUpRequestDto;
import com.koderkt.userservice.dtos.UserDto;
import com.koderkt.userservice.models.Token;
import com.koderkt.userservice.models.User;
import com.koderkt.userservice.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String hello(){
        return "Hello toxic";
    }
    @PostMapping("/signup")
    public UserDto signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        return UserDto.from(userService.signUp(
                signUpRequestDto.getName(),
                signUpRequestDto.getEmail(),
                signUpRequestDto.getPassword()
        ));
    }

    @PostMapping("/login")
    public Token login(@RequestBody LoginRequestDto loginRequestDto) {
        return userService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDto logoutRequestDto) {

        userService.logout(logoutRequestDto.getToken());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/validate/{token}")
    public UserDto validateToken(@PathVariable("token") String value) {
        return UserDto.from(userService.validateToken(value));
    }
}
