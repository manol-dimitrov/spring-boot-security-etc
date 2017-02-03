package com.zamrad;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.zamrad.dto.profiles.NewProfileDto;
import com.zamrad.util.JWTTokenGenerator;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.assertThat;

class BaseIT {

    private final static String PROFILES_PATH = "http://localhost:8080/profiles/v1/";
    private String testToken = JWTTokenGenerator.generateToken();

    BaseIT() throws UnsupportedEncodingException {
    }

    @Before
    public void setUp() throws Exception {

        Unirest.setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                    = new com.fasterxml.jackson.databind.ObjectMapper();

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Unirest.setDefaultHeader("Content-Type", "application/json");
        Unirest.setDefaultHeader("Accept", "application/json");
        Unirest.setDefaultHeader("Authorization", "Bearer " + testToken);
    }

    @After
    public void tearDown() {

    }

    void doDeleteAllProfiles() {
        //HttpResponse<JsonNode> jsonResponse = Unirest.delete(PROFILES_PATH + "{id}").routeParam("id", profileId).asJson();
    }

    void doDeleteAllEvents(){
        //HttpResponse<JsonNode> jsonResponse = Unirest.delete(PROFILES_PATH + "{id}").routeParam("id", profileId).asJson();
    }

    String doPostNewArtist() throws UnirestException {
        NewProfileDto newProfileDto = createProfileDto("artist");

        HttpResponse<JsonNode> jsonResponse = Unirest.post(PROFILES_PATH)
                .body(newProfileDto)
                .asJson();

        assertThat(jsonResponse.getStatus()).isEqualTo(201);
        assertThat(jsonResponse.getStatusText()).isEqualTo("Created");
        assertThat(jsonResponse.getHeaders().get("Location")).isNotNull();

        return jsonResponse.getHeaders().get("Location").get(0);
    }

    String doPostNewBooker() throws UnirestException {
        NewProfileDto newProfileDto = createProfileDto("booker");

        HttpResponse<JsonNode> jsonResponse = Unirest.post(PROFILES_PATH)
                .body(newProfileDto)
                .asJson();

        assertThat(jsonResponse.getStatus()).isEqualTo(201);
        assertThat(jsonResponse.getStatusText()).isEqualTo("Created");
        assertThat(jsonResponse.getHeaders().get("Location")).isNotNull();

        return jsonResponse.getHeaders().get("Location").get(0);
    }

    private NewProfileDto createProfileDto(String profileType) {
        NewProfileDto newProfileDto = new NewProfileDto();
        newProfileDto.setFirstName("Justin");
        newProfileDto.setSecondName("Timberlake");
        newProfileDto.setGender("male");
        newProfileDto.setFacebookId(1392995950728274L);
        newProfileDto.setPhotoUrl("http://photo.com");
        newProfileDto.setProfileType(profileType);

        return newProfileDto;
    }
}
