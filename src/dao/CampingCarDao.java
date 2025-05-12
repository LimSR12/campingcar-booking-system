package dao;

import db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.CampingCar;

public class CampingCarDao {

    public List<CampingCar> getAllCampingCars() {
        List<CampingCar> list = new ArrayList<>();

        String sql = "SELECT name, plate_number, capacity, rental_price, DATE_FORMAT(registration_date, '%Y-%m-%d %H:%i:%s') as registration_date FROM camping_car";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                CampingCar car = new CampingCar(
                        rs.getString("name"),
                        rs.getString("plate_number"),
                        rs.getInt("capacity"),
                        rs.getInt("rental_price"),
                        rs.getString("registration_date")
                );
                list.add(car);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
