package com.project.parking.data;

/**
 * Created by Yohanes on 15/06/2017.
 */

public class InqForgotPasswordRequest implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
