package com.java.main;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;

@Data
@AllArgsConstructor
@Getter
@Setter
public class User implements Serializable {
    private String username;
    private String token;
    private String ipAddress;
    private int port;
}
