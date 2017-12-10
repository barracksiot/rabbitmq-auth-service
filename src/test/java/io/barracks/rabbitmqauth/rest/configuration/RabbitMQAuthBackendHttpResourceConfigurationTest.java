/*
 * MIT License
 *
 * Copyright (c) 2017 Barracks Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.barracks.rabbitmqauth.rest.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.barracks.commons.util.Endpoint;
import io.barracks.rabbitmqauth.model.ResourceCheck;
import io.barracks.rabbitmqauth.rest.RabbitMQAuthBackendHttpResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@BarracksResourceTest(controllers = RabbitMQAuthBackendHttpResource.class, outputDir = "build/generated-snippets/auth")
public class RabbitMQAuthBackendHttpResourceConfigurationTest {

    private static final String baseUrl = "https://not.barracks.io";

    private static final Endpoint CHECK_RESOURCE_ENDPOINT = Endpoint.from(HttpMethod.POST, "/auth/resource?username={username}&vhost={vhost}&resource={resource}&name={name}&permission={permission}");

    @MockBean
    private RabbitMQAuthBackendHttpResource resource;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Value("classpath:io/barracks/rabbitmqauth/rest/resourceCheck.json")
    private Resource resourceCheck;

    @Test
    public void documentResource() throws Exception {
        // Given
        final Endpoint endpoint = CHECK_RESOURCE_ENDPOINT;
        final ResourceCheck request = ResourceCheck.builder()
                .username("coucou")
                .vhost("/")
                .resource("topic")
                .name("resourceName")
                .permission("write")
                .build();

        final String response = "allow";
        doReturn(response).when(resource).resource(refEq(request));

        // When
        final ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders
                        .request(
                                endpoint.getMethod(),
                                baseUrl + endpoint.getPath(),
                                request.getUsername(),
                                request.getVhost(),
                                request.getResource(),
                                request.getName(),
                                request.getPermission()
                        )
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // Then
        verify(resource).resource(refEq(request));
        result.andExpect(status().isOk())
                .andDo(
                        document(
                                "check",
                                requestParameters(
                                        parameterWithName("name").description("The name of the resource we want to check"),
                                        parameterWithName("resource").description("The type of resource considered (generally a queue or a topic)"),
                                        parameterWithName("permission").description("The permissions for this user"),
                                        parameterWithName("vhost").description("The host on which the resource considered is."),
                                        parameterWithName("username").description("The name of the user")
                                )

                        )
                );
    }

    @Test
    public void resource_withValidResourceCheck_shouldCallResourceAndReturnValue() throws Exception {
        // Given
        final Endpoint endpoint = CHECK_RESOURCE_ENDPOINT;
        final ResourceCheck request = ResourceCheck.builder()
                .username("coucou")
                .vhost("/")
                .resource("coucou")
                .name("resourceName")
                .permission("write")
                .build();

        final String response = "allow";
        doReturn(response).when(resource).resource(refEq(request));

        // When
        final ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders
                        .request(
                                endpoint.getMethod(),
                                baseUrl + endpoint.getPath(),
                                request.getUsername(),
                                request.getVhost(),
                                request.getResource(),
                                request.getName(),
                                request.getPermission()
                        )
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // Then
        verify(resource).resource(refEq(request));
        result.andExpect(status().isOk())
                .andExpect(content().string(response));
    }

}
