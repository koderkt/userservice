package com.koderkt.userservice.dtos;

import com.koderkt.userservice.models.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendEmailEventDto {
    private String to;
    private String from;
    private String subject;
    private String body;


}
