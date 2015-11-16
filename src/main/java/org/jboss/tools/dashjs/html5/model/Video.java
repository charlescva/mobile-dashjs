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
package org.jboss.tools.dashjs.html5.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
@Table(name = "Video_html5mobi", uniqueConstraints = @UniqueConstraint(columnNames = "uri"))
public class Video implements Serializable {
    /** Default value included to remove warning. Remove or modify at will. **/
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Size(min = 1, max = 25, message = "1-25 letters and spaces")
    @Pattern(regexp = ".*", message = "Must not contain numbers")
    private String name;

    @NotNull
    @Pattern(regexp = ".*[mp4|ismv|m4v|acc|ac3|ec3|dtshd]", message = "Only extensions mp4, ismv, mov, m4v, aac, ac3, ec3, dtshd are known.")
    private String uri;

    @Column(name = "explode")
    private String explodeVideo;

    @Column(name = "mpduri")
    private String mpdUri;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getMpdURI() {
    	return mpdUri;
    }
    
    public void setMpdURI(String mpduri) {
    	this.mpdUri = mpduri;
    }

    public String getExplode() {
        return explodeVideo;
    }

    public void setExplode(String explodeVideo) {
        this.explodeVideo = explodeVideo;
    }

    public String getURI() {
        return uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }
}
