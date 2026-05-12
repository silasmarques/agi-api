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

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@Epic("Dog API")
@Feature("Imagem aleatoria")
@ExtendWith(RestAssuredExtension.class)
class RandomImageTest {

    private final DogApiClient dogApiClient = new DogApiClient();

    @Test
    @Tag("smoke")
    @Tag("contract")
    @Tag("images")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Deve retornar uma imagem aleatoria com contrato valido")
    void deveRetornarUmaImagemAleatoriaComContratoValido() {
        Response response = dogApiClient.randomImage();

        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/random-image-schema.json"));

        DogApiAssertions.shouldContainRandomImage(response);
    }

    @Test
    @Tag("regression")
    @Tag("images")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Deve retornar imagem aleatoria como JSON em tempo aceitavel")
    void deveRetornarImagemAleatoriaComoJsonEmTempoAceitavel() {
        Response response = dogApiClient.randomImage();

        DogApiAssertions.shouldReturnJson(response);
        DogApiAssertions.shouldRespondWithin(response, 5000);
        DogApiAssertions.shouldContainRandomImage(response);
    }
}
