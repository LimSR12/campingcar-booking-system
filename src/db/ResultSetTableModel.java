package db;

import java.sql.ResultSet;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import java.sql.ResultSetMetaData;

/*
 * ResultSet의 메타데이터(column 이름, 개수)와
 * 데이터를 벡터로 읽어들여 JTable 모델로 변환해주는 클래스
 */

public class ResultSetTableModel extends AbstractTableModel {
	private final Vector<String> cols = new Vector<>();
	private final Vector<Vector<Object>> rows = new Vector<>();

	// 생성자: ResultSet 전체를 읽어들여 cols, rows에 채워넣는다
	public ResultSetTableModel(ResultSet rs) throws Exception {
		// 칼럼 이름 추출
		ResultSetMetaData md = rs.getMetaData();
		int colCnt = md.getColumnCount();
		for(int i=1; i<=colCnt; i++) cols.add(md.getColumnLabel(i));
		
		// 각 행 읽기
		while (rs.next()) {
			Vector<Object> row = new Vector<>();
			for(int i=1; i<=colCnt; i++) row.add(rs.getObject(i));
			rows.add(row);
		}
	}

	@Override
	public int getRowCount() {
		return rows.size(); // 행 개수
	}

	@Override
	public int getColumnCount() {
		return cols.size(); // 칼럼 개수
	}
	
	public String getColumnName(int c) {
		return cols.get(c); // 칼럼 헤더
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return rows.get(rowIndex).get(columnIndex); // 셀 값
	}
	
}
