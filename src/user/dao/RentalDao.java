package user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import global.db.DBConnection;
import global.entity.Rental;

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
    
    public boolean insertRental(Rental rental) {
        String sql = "INSERT INTO rental (car_id, license_number, customer_id, company_id, start_date, return_date, rental_days, rental_fee, fee_due_date, extra_details, extra_fee) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, rental.getCarId());
            pstmt.setString(2, rental.getLicenseNumber());
            pstmt.setLong(3, rental.getCustomerId());
            pstmt.setLong(4, rental.getCompanyId());
            pstmt.setTimestamp(5, 	Timestamp.valueOf(rental.getStartDate()));
            pstmt.setTimestamp(6, Timestamp.valueOf(rental.getReturnDate()));
            pstmt.setLong(7, rental.getRentalDays());
            pstmt.setDouble(8, rental.getRentalFee());
            pstmt.setTimestamp(9, Timestamp.valueOf(rental.getFeeDueDate()));
            if (rental.getExtraDetails() != null) {
                pstmt.setString(10, rental.getExtraDetails());
            } else {
                pstmt.setNull(10, java.sql.Types.VARCHAR);
            }

            if (rental.getExtraFee() != null) {
                pstmt.setDouble(11, rental.getExtraFee());
            } else {
                pstmt.setNull(11, java.sql.Types.DOUBLE);
            }

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
