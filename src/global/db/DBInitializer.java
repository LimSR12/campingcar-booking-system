package global.db;

import java.io.BufferedReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBInitializer {
	public static void run() throws Exception {
		
		Connection conn = DBConnection.getConnection();
		Statement stmt = conn.createStatement();
		Path sqlPath = Path.of("202501-19010801-ini.sql");
		System.out.println("run() 진입");
        try {
            // DB 연결
            Reader rd = Files.newBufferedReader(sqlPath);
            BufferedReader br = new BufferedReader(rd);
            
            StringBuilder sb = new StringBuilder();
            String line;
            
            while((line = br.readLine()) != null) {
            	line = line.trim();
            	
            	// 주석, 빈줄 스킵
            	if (line.isEmpty() || line.startsWith("--") || line.startsWith("//") || line.startsWith("#"))
                    continue;
            	
            	// 쿼리 누적
            	sb.append(line).append(' ');
            	
            	// 세미콜론 끝 -> 실행
            	if(line.endsWith(";")) {
            		String sql = sb.toString();
            		sql = sql.substring(0, sql.length() - 1); // 세미콜론 제거
            		stmt.execute(sql);
            		sb.setLength(0); // 버퍼 비우기
            	}
            }
            
            if (sb.length() > 0) {
            	stmt.execute(sb.toString());
            }
            
            conn.close();
            
        } catch (SQLException e) {
            System.out.println("🔴 [SQL 오류 발생]");
            System.out.println("▶ SQLState : " + e.getSQLState());
            System.out.println("▶ ErrorCode : " + e.getErrorCode());
            System.out.println("▶ Message   : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("🟠 [일반 예외 발생]");
            System.out.println("▶ 예외 메시지 : " + e.getMessage());
            e.printStackTrace();
        }

	}
	
	public static List<String> getTableNames() throws SQLException {
		List<String> tables = new ArrayList<>();
		try (Connection conn = DBConnection.getConnection()) {
			DatabaseMetaData md = conn.getMetaData();
			try (ResultSet rs = md.getTables(conn.getCatalog(), conn.getSchema(), "%", new String[]{"TABLE"})) {
				while (rs.next()) {
					tables.add(rs.getString("TABLE_NAME"));
				}
			}
		} 
		return tables;
	}
}
