package com.ecomweb.identity_service.controller;

import com.ecomweb.identity_service.dto.request.AuthenticationRequest;
import com.ecomweb.identity_service.dto.request.IntrospectRequest;
import com.ecomweb.identity_service.dto.request.LogoutRequest;
import com.ecomweb.identity_service.dto.request.RefreshRequest;
import com.ecomweb.identity_service.dto.response.ApiResponse;
import com.ecomweb.identity_service.dto.response.AuthenticationResponse;
import com.ecomweb.identity_service.dto.response.IntrospectResponse;
import com.ecomweb.identity_service.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController2 {
    AuthenticationService authenticationService;

    @GetMapping("/test")
    void authenticate() {

    }

}
