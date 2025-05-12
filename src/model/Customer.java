package model;

import java.sql.Timestamp;

public class Customer {
    private Long id;
    private String username;
    private String password;
    private String licenseNumber;
    private String name;
    private String address;
    private String phone;
    private String email;
    private Timestamp prevReturnDate;  // DATETIME → java.sql.Timestamp 또는 String
    private String prevCarType;

    // 기본 생성자
    public Customer() {}

    // 전체 필드를 받는 생성자
    public Customer(Long id, String username, String password, String licenseNumber,
                    String name, String address, String phone, String email,
                    Timestamp prevReturnDate, String prevCarType) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.licenseNumber = licenseNumber;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.prevReturnDate = prevReturnDate;
        this.prevCarType = prevCarType;
    }

    // Getter & Setter (필요한 것만 뽑아서 사용해도 됨)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getPrevReturnDate() {
        return prevReturnDate;
    }

    public void setPrevReturnDate(Timestamp prevReturnDate) {
        this.prevReturnDate = prevReturnDate;
    }

    public String getPrevCarType() {
        return prevCarType;
    }

    public void setPrevCarType(String prevCarType) {
        this.prevCarType = prevCarType;
    }
}
