package user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import global.db.DBConnection;

public class RentalDao {

    public Set<LocalDate> getReservedDatesByPlate(String plateNumber) {
        Set<LocalDate> reservedDates = new HashSet<>();

        String sql = "SELECT start_date, return_date " +
                     "FROM rental r JOIN camping_car c ON r.car_id = c.id " +
                     "WHERE c.plate_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, plateNumber);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                LocalDate start = rs.getTimestamp("start_date").toLocalDateTime().toLocalDate();
                LocalDate end = rs.getTimestamp("return_date").toLocalDateTime().toLocalDate();

                for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
                    reservedDates.add(d);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return reservedDates;
    }
}
