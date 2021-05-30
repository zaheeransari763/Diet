package com.example.diet;

public class Dietician {
    public String Addresss, City, Name, Email, Phone;

    public Dietician() {
    }

    public Dietician(String address, String city, String name, String email, String phone) {
        this.Addresss = address;
        this.City = city;
        this.Name = name;
        this.Email = email;
        this.Phone = phone;
    }

    public String getAddress() {
        return Addresss;
    }

    public void setAddress(String address) {
        Addresss = address;
    }

    public String getCityy() {
        return City;
    }

    public void setCityy(String city) {
        City = city;
    }

    public String getNamee() {
        return Name;
    }

    public void setNamee(String name) {
        Name = name;
    }

    public String getEmaill() {
        return Email;
    }

    public void setEmaill(String email) {
        Email = email;
    }

    public String getPhonee() {
        return Phone;
    }

    public void setPhonee(String phone) {
        Phone = phone;
    }
}
