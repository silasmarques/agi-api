package br.com.silas.agiapi.core;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

public final class ResponseSpecFactory {

    private ResponseSpecFactory() { }

    public static ResponseSpecification ok()           { return expect(200); }
    public static ResponseSpecification created()      { return expect(201); }
    public static ResponseSpecification noContent()    { return expect(204); }
    public static ResponseSpecification badRequest()   { return expect(400); }
    public static ResponseSpecification unauthorized() { return expect(401); }
    public static ResponseSpecification notFound()     { return expect(404); }

    private static ResponseSpecification expect(int status) {
        return new ResponseSpecBuilder().expectStatusCode(status).build();
    }
}
