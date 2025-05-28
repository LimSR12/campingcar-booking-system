package global.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ExternalMaintenance {
    private long id;
    private long carId;
    private long centerId;
    private long customerId;
    private String licenseNumber;
    private long companyId;
    private String repairDetails;
    private LocalDateTime repairDate;
    private double repairFee;
    private LocalDate feeDueDate;
    private String extraDetails;

    // 기본 생성자
    public ExternalMaintenance() {}

    // 모든 필드를 매개변수로 받는 생성자
    public ExternalMaintenance(long id, long carId, long centerId, long customerId, String licenseNumber,
                               long companyId, String repairDetails, LocalDateTime repairDate,
                               double repairFee, LocalDate feeDueDate, String extraDetails) {
        this.id = id;
        this.carId = carId;
        this.centerId = centerId;
        this.customerId = customerId;
        this.licenseNumber = licenseNumber;
        this.companyId = companyId;
        this.repairDetails = repairDetails;
        this.repairDate = repairDate;
        this.repairFee = repairFee;
        this.feeDueDate = feeDueDate;
        this.extraDetails = extraDetails;
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

    public long getCenterId() {
        return centerId;
    }

    public void setCenterId(long centerId) {
        this.centerId = centerId;
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

    public String getRepairDetails() {
        return repairDetails;
    }

    public void setRepairDetails(String repairDetails) {
        this.repairDetails = repairDetails;
    }

    public LocalDateTime getRepairDate() {
        return repairDate;
    }

    public void setRepairDate(LocalDateTime repairDate) {
        this.repairDate = repairDate;
    }

    public double getRepairFee() {
        return repairFee;
    }

    public void setRepairFee(double repairFee) {
        this.repairFee = repairFee;
    }

    public LocalDate getFeeDueDate() {
        return feeDueDate;
    }

    public void setFeeDueDate(LocalDate feeDueDate) {
        this.feeDueDate = feeDueDate;
    }

    public String getExtraDetails() {
        return extraDetails;
    }

    public void setExtraDetails(String extraDetails) {
        this.extraDetails = extraDetails;
    }
}
