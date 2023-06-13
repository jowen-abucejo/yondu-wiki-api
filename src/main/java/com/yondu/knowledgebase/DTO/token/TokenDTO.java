package com.yondu.knowledgebase.DTO.token;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TokenDTO {

    public record Base(String token, LocalDateTime issued, LocalDateTime expiration) {}
}
