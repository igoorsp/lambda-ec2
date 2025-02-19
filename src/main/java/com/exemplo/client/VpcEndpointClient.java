package com.exemplo.client;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.Map;

@Path("/")
@RegisterRestClient(configKey = "vpc-endpoint-client")
public interface VpcEndpointClient {

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response callVpcEndpoint(Map<String, Object> input);
}