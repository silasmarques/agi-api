package br.com.silas.agiapi.clients;

import br.com.silas.agiapi.core.RequestSpecFactory;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class DogApiClient {

    private static final String ALL_BREEDS_PATH = "/breeds/list/all";
    private static final String BREED_IMAGES_PATH = "/breed/{breed}/images";
    private static final String RANDOM_IMAGE_PATH = "/breeds/image/random";

    @Step("GET /breeds/list/all - listar todas as racas")
    public Response listAllBreeds() {
        return given()
                .spec(RequestSpecFactory.defaultSpec())
            .when()
                .get(ALL_BREEDS_PATH);
    }

    @Step("GET /breed/{breed}/images - listar imagens da raca {breed}")
    public Response listBreedImages(String breed) {
        return given()
                .spec(RequestSpecFactory.defaultSpec())
                .pathParam("breed", breed)
            .when()
                .get(BREED_IMAGES_PATH);
    }

    @Step("GET /breeds/image/random - buscar imagem aleatoria")
    public Response randomImage() {
        return given()
                .spec(RequestSpecFactory.defaultSpec())
            .when()
                .get(RANDOM_IMAGE_PATH);
    }
}
