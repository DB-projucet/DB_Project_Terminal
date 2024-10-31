package common;
// 1. DB 연결정보 생성
// 2. 드라이버 로딩
// 3. 오라클 데이터베이스 연결
// 4. Prepared Statement로 SQL 쿼리문을 전달
// 5. 결과 받기
// 6. 연결 끊기

import java.sql.*;

public class Common {
    // DB 연결 정보 생성
    final static String ORACLE_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    final static String ORACLE_ID = "SCOTT";
    final static String ORACLE_PW = "TIGER";
    final static String ORACLE_DRV = "oracle.jdbc.driver.OracleDriver";

    // 오라클 드라이버 로딩 및 연결

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Connection conn = null;
        try {
            Class.forName(ORACLE_DRV);
            conn = DriverManager.getConnection(ORACLE_URL, ORACLE_ID, ORACLE_PW);
            //System.out.println("오라클 DB 연결 성공");
        } catch (Exception e) {
            System.out.println("오라클 DB 연결 실패 !!!");
        }
        return conn;
    }

    public static void close(Connection conn) {
        try{
            if (conn != null && !conn.isClosed()) {
                conn.close();
                //System.out.println("Connection 해제 성공");
            }
        } catch (Exception e) {
            System.out.println("Connection 해제 실패 !!!");
        }
    }

    public static void close(Statement stmt) {
        try {
            if(stmt != null && !stmt.isClosed()) {
                stmt.close();
                //System.out.println("Statement 해제 성공");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void close(ResultSet rset) {
        try {
            if(rset != null && !rset.isClosed()) {
                rset.close();
                //System.out.println("Result set 해제 성공");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void commit(Connection conn) {
        try {
            if(conn != null && !conn.isClosed()) {
                conn.commit();
                //System.out.println("커밋 완료");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void rollback(Connection conn) {
        try {
            if(conn != null && !conn.isClosed()) {
                conn.rollback();
                //System.out.println("롤백 완료");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void close(ResultSet rs, PreparedStatement psmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (psmt != null) psmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close(PreparedStatement psmt, Connection conn) {

    }
}
