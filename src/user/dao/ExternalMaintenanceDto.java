package user.dao;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExternalMaintenanceDto {
    private Long id;
    private String centerName;
    private String repairDetails;
    private LocalDateTime repairDate;
    private Double repairFee;

    public ExternalMaintenanceDto(Long id, String centerName, String repairDetails, LocalDateTime repairDate, Double repairFee) {
        this.id = id;
        this.centerName = centerName;
        this.repairDetails = repairDetails;
        this.repairDate = repairDate;
        this.repairFee = repairFee;
    }

    public Long getId() {
        return id;
    }

    public String getCenterName() {
        return centerName;
    }

    public String getRepairDetails() {
        return repairDetails;
    }

    public LocalDateTime getRepairDate() {
        return repairDate;
    }

    public String getFormattedRepairDate() {
        return repairDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public Double getRepairFee() {
        return repairFee;
    }
}
