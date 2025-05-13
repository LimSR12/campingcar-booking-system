package entity;

import java.time.LocalDateTime;

public class InternalMaintenance {
    private long id;
    private long carId;
    private long partId;
    private long staffId;
    private LocalDateTime repairDate;
    private int duration;
    private String description;

    // 기본 생성자
    public InternalMaintenance() {}

    // 모든 필드를 매개변수로 받는 생성자
    public InternalMaintenance(long id, long carId, long partId, long staffId,
                               LocalDateTime repairDate, int duration, String description) {
        this.id = id;
        this.carId = carId;
        this.partId = partId;
        this.staffId = staffId;
        this.repairDate = repairDate;
        this.duration = duration;
        this.description = description;
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

    public long getPartId() {
        return partId;
    }

    public void setPartId(long partId) {
        this.partId = partId;
    }

    public long getStaffId() {
        return staffId;
    }

    public void setStaffId(long staffId) {
        this.staffId = staffId;
    }

    public LocalDateTime getRepairDate() {
        return repairDate;
    }

    public void setRepairDate(LocalDateTime repairDate) {
        this.repairDate = repairDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
