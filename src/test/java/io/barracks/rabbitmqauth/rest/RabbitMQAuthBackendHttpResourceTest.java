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

package io.barracks.rabbitmqauth.rest;


import io.barracks.rabbitmqauth.manager.RabbitMQAuthBackendHttpManager;
import io.barracks.rabbitmqauth.model.ResourceCheck;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RabbitMQAuthBackendHttpResourceTest {

    @Mock
    private RabbitMQAuthBackendHttpManager rabbitMQAuthBackendHttpManager;

    @InjectMocks
    private RabbitMQAuthBackendHttpResource rabbitMQAuthBackendHttpResource;

    @Test
    public void resource_whenResourceIsWrongButVhostIsNotDefault_shouldReturnAllow() {
        //Given
        final String expected = RabbitMQAuthBackendHttpResource.ALLOW;
        final ResourceCheck resource = ResourceCheck.builder()
                .username("coucou")
                .vhost("/internal")
                .resource("topic")
                .name("resourceName")
                .permission("write")
                .build();

        //When
        final String result = rabbitMQAuthBackendHttpResource.resource(resource);

        //Then
        verify(rabbitMQAuthBackendHttpManager, never()).checkResourceName(resource.getName(), resource.getResource());
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void resource_whenResourceIsTopic_shouldCallManager() {
        //Given
        final String expected = RabbitMQAuthBackendHttpResource.ALLOW;
        final ResourceCheck resource = ResourceCheck.builder()
                .username("coucou")
                .vhost("/")
                .resource("topic")
                .name("resourceName")
                .permission("write")
                .build();

        doReturn(true).when(rabbitMQAuthBackendHttpManager).checkResourceName(resource.getName(), resource.getResource());

        //When
        final String result = rabbitMQAuthBackendHttpResource.resource(resource);

        //Then
        verify(rabbitMQAuthBackendHttpManager).checkResourceName(resource.getName(), resource.getResource());
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void resource_whenResourceIsQueue_shouldCallManager() {
        //Given
        final String expected = RabbitMQAuthBackendHttpResource.DENY;
        final ResourceCheck resource = ResourceCheck.builder()
                .username("coucou")
                .vhost("/")
                .resource("queue")
                .name("resourceName")
                .permission("write")
                .build();

        doReturn(false).when(rabbitMQAuthBackendHttpManager).checkResourceName(resource.getName(), resource.getResource());

        //When
        final String result = rabbitMQAuthBackendHttpResource.resource(resource);

        //Then
        verify(rabbitMQAuthBackendHttpManager).checkResourceName(resource.getName(), resource.getResource());
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void resource_whenResourceIsNotTopicOrQueue_shouldReturnAllow() {
        //Given
        final String expected = RabbitMQAuthBackendHttpResource.ALLOW;
        final ResourceCheck resource = ResourceCheck.builder()
                .username("coucou")
                .vhost("/")
                .resource("coucou")
                .name("resourceName")
                .permission("write")
                .build();

        //When
        final String result = rabbitMQAuthBackendHttpResource.resource(resource);

        //Then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void user_shouldReturnAllow() {
        //Given
        final String expected = RabbitMQAuthBackendHttpResource.ALLOW;
        final String username = UUID.randomUUID().toString();
        final String password = UUID.randomUUID().toString();

        //When
        final String result = rabbitMQAuthBackendHttpResource.user(username, password);

        //Then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void vhost_shouldReturnAllow() {
        //Given
        final String expected = RabbitMQAuthBackendHttpResource.ALLOW;

        //When
        final String result = rabbitMQAuthBackendHttpResource.vhost();

        //Then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void topic_shouldReturnAllow() {
        //Given
        final String expected = RabbitMQAuthBackendHttpResource.ALLOW;

        //When
        final String result = rabbitMQAuthBackendHttpResource.topic(UUID.randomUUID().toString());

        //Then
        assertThat(result).isEqualTo(expected);
    }

}
