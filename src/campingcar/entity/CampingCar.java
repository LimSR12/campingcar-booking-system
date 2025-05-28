package campingcar.entity;

import java.sql.Timestamp;

public class CampingCar {
    private Long id;
    private Long companyId;
    private String name;
    private String plateNumber;
    private int capacity;
    private String image;
    private String detailInfo;
    private int rentalPrice;
    private String registrationDate;

    // 기본 생성자
    public CampingCar() {}

    // 전체 필드를 받는 생성자
    public CampingCar(Long id, Long companyId, String name, String plateNumber, int capacity,
                      String image, String detailInfo, int rentalPrice, String registrationDate) {
        this.id = id;
        this.companyId = companyId;
        this.name = name;
        this.plateNumber = plateNumber;
        this.capacity = capacity;
        this.image = image;
        this.detailInfo = detailInfo;
        this.rentalPrice = rentalPrice;
        this.registrationDate = registrationDate;
    }

 // user/CampinCarPanel에서 캠핑카 조회
    public CampingCar(String name, String plateNumber, int capacity, int rentalPrice, String registrationDate, String image, String detailInfo) {
        this.name = name;
        this.plateNumber = plateNumber;
        this.capacity = capacity;
        this.rentalPrice = rentalPrice;
        this.registrationDate = registrationDate;
        this.image = image;
        this.detailInfo = detailInfo;
    }

    
    // Getter & Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDetailInfo() {
        return detailInfo;
    }

    public void setDetailInfo(String detailInfo) {
        this.detailInfo = detailInfo;
    }

    public int getRentalPrice() {
        return rentalPrice;
    }

    public void setRentalPrice(int rentalPrice) {
        this.rentalPrice = rentalPrice;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }
}
