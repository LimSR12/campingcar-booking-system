package entity;

public class Company {
    private long id;
    private String name;
    private String address;
    private String phone;
    private String managerName;
    private String managerEmail;

    // 기본 생성자
    public Company() {}

    // 모든 필드를 매개변수로 받는 생성자
    public Company(long id, String name, String address, String phone, String managerName, String managerEmail) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.managerName = managerName;
        this.managerEmail = managerEmail;
    }

    // getter & setter
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }
}
