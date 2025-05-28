package global.entity;

public class Staff {
    private long id;
    private String name;
    private String phone;
    private String address;
    private int salary;
    private int familyNum;
    private String department;
    private String role;

    // 기본 생성자
    public Staff() {}

    // 모든 필드를 매개변수로 받는 생성자
    public Staff(long id, String name, String phone, String address, int salary,
                 int familyNum, String department, String role) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.salary = salary;
        this.familyNum = familyNum;
        this.department = department;
        this.role = role;
    }

    // Getter & Setter
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public int getFamilyNum() {
        return familyNum;
    }

    public void setFamilyNum(int familyNum) {
        this.familyNum = familyNum;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
