package db;

import java.io.*;
import java.sql.*;

public class db3 {
	public static void main (String[] args) {
		Connection conn;
		Statement stmt = null;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL 드라이버 로드
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/DBTEST", "root","1234"); // JDBC 연결
			System.out.println("DB 연결 완료");
			
			stmt = conn.createStatement(); // SQL문 처리용 Statement 객체 생성
			//printTable(stmt);

			stmt.executeUpdate("insert into 고객 values('mango','아무개',45,'gold','회사원',0)"); // 레코드 추가
			printTable(stmt);
			
			stmt.executeUpdate("update 고객 set 고객이름='홍길동' where 고객아이디='mango'"); //데이터 수정
			printTable(stmt);
			
			stmt.executeUpdate("delete from 고객 where 고객이름='홍길동'"); // 레코드 삭제
			printTable(stmt);
		} catch (ClassNotFoundException e) {
			System.out.println("JDBC 드라이버 로드 오류");
		} catch (SQLException e) {
			System.out.println("SQL 실행 오류");
			e.printStackTrace();
		}
	}
	// 레코드의 각 열의 값 화면에 출력
	private static void printTable(Statement stmt) throws SQLException {
		ResultSet srs = stmt.executeQuery("select * from 고객");
		while (srs.next()) {
			System.out.print(srs.getString("고객이름"));
			System.out.print("\t|\t" + srs.getString("고객아이디"));
			System.out.println("\t|\t" + srs.getString("등급"));
		}
		System.out.println("=========================================");
	}

}
