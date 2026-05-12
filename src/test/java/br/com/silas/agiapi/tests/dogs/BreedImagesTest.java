package br.com.silas.agiapi.tests.dogs;

import br.com.silas.agiapi.assertions.DogApiAssertions;
import br.com.silas.agiapi.clients.DogApiClient;
import br.com.silas.agiapi.core.RestAssuredExtension;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@Epic("Dog API")
@Feature("Imagens por raca")
@ExtendWith(RestAssuredExtension.class)
class BreedImagesTest {

    private static final String KNOWN_BREED = "hound";
    private final DogApiClient dogApiClient = new DogApiClient();

    @Test
    @Tag("smoke")
    @Tag("contract")
    @Tag("images")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Deve listar imagens de uma raca existente")
    void deveListarImagensDeUmaRacaExistente() {
        Response response = dogApiClient.listBreedImages(KNOWN_BREED);

        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/breed-images-schema.json"));

        DogApiAssertions.shouldContainBreedImages(response, KNOWN_BREED);
    }

    @ParameterizedTest(name = "raca {0} deve retornar imagens validas")
    @ValueSource(strings = {"akita", "hound", "pug", "retriever"})
    @Tag("regression")
    @Tag("images")
    @Severity(SeverityLevel.CRITICAL)
    void deveRetornarImagensValidasParaRacasConhecidas(String breed) {
        Response response = dogApiClient.listBreedImages(breed);

        DogApiAssertions.shouldReturnJson(response);
        DogApiAssertions.shouldContainBreedImages(response, breed);
    }

    @Test
    @Tag("regression")
    @Tag("images")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Deve responder imagens por raca em tempo aceitavel")
    void deveResponderImagensPorRacaEmTempoAceitavel() {
        Response response = dogApiClient.listBreedImages(KNOWN_BREED);

        DogApiAssertions.shouldRespondWithin(response, 5000);
    }

    @Test
    @Tag("negative")
    @Tag("contract")
    @Tag("images")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Deve retornar erro ao consultar raca inexistente")
    void deveRetornarErroAoConsultarRacaInexistente() {
        Response response = dogApiClient.listBreedImages("raca-inexistente");

        response.then()
                .statusCode(404)
                .body(matchesJsonSchemaInClasspath("schemas/error-schema.json"));

        DogApiAssertions.shouldReturnBreedNotFound(response);
    }
}
