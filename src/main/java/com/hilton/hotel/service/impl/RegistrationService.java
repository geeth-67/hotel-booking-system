package com.hilton.hotel.service.impl;


import com.hilton.hotel.domain.Guest;
import com.hilton.hotel.domain.UserRole;
import com.hilton.hotel.dto.request.RegisterRequest;
import com.hilton.hotel.exception.DuplicateResourceException;
import com.hilton.hotel.repository.GuestRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {

    private final GuestRepository guestRepository;

    @Value("${keycloak.admin.client-id}")
    private String clientId;


    @Value("${keycloak.admin.server-url}")
    private String serverUrl;

    @Value("${keycloak.admin.realm}")
    private String realm;

    @Value("${keycloak.admin.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    public Guest register(RegisterRequest request) {
        if (guestRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + request.getEmail());}

        if (guestRepository.existsByPhoneNo(request.getPhoneNo())) {
            throw new DuplicateResourceException("Phone number already exists: " + request.getPhoneNo());}

        String adminToken ;
        String keycloakId;

        try{
            adminToken = getAdminToken();
        } catch (Exception e){
            log.error("Error getting admin token",e);
            throw new RuntimeException("Unable to authenticate with Keycloak");
        }

        try{
            keycloakId = createKeycloakUser(adminToken,request);
        } catch (HttpClientErrorException.Conflict e){
            log.error( e.getMessage());
            throw new DuplicateResourceException("User already exists");
        }

        try{
            assignRealmRoles(adminToken, keycloakId, "GUEST");
        }catch (Exception e){
            log.warn("Could not assign role to keycloakid - {}",keycloakId);
        }

        Guest guest;
        try {
            guest = guestRepository.save(Guest.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .phoneNo(request.getPhoneNo())
                    .keycloakId(keycloakId)
                    .role(UserRole.GUEST)
                    .build());
        } catch (Exception e) {
            log.error("Database save failed, rolling back Keycloak user {}", keycloakId, e);
            deleteKeycloakUser(adminToken, keycloakId);
            throw new RuntimeException("Registration failed, please try again");
        }

        log.info("New Guest created: {}", request.getLastName());

        return guest;

    }

    private void deleteKeycloakUser(String adminToken, String keycloakId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(adminToken);
            restTemplate.exchange(
                    serverUrl + "/admin/realms/" + realm + "/users/" + keycloakId,
                    HttpMethod.DELETE,
                    new HttpEntity<>(headers),
                    Void.class
            );
        } catch (Exception e) {
            log.error("Failed to roll back Keycloak user {}. Manual cleanup required.", keycloakId, e);
        }
    }

    private void assignRealmRoles(String adminToken, String keycloakId, String roleName){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<Map> roleResp = restTemplate.exchange(
                serverUrl + "/admin/realms/" + realm + "/roles/" + roleName,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
        );

        if (roleResp.getBody() != null) {

            restTemplate.postForEntity(
                    serverUrl + "/admin/realms/" + realm + "/users/" + keycloakId + "/role-mappings/realm",
                    new HttpEntity<>(List.of(roleResp.getBody()), headers),
                    Void.class
            );
        }
    }

    private String createKeycloakUser(String adminToken, RegisterRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String,Object> user = Map.of(
                "username", request.getEmail(),
                "email", request.getEmail(),
                "firstName", request.getFirstName(),
                "lastName", request.getLastName(),
                "enabled", true,
                "credentials",  List.of(Map.of(
                        "type", "password",
                        "value", request.getPassword(),
                        "temporary", false
                ))
        );

        ResponseEntity<Void> response = restTemplate.postForEntity(
                serverUrl + "/admin/realms/" + realm + "/users",
                new HttpEntity<>(user, headers),
                Void.class
        );

        String location = response.getHeaders().getFirst(HttpHeaders.LOCATION);

        if(location==null){
            throw new IllegalStateException("Keycloak did not return location header");
        }

        return location.substring(location.lastIndexOf("/")+1);
    }


    private String getAdminToken(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body= new LinkedMultiValueMap<>();
        body.add("grant_type","client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                serverUrl + "/realms/" + realm + "/protocol/openid-connect/token",
                new HttpEntity<>(body,headers), Map.class
        );

        return (String) response.getBody().get("access_token");
    }

}