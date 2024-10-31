package dao;

import common.Common;
import vo.ProductVO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// DAO(Database Access Object): 데이터베이스에 접근하여 데이터를 조회하거나 수정하는 역할
public class ProductDAO {
    private static Connection conn;

    public ProductDAO(Connection conn) {
        ProductDAO.conn = conn;
    }

    static PreparedStatement psmt = null;
    static ResultSet rs = null;

    public ProductDAO() {

    }

    // 제품 조회 메서드 (카테고리와 정렬 옵션에 따라 조회)
    public static List<ProductVO> productSelect(String type, String sortOption) {
        List<ProductVO> list = new ArrayList<>();
        try {
            conn = Common.getConnection();

            // OrderTB의 product_id를 업데이트하는 쿼리
            String updateQuery = "UPDATE OrderTB " +
                    "SET product_id = (SELECT product_id FROM ProductTB WHERE p_type = ? AND p_name = OrderTB.p_name AND ROWNUM = 1) " +
                    "WHERE product_id IS NULL";
            psmt = conn.prepareStatement(updateQuery);
            psmt.setString(1, type);  // 업데이트할 타입을 설정
            psmt.executeUpdate();

            String query = "";

            // 정렬 옵션에 따른 쿼리 구분
            if ("popular".equals(sortOption)) {
                // 인기순: sales_volume 기준 내림차순
                query = "SELECT P.P_name, P.P_price FROM ProductTB P " +
                        "JOIN OrderTB O ON P.product_id = O.product_id " +
                        "WHERE P.P_type = ? " +
                        "GROUP BY P.P_name, P.P_price " +
                        "ORDER BY SUM(O.sales_volume) DESC";
            } else if ("high".equals(sortOption)) {
                // 가격 높은순, 중복 제거
                query = "SELECT DISTINCT P_name, P_price FROM ProductTB WHERE P_type = ? ORDER BY P_price DESC";
            } else {
                // 가격 낮은순, 중복 제거
                query = "SELECT DISTINCT P_name, P_price FROM ProductTB WHERE P_type = ? ORDER BY P_price ASC";
            }

            psmt = conn.prepareStatement(query);
            psmt.setString(1, type);
            rs = psmt.executeQuery();
            //System.out.println(query);

            while (rs.next()) {
                // ResultSet에서 컬럼명을 정확히 지정하여 값 가져오기
                String P_name = rs.getString("P_name");
                int P_price = rs.getInt("P_price");

                // ProductVO 객체에 값 설정
                ProductVO vo = new ProductVO(P_name, P_price);
                list.add(vo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            Common.close(rs, psmt, conn);
        }
        return list;
    }

    // [4] 상품 추천 메소드
    public static void menuSelect() throws SQLException {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println();
            System.out.println(" ======================== 카테고리 선택 ======================== ");
            System.out.println();
            System.out.println("        [1] 러닝화 [2] 축구화 [3] 농구화 [4] 일상화 [5] 샌들 ");
            System.out.println();
            System.out.println("                            [6] 메인 메뉴");
            System.out.println();
            System.out.println(" ============================================================= ");
            System.out.println();
            System.out.print(" 메뉴 선택 [  ] : ");
            int choice = sc.nextInt();

            if (choice == 6) {
                //System.out.println(" 프로그램을 종료합니다.");
                break;
            }
            System.out.println();
            System.out.println(" =========================== 정렬 선택 =========================== ");
            System.out.println();
            System.out.println("            [1] 인기순 [2] 가격 내림차순 [3] 가격 오름차순  ");
            System.out.println();
            System.out.println(" =============================================================== ");
            System.out.println();
            System.out.print(" 메뉴 선택 [  ] : ");
            int choice2 = sc.nextInt();

            String type = "";
            String sortOption = "";

            switch (choice) {
                case 1 -> type = "RUNNING";
                case 2 -> type = "FOOTBALL";
                case 3 -> type = "BASKETBALL";
                case 4 -> type = "DAILY";
                case 5 -> type = "SANDAL";
            }
            // 정렬 옵션 매핑
            switch (choice2) {
                case 1 -> sortOption = "popular";
                case 2 -> sortOption = "high";
                case 3 -> sortOption = "low";
            }

            List<ProductVO> products = ProductDAO.productSelect(type, sortOption);
            System.out.println();
            System.out.println(" =================================== ");
            System.out.println();
            for (ProductVO product : products) {
                System.out.println(" 제품명: " + product.getP_name() + ", 가격: " + product.getP_price());
            }
            System.out.println();
            System.out.println(" =================================== ");
            System.out.println();
        }
    }
}
