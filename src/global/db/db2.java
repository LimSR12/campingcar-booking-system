package global.db;
import java.io.*;
import java.sql.*;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;

public class db2 {
	public static void main (String[] args) {
		Connection conn;
		Statement stmt = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL 드라이버 로드
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/DBTEST", "root","1234"); // JDBC 연결

			System.out.println("DB 연결 완료");
			
			stmt = conn.createStatement(); // SQL문 처리용 Statement 객체 생성
			
			ResultSet srs = stmt.executeQuery("select * from customer"); // 테이블의 모든 데이터 검색
			System.out.println("[Query 1]");
			printData(srs, "username", "id", "name");
			
			srs = stmt.executeQuery("select username, id, name from customer where id = 3"); // 고객아이디 = 'banana'만 검색
			System.out.println("\n[Query 2]");
			printData(srs, "username", "id", "name");
			
		} catch (ClassNotFoundException e) {
			System.out.println("JDBC 드라이버 로드 오류");
		} catch (SQLException e) {
			System.out.println("SQL 실행오류");
		} 
	}
	// 레코드의 각 열의 값 화면에 출력
	private static void printData(ResultSet srs, String col1, String col2, String col3) throws SQLException {
		while (srs.next()) {
			if (!col1.equals(""))
				System.out.print(srs.getString("username")); 
			if (!col2.equals(""))
				System.out.print("\t|\t" + srs.getString("id"));
			if (!col3.equals(""))
				System.out.println("\t|\t" + srs.getString("name"));
			else 
				System.out.println();
		}
	}

}
