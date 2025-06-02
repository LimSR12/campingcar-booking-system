package user.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import global.db.DBConnection;

public class ExternalCenterDao {
	public List<ExternalCenterDto> getAllCenters() {
	    List<ExternalCenterDto> list = new ArrayList<>();
	    String sql = "SELECT id, name FROM external_center";

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql);
	         ResultSet rs = pstmt.executeQuery()) {

	        while (rs.next()) {
	            Long id = rs.getLong("id");
	            String name = rs.getString("name");
	            list.add(new ExternalCenterDto(id, name));
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return list;
	}

    
}
