package br.com.silas.agiapi.assertions;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public final class DogApiAssertions {

    private static final String DOG_IMAGE_URL_REGEX =
            "^https://images\\.dog\\.ceo/breeds/.+\\.(jpg|jpeg|png|gif)$";

    private DogApiAssertions() { }

    @Step("Validar resposta de sucesso da Dog API")
    public static void shouldBeSuccessful(Response response) {
        assertThat(response.statusCode()).as("status code").isEqualTo(200);
        assertThat(response.jsonPath().getString("status")).as("status").isEqualTo("success");
    }

    @Step("Validar content-type JSON")
    public static void shouldReturnJson(Response response) {
        assertThat(response.contentType())
                .as("content-type")
                .contains("application/json");
    }

    @Step("Validar lista de racas retornada")
    public static void shouldContainBreedList(Response response) {
        shouldBeSuccessful(response);
        Map<String, List<String>> breeds = response.jsonPath().getMap("message");

        assertThat(breeds)
                .as("mapa de racas")
                .isNotNull()
                .isNotEmpty()
                .containsKeys("hound", "retriever", "terrier", "akita", "pug");

        assertThat(breeds.get("hound"))
                .as("sub-racas de hound")
                .isNotNull()
                .isNotEmpty();
    }

    @Step("Validar estrutura de racas e sub-racas")
    public static void shouldContainOnlyBreedEntriesWithSubBreedLists(Response response) {
        shouldBeSuccessful(response);
        Map<String, List<String>> breeds = response.jsonPath().getMap("message");

        assertThat(breeds).as("mapa de racas").isNotEmpty();
        assertThat(breeds.keySet())
                .as("nomes das racas")
                .allSatisfy(breed -> assertThat(breed).isNotBlank().isLowerCase());
        assertThat(breeds.values())
                .as("listas de sub-racas")
                .allSatisfy(subBreeds -> assertThat(subBreeds).isNotNull());
    }

    @Step("Validar lista de imagens da raca {breed}")
    public static void shouldContainBreedImages(Response response, String breed) {
        shouldBeSuccessful(response);
        List<String> images = response.jsonPath().getList("message");

        assertThat(images)
                .as("imagens retornadas para a raca " + breed)
                .isNotNull()
                .isNotEmpty()
                .allMatch(DogApiAssertions::isDogImageUrl);

        assertThat(images)
                .as("imagens pertencem a raca " + breed)
                .allMatch(url -> url.contains("/breeds/" + breed));
    }

    @Step("Validar imagem aleatoria")
    public static void shouldContainRandomImage(Response response) {
        shouldBeSuccessful(response);
        assertThat(response.jsonPath().getString("message"))
                .as("url da imagem aleatoria")
                .matches(DOG_IMAGE_URL_REGEX);
    }

    @Step("Validar erro de raca inexistente")
    public static void shouldReturnBreedNotFound(Response response) {
        assertThat(response.statusCode()).as("status code").isEqualTo(404);
        assertThat(response.jsonPath().getString("status")).as("status").isEqualTo("error");
        assertThat(response.jsonPath().getString("message"))
                .as("mensagem de erro")
                .containsIgnoringCase("breed not found");
        assertThat(response.jsonPath().getInt("code")).as("codigo de erro").isEqualTo(404);
    }

    private static boolean isDogImageUrl(String url) {
        return url != null && url.matches(DOG_IMAGE_URL_REGEX);
    }
}
