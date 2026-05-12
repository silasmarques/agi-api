package br.com.silas.agiapi.tests.dogs;

import br.com.silas.agiapi.assertions.DogApiAssertions;
import br.com.silas.agiapi.clients.DogApiClient;
import br.com.silas.agiapi.core.RestAssuredExtension;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@Epic("Dog API")
@Feature("Listagem de racas")
@ExtendWith(RestAssuredExtension.class)
class BreedListTest {

    private final DogApiClient dogApiClient = new DogApiClient();

    @Test
    @Tag("smoke")
    @Tag("contract")
    @Tag("breeds")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Deve listar todas as racas com contrato valido")
    void deveListarTodasAsRacasComContratoValido() {
        var response = dogApiClient.listAllBreeds();

        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/breed-list-schema.json"));

        DogApiAssertions.shouldReturnJson(response);
    }

    @Test
    @Tag("regression")
    @Tag("breeds")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Deve retornar racas conhecidas e sub-racas")
    void deveRetornarRacasConhecidasESubRacas() {
        DogApiAssertions.shouldContainBreedList(dogApiClient.listAllBreeds());
    }

    @Test
    @Tag("regression")
    @Tag("breeds")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Deve retornar racas em formato de mapa com listas de sub-racas")
    void deveRetornarRacasEmFormatoDeMapaComListasDeSubRacas() {
        DogApiAssertions.shouldContainOnlyBreedEntriesWithSubBreedLists(dogApiClient.listAllBreeds());
    }
}
