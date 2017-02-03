package com.zamrad;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.zamrad.dto.profiles.GenreUpdateDto;
import com.zamrad.dto.profiles.UpdateProfileDto;
import com.zamrad.util.JWTTokenGenerator;
import org.junit.After;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.assertThat;

public class ProfilesIT extends BaseIT {
    private final static String PROFILES_PATH = "http://localhost:8080/profiles/v1/";
    private final static String PROFILES_PATH_ME = "http://localhost:8080/profiles/v1/me";
    private String testToken = JWTTokenGenerator.generateToken();

    public ProfilesIT() throws UnsupportedEncodingException {
    }

    @After
    public void tearDown() {

    }

    @Test
    public void shouldGetArtistById() throws UnirestException {
        final String newlyPostedArtistId = doPostNewArtist();
        final String newlyInsertedArtistId = newlyPostedArtistId.substring(33);

        HttpResponse<JsonNode> jsonResponse = Unirest.get(PROFILES_PATH + "{id}").routeParam("id", newlyInsertedArtistId).asJson();

        assertThat(jsonResponse.getStatus()).isEqualTo(200);
        assertThat(jsonResponse.getBody().getObject().get("first_name")).isEqualTo("Justin");
    }

    @Test
    public void shouldReturnNotFoundIfArtistIdDoesNotExist() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse = Unirest.get(PROFILES_PATH + "{id}").routeParam("id", "c8f4e3f2-0c5c-4a05-ba0b-e9dc0e4392b1").asJson();
        assertThat(jsonResponse.getStatus()).isEqualTo(404);
    }

    @Test
    public void shouldCreateArtist() throws UnirestException {
        doPostNewArtist();
    }

    @Test
    public void shouldDeleteArtist() throws UnirestException {
        final String id = doPostNewArtist().substring(34);

        HttpResponse<JsonNode> jsonResponse = Unirest.delete(PROFILES_PATH + "{id}").routeParam("id", id).asJson();

        assertThat(jsonResponse.getStatus()).isEqualTo(204);

    }

    @Test
    public void shouldUpdateArtist() throws UnirestException {
        UpdateProfileDto updateProfileDto = createUpdateArtistDto();

        HttpResponse<JsonNode> jsonResponse = Unirest.patch(PROFILES_PATH_ME)
                .body(updateProfileDto)
                .asJson();

        assertThat(jsonResponse.getStatus()).isEqualTo(200);
    }

    @Test
    public void shouldReturn404WhenUserBeingUpdatedIsNotRegistered() throws UnirestException {
        UpdateProfileDto updateProfileDto = createUpdateArtistDto();

        HttpResponse<JsonNode> jsonResponse = Unirest.patch(PROFILES_PATH_ME)
                .body(updateProfileDto)
                .asJson();

        assertThat(jsonResponse.getStatus()).isEqualTo(404);
    }

    @Test
    public void shouldGetMyProfile() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse = Unirest.get(PROFILES_PATH_ME).asJson();

        assertThat(jsonResponse.getStatus()).isEqualTo(200);
    }

    @Test
    public void shouldReturn404WhenRequestingMyProfileIfNotRegistered() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse = Unirest.get(PROFILES_PATH_ME).asJson();

        assertThat(jsonResponse.getStatus()).isEqualTo(404);
    }

    @Test
    public void shouldAddArtistGenre() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse = addArtistGenre("dnb");
        assertThat(jsonResponse.getStatus()).isEqualTo(200);
    }

    @Test
    public void shouldUpdateArtistGenre() throws UnirestException {
        //addArtistGenre("pop");
        //addArtistGenre("dance");
        //addArtistGenre("rock");

        UpdateProfileDto updateProfileDto = createUpdateArtistDtoWithGenre("rock", "test");

        HttpResponse<JsonNode> jsonResponse = Unirest.patch(PROFILES_PATH_ME)
                .body(updateProfileDto)
                .asJson();

        assertThat(jsonResponse.getStatus()).isEqualTo(200);
    }

    @Test
    public void shouldDeleteArtistGenre() throws UnirestException {
        UpdateProfileDto updateProfileDto = createUpdateArtistDtoWithGenreDeletion();

        HttpResponse<JsonNode> jsonResponse = Unirest.patch(PROFILES_PATH_ME)
                .body(updateProfileDto)
                .asJson();

        assertThat(jsonResponse.getStatus()).isEqualTo(200);
        assertThat(jsonResponse.getBody().getObject().getJSONArray("genres")).isEqualTo(null);
    }

    private HttpResponse<JsonNode> addArtistGenre(String genre) throws UnirestException {
        UpdateProfileDto updateProfileDto = createUpdateArtistDtoWithGenre(null, genre);

        HttpResponse<JsonNode> jsonResponse = Unirest.patch(PROFILES_PATH_ME)
                .body(updateProfileDto)
                .asJson();

        assertThat(jsonResponse.getStatus()).isEqualTo(200);
        return jsonResponse;
    }

    private UpdateProfileDto createUpdateArtistDtoWithGenreDeletion() {
        UpdateProfileDto updateProfileDto = new UpdateProfileDto();
        updateProfileDto.setEmail("updated-email@email.com");
        updateProfileDto.setDescription("I'm a bloody rockstar!!");

        GenreUpdateDto genreUpdateDto = new GenreUpdateDto();
        genreUpdateDto.setCurrentGenre("pop");
        genreUpdateDto.setNewGenre("none");

        updateProfileDto.setGenreUpdateDto(genreUpdateDto);

        return updateProfileDto;
    }

    private UpdateProfileDto createUpdateArtistDto() {
        UpdateProfileDto updateProfileDto = new UpdateProfileDto();
        updateProfileDto.setEmail("updated-email@email.com");
        updateProfileDto.setDescription("I'm a bloody rockstar!!");

        return updateProfileDto;
    }

    private UpdateProfileDto createUpdateArtistDtoWithGenre(String currentGenre, String newGenre) {
        UpdateProfileDto updateProfileDto = new UpdateProfileDto();
        updateProfileDto.setEmail("updated-email@email.com");
        updateProfileDto.setDescription("I'm a bloody rockstar!!");

        GenreUpdateDto genreUpdateDto = new GenreUpdateDto();
        genreUpdateDto.setCurrentGenre(currentGenre);
        genreUpdateDto.setNewGenre(newGenre);

        updateProfileDto.setGenreUpdateDto(genreUpdateDto);

        return updateProfileDto;
    }
}
