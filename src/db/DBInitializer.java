package db;

import java.io.BufferedReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

public class DBInitializer {
	public static void run() throws Exception {
		
		Connection conn = null;
		Path sqlPath = Path.of("202501-19010801-ini.sql");
		
        try {
            // DB 연결
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306", "root", "1234"
            );

            Statement stmt = conn.createStatement();
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
            
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
