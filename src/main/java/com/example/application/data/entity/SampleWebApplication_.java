package com.example.application.data.entity;

import javax.persistence.Entity;
import javax.validation.constraints.Email;

@Entity
public class SampleWebApplication_ extends AbstractEntity {

    private String nama;
    @Email
    private String email;
    private String address;

    public String getNama() {
        return nama;
    }
    public void setNama(String nama) {
        this.nama = nama;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

}
