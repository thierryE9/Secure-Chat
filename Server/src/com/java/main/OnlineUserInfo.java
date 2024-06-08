package com.java.main;

import lombok.*;

import java.security.PublicKey;

@Data
@AllArgsConstructor
@Getter
@Setter
public class OnlineUserInfo {

    private String token;
    private String publicKey;
    private String ipAddress;
    private int port;

}
