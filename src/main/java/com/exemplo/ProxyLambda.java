package com.exemplo;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

public class ProxyLambda implements RequestHandler<JsonObject, JsonObject> {

    @Inject
    Client client;

    @Override
    public JsonObject handleRequest(JsonObject input, Context context) {
        String vpcEndpoint = System.getenv("VPC_ENDPOINT");

        try (Response response = client.target(vpcEndpoint)
                .request()
                .post(Entity.json(input))) {

            if (response.getStatus() == 200) {
                return response.readEntity(JsonObject.class);
            } else {
                throw new HttpRequestFailedException(
                        "HTTP call failed: " + response.getStatus(),
                        response.getStatus()
                );
            }
        }
    }
}