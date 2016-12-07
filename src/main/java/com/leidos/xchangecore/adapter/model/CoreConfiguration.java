package com.leidos.xchangecore.adapter.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CoreConfiguration
implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    private String uri = "http://localhost/core/ws/services";
    private String username = "xchangecore";
    private String password = "xchangecore";

    public CoreConfiguration() {
        super();
    }

    public CoreConfiguration(String uri, String usernmae, String passwd) {
        this.uri = uri;
        this.username = usernmae;
        this.password = passwd;
    }

    public String getPassword() {

        return this.password;
    }
    public String getUri() {
        return this.uri;
    }

    public String getUsername() {

        return this.username;
    }

}
