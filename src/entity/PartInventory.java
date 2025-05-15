package entity;

import java.time.LocalDateTime;

public class PartInventory {
    private long id;
    private String name;
    private double price;
    private int quantity;
    private LocalDateTime receivedDate;
    private String supplierName;

    // 기본 생성자
    public PartInventory() {}

    // 모든 필드를 매개변수로 받는 생성자
    public PartInventory(long id, String name, double price, int quantity, LocalDateTime receivedDate, String supplierName) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.receivedDate = receivedDate;
        this.supplierName = supplierName;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDateTime receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
}
