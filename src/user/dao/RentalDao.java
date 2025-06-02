package user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    
    // MyReservationPanel 사용자 id 로 대여 정보 받아옴
    public List<Rental> getReservationsByCustomerId(Long customerId) {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rental WHERE customer_id = ? ORDER BY start_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Rental rental = new Rental();
                rental.setId(rs.getLong("id"));
                rental.setCarId(rs.getLong("car_id"));
                rental.setCustomerId(rs.getLong("customer_id"));
                rental.setLicenseNumber(rs.getString("license_number"));
                rental.setCompanyId(rs.getLong("company_id"));
                rental.setStartDate(rs.getTimestamp("start_date").toLocalDateTime());
                rental.setReturnDate(rs.getTimestamp("return_date").toLocalDateTime());
                rental.setRentalDays(rs.getInt("rental_days"));
                rental.setRentalFee(rs.getDouble("rental_fee"));
//                rental.setFeeDueDate(rs.getTimestamp("fee_due_date").toLocalDateTime());
                Timestamp ts = rs.getTimestamp("fee_due_date");
                if (ts != null) {
                    rental.setFeeDueDate(ts.toLocalDateTime());
                } else {
                    rental.setFeeDueDate(null); 
                }

                rental.setExtraDetails(rs.getString("extra_details"));
//                Object extraFeeObj = rs.getObject("extra_fee");
//                if (extraFeeObj != null) {
//                    rental.setExtraFee(rs.getDouble("extra_fee"));
//                } else {
//                    rental.setExtraFee(null);
//                }

                rental.setExtraFee(rs.getObject("extra_fee") != null ? rs.getDouble("extra_fee") : null);

                rentals.add(rental);
            }

        } catch (SQLException e) {
        	System.out.println("SQL 에러 발생:");
            System.out.println("메시지: " + e.getMessage());
            System.out.println("SQL 상태: " + e.getSQLState());
            System.out.println("에러 코드: " + e.getErrorCode());
            e.printStackTrace();
        }

        return rentals;
    }
    
    // rental id 받아서 삭제하는 메서드
    public void deleteByRentalId(Long id) {
        String sql = "DELETE FROM rental WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("삭제 성공: " + rowsAffected + "건");
            } else {
                System.out.println("삭제할 예약이 없습니다.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 캠핑카 예약 내역 업데이트 하는 메서드
    public boolean updateCampingCar(Long rentalId, Long newCarId, int unitPrice, int rentalDays) {
        String sql = "UPDATE rental SET car_id = ?, rental_fee = ? WHERE id = ?";

        double newFee = unitPrice * rentalDays;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, newCarId);
            pstmt.setDouble(2, newFee);
            pstmt.setLong(3, rentalId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    // 캠핑카 id, name 받아오는 DTO
    public class CampingCarDto {
        private Long id;
        private String name;

        public CampingCarDto(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;  // ComboBox에서 보여줄 텍스트
        }
    }

    
    // 전체 캠핑카 id 받아오는 메서드
    public List<CampingCarDto> getAllCampingCars() {
        List<CampingCarDto> carList = new ArrayList<>();
        String sql = "SELECT id, name FROM camping_car";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Long id = rs.getLong("id");
                String name = rs.getString("name");
                carList.add(new CampingCarDto(id, name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return carList;
    }

    // 예약 일정 변경 메서드
    public boolean updateRentalDates(Long rentalId, LocalDate newStartDate, LocalDate newReturnDate, int unitPrice) {
        String sql = "UPDATE rental SET start_date = ?, return_date = ?, rental_days = ?, rental_fee = ? WHERE id = ?";

        int rentalDays = (int) (newReturnDate.toEpochDay() - newStartDate.toEpochDay()) + 1;
        double newFee = unitPrice * rentalDays;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(newStartDate.atStartOfDay()));
            pstmt.setTimestamp(2, Timestamp.valueOf(newReturnDate.atStartOfDay()));
            pstmt.setInt(3, rentalDays);
            pstmt.setDouble(4, newFee);
            pstmt.setLong(5, rentalId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    // 선택한 기간에 이미 예약되어있는지 확인하는 메서드
    public boolean isDateOverlapping(Long rentalId, Long carId, LocalDate newStart, LocalDate newEnd) {
        String sql = """
            SELECT COUNT(*) 
            FROM rental 
            WHERE car_id = ? 
              AND id != ? 
              AND (
                (start_date <= ? AND return_date >= ?) OR
                (start_date <= ? AND return_date >= ?) OR
                (start_date >= ? AND return_date <= ?)
              )
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, carId);
            pstmt.setLong(2, rentalId);
            pstmt.setTimestamp(3, Timestamp.valueOf(newEnd.atStartOfDay()));   // 기존 예약이 끝나는 날 >= 새 시작일
            pstmt.setTimestamp(4, Timestamp.valueOf(newStart.atStartOfDay()));
            pstmt.setTimestamp(5, Timestamp.valueOf(newStart.atStartOfDay())); // 기존 예약이 시작하는 날 <= 새 종료일
            pstmt.setTimestamp(6, Timestamp.valueOf(newEnd.atStartOfDay()));
            pstmt.setTimestamp(7, Timestamp.valueOf(newStart.atStartOfDay())); // 전체를 덮는 경우
            pstmt.setTimestamp(8, Timestamp.valueOf(newEnd.atStartOfDay()));

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }
    
    // 캠핑카 예약 가격 가져오는 메서드
    public int getCampingCarPrice(Long carId) {
        String sql = "SELECT rental_price FROM camping_car WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, carId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("rental_price");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0; // 조회 실패 시 0원 처리
    }

}
