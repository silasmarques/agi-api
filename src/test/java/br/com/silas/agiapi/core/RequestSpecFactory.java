package br.com.silas.agiapi.core;

import br.com.silas.agiapi.config.ConfigManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public final class RequestSpecFactory {

    private RequestSpecFactory() { }

    public static RequestSpecification defaultSpec() {
        RestAssuredConfig timeouts = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", ConfigManager.connectTimeout())
                        .setParam("http.socket.timeout", ConfigManager.readTimeout()));

        return new RequestSpecBuilder()
                .setBaseUri(ConfigManager.baseUrl())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setConfig(timeouts)
                .addFilter(new AllureRestAssured())
                .addFilter(new ErrorLoggingFilter())
                .build();
    }
}
