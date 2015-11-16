/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.tools.dashjs.html5.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.tools.dashjs.html5.data.VideoRepository;
import org.jboss.tools.dashjs.html5.rest.VideoService;
import org.jboss.tools.dashjs.html5.service.VideoRegistration;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.jboss.tools.dashjs.html5.model.Video;
import org.jboss.tools.dashjs.html5.util.Resources;

/**
 * Uses Arquilian to test the JAX-RS processing class for member registration.
 * 
 * @author balunasj
 */
@RunWith(Arquillian.class)
public class VideoRegistrationTest {
    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap
                .create(WebArchive.class, "test.war")
                .addClasses(Video.class, VideoService.class, VideoRepository.class, VideoRegistration.class,
                        Resources.class).addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource("arquillian-ds.xml").addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    VideoService memberRegistration;

    @Inject
    Logger log;

    @Test
    @InSequence(1)
    public void testRegister() throws Exception {
        Video member = createVideoInstance("Jack Doe", "jack@mailinator.com", "2125551234");
        Response response = memberRegistration.createVideo(member);

        assertEquals("Unexpected response status", 200, response.getStatus());
        log.info(" New member was persisted and returned status " + response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(2)
    public void testInvalidRegister() throws Exception {
        Video member = createVideoInstance("", "", "");
        Response response = memberRegistration.createVideo(member);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 3,
                ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid member register attempt failed with return code " + response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(3)
    public void testDuplicateEmail() throws Exception {
        // Register an initial user
        Video member = createVideoInstance("Jane Doe", "jane@mailinator.com", "2125551234");
        memberRegistration.createVideo(member);

        // Register a different user with the same uri
        Video anotherVideo = createVideoInstance("John Doe", "jane@mailinator.com", "2133551234");
        Response response = memberRegistration.createVideo(anotherVideo);

        assertEquals("Unexpected response status", 409, response.getStatus());
        assertNotNull("response.getEntity() should not null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 1,
                ((Map<String, String>) response.getEntity()).size());
        log.info("Duplicate member register attempt failed with return code " + response.getStatus());
    }

    private Video createVideoInstance(String name, String uri, String explode) {
        Video member = new Video();
        member.setURI(uri);
        member.setName(name);
        member.setExplode(explode);
        return member;
    }
}
