package br.com.silas.agiapi.core;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class RestAssuredExtension implements BeforeAllCallback {

    private static volatile boolean initialized = false;

    @Override
    public void beforeAll(ExtensionContext context) {
        if (initialized) return;
        synchronized (RestAssuredExtension.class) {
            if (initialized) return;

            RestAssured.defaultParser = Parser.JSON;
            RestAssured.useRelaxedHTTPSValidation();

            initialized = true;
        }
    }
}
