package entity;

import java.time.LocalDateTime;

public class Rental {
    private long id;
    private long carId;
    private long customerId;
    private String licenseNumber;
    private long companyId;
    private LocalDateTime startDate;
    private LocalDateTime returnDate;
    private int rentalDays;
    private double rentalFee;
    private LocalDateTime feeDueDate;
    private String extraDetails;
    private Double extraFee; // NULL 가능하므로 참조형 Double 사용

    // 기본 생성자
    public Rental() {}

    // 모든 필드를 매개변수로 받는 생성자
    public Rental(long id, long carId, long customerId, String licenseNumber, long companyId,
                  LocalDateTime startDate, LocalDateTime returnDate, int rentalDays,
                  double rentalFee, LocalDateTime feeDueDate, String extraDetails, Double extraFee) {
        this.id = id;
        this.carId = carId;
        this.customerId = customerId;
        this.licenseNumber = licenseNumber;
        this.companyId = companyId;
        this.startDate = startDate;
        this.returnDate = returnDate;
        this.rentalDays = rentalDays;
        this.rentalFee = rentalFee;
        this.feeDueDate = feeDueDate;
        this.extraDetails = extraDetails;
        this.extraFee = extraFee;
    }

    // Getter & Setter
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCarId() {
        return carId;
    }

    public void setCarId(long carId) {
        this.carId = carId;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public int getRentalDays() {
        return rentalDays;
    }

    public void setRentalDays(int rentalDays) {
        this.rentalDays = rentalDays;
    }

    public double getRentalFee() {
        return rentalFee;
    }

    public void setRentalFee(double rentalFee) {
        this.rentalFee = rentalFee;
    }

    public LocalDateTime getFeeDueDate() {
        return feeDueDate;
    }

    public void setFeeDueDate(LocalDateTime feeDueDate) {
        this.feeDueDate = feeDueDate;
    }

    public String getExtraDetails() {
        return extraDetails;
    }

    public void setExtraDetails(String extraDetails) {
        this.extraDetails = extraDetails;
    }

    public Double getExtraFee() {
        return extraFee;
    }

    public void setExtraFee(Double extraFee) {
        this.extraFee = extraFee;
    }
}
