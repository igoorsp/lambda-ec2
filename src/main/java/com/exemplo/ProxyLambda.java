package com.exemplo;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.exemplo.client.VpcEndpointClient;
import com.exemplo.exception.HttpRequestFailedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@ApplicationScoped
public class ProxyLambda implements RequestHandler<Map<String, Object>, String> {

    private final VpcEndpointClient vpcEndpointClient;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Inject
    public ProxyLambda(@RestClient VpcEndpointClient vpcEndpointClient) {
        this.vpcEndpointClient = vpcEndpointClient;
    }


    private static final Logger LOG = LoggerFactory.getLogger(ProxyLambda.class);

    @Override
    public String handleRequest(Map<String, Object> input, Context context) {
        LOG.info("Before call vpcEndpointClient");
        LOG.info("Input {}", input);

        try (Response response = vpcEndpointClient.callVpcEndpoint(input)) {
            String inputJson = objectMapper.writeValueAsString(input);

            if (response.getStatus() == 200) {
                LOG.info("StatusCode is 200");
                return response.readEntity(String.class);
            } else {
                LOG.info("StatusCode is: {}", response.getStatus());
                throw new HttpRequestFailedException(
                        "HTTP call failed: " + response.getStatus(),
                        response.getStatus()
                );
            }
        } catch (WebApplicationException e) {
            String errorMessage = "HTTP call failed: " + e.getResponse().getStatus();
            throw new HttpRequestFailedException(errorMessage, e.getResponse().getStatus());
        } catch (Exception e) {
            LOG.error("Unexpected error processing Lambda request", e);
            throw new HttpRequestFailedException("Unexpected error", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }
    }
}