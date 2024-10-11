package com.koderkt.userservice.models;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MappedSuperclass
@Getter
@Setter
public class BaseModel {
    @Id
    private Long id;
}
