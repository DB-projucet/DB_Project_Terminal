package dao;

import common.Common;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleDatabaseMetaData;
import vo.OrderVO;

import java.sql.*;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;


import static common.Common.getConnection;

public class OrderDAO {
    private static Connection conn;

    public OrderDAO(Connection conn) {
        OrderDAO.conn = conn;
    }
    static PreparedStatement psmt;


    public static boolean check = false;
    public static String check_id = null;

    // 주문 추가 메서드
    public static OrderVO addOrder(OrderVO order) throws SQLException, ClassNotFoundException {
        OrderVO resultOrder = null;

        try {
            conn = getConnection();
            if (conn == null) {
                throw new SQLException("Failed to obtain database connection.");
            }

            conn.setAutoCommit(false);

            Integer productId = getProductID(order.getpType(), order.getpName(), order.getpColor(), order.getpSize(), order.getpPrice());
            if (productId == null) {
                throw new SQLException("The specified product does not exist in ProductTB. Cannot add order.");
            }

            // Check product stock
            String checkStockQuery = "SELECT p_cnt FROM ProductTB WHERE product_id = ?";
            try (PreparedStatement stockStmt = conn.prepareStatement(checkStockQuery)) {
                stockStmt.setInt(1, productId);
                ResultSet rs = stockStmt.executeQuery();

                if (rs.next() && rs.getInt("p_cnt") > 1) {
                    // Decrement stock
                    String updateStockQuery = "UPDATE ProductTB SET p_cnt = p_cnt - 1 WHERE product_id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateStockQuery)) {
                        updateStmt.setInt(1, productId);
                        updateStmt.executeUpdate();
                    }

                    // Insert order
                    String insertOrderQuery = "INSERT INTO OrderTB (order_id, product_id, mem_id, mem_name, mem_addr, p_type, p_name, p_color, p_size, p_price, ord_d_start, ord_d_end) "
                            + "VALUES (ORDER_ID_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(insertOrderQuery)) {
                        pstmt.setInt(1, productId);
                        pstmt.setString(2, order.getMemId());
                        pstmt.setString(3, order.getMemName());
                        pstmt.setString(4, order.getMemAddr());
                        pstmt.setString(5, order.getpType());
                        pstmt.setString(6, order.getpName());
                        pstmt.setString(7, order.getpColor());
                        pstmt.setInt(8, order.getpSize());
                        pstmt.setInt(9, order.getpPrice());
                        pstmt.setTimestamp(10, order.getOrdStartTime());

                        if (order.getOrdEndTime() != null) {
                            pstmt.setDate(11, new java.sql.Date(order.getOrdEndTime().getTime()));
                        } else {
                            pstmt.setDate(11, null);
                        }

                        pstmt.executeUpdate();
                    }

                    // Retrieve the latest order_id for the current session
                    String selectOrderIdQuery = "SELECT ORDER_ID_SEQ.CURRVAL AS order_id FROM dual";
                    try (PreparedStatement idStmt = conn.prepareStatement(selectOrderIdQuery);
                         ResultSet idRs = idStmt.executeQuery()) {
                        if (idRs.next()) {
                            conn.commit();

                            // Setting orderId in OrderVO
                            resultOrder = new OrderVO(order.getMemId(), order.getMemName(), order.getMemAddr(), order.getpType(), order.getpName(), order.getpColor(), order.getpSize(), order.getpPrice(), order.getOrdStartTime(), order.getOrdEndTime());
                        } else {
                            throw new SQLException("Creating order failed, no ID obtained.");
                        }
                    }
                } else {
                    throw new SQLException("해당 제품의 재고가 부족합니다.");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            if (conn != null) {
                conn.close();
            }
        }

        return resultOrder;
    }

    // 주문 조회 메서드
    public static List<OrderVO> getOrdersByMember(String memId) throws SQLException, ClassNotFoundException {
        List<OrderVO> orders = new ArrayList<>();
        // Verify if the connection is open, reinitialize if closed
        if (conn == null || conn.isClosed()) {
            conn = Common.getConnection();
        }
        String query = "SELECT * FROM OrderTB WHERE mem_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, memId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                OrderVO order = new OrderVO(
                        rs.getInt("order_id"),
                        rs.getString("mem_id"),
                        rs.getString("mem_name"),
                        rs.getString("mem_addr"),
                        rs.getString("p_type"),
                        rs.getString("p_name"),
                        rs.getString("p_color"),
                        rs.getInt("p_size"),
                        rs.getInt("p_price"),
                        rs.getTimestamp("ord_d_start"),
                        rs.getDate("ord_d_end")
                );
                orders.add(order);
            }
        }
        return orders;
    }

    public static void printOrdersByMemberId(String memId) throws SQLException, ClassNotFoundException {
        List<OrderVO> orders = getOrdersByMember(memId);
        for (OrderVO order : orders) {
            System.out.println(order);
        }
    }

    // 주문 삭제 메서드
    public static void deleteOrder(int orderId) throws SQLException {
        String query = "DELETE FROM OrderTB WHERE order_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, orderId);
            pstmt.executeUpdate();
        }
    }

    public static void updateOrderProductIds(String type) {
        try {
            conn = Common.getConnection();

            // Update OrderTB's product_id by finding matching p_type and p_name
            String updateQuery = "UPDATE OrderTB " +
                    "SET product_id = (SELECT product_id FROM ProductTB WHERE p_type = ? AND p_name = OrderTB.p_name AND ROWNUM = 1) " +
                    "WHERE product_id IS NULL";

            psmt = conn.prepareStatement(updateQuery);
            psmt.setString(1, type);
            int rowsUpdated = psmt.executeUpdate();

            System.out.println("Rows updated: " + rowsUpdated);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Common.close(psmt, conn);
        }
    }

    // Check if product exists in ProductTB
    public static boolean productExists(String pType, String pName, String pColor, int pSize, int pPrice) throws SQLException {
        String query = "SELECT COUNT(*) FROM ProductTB WHERE p_type = ? AND p_name = ? AND p_color = ? AND p_size = ? AND p_price = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, pType);
            pstmt.setString(2, pName);
            pstmt.setString(3, pColor);
            pstmt.setInt(4, pSize);
            pstmt.setInt(5, pPrice);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;  // Returns true if product exists
            }
        }
        return false;
    }

    // Method to get memName and memAddr based on mem_id
    public static String[] getMemberDetails(String memId) {
        String memName = null;
        String memAddr = null;

        try {
            if (conn == null || conn.isClosed()) {
                conn = Common.getConnection();  // Ensure that `conn` is initialized properly
            }

            // SQL query to get mem_name and mem_addr from MembersTB where mem_id matches
            String query = "SELECT mem_name, mem_addr FROM MembersTB WHERE mem_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, memId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    memName = rs.getString("mem_name");
                    memAddr = rs.getString("mem_addr");
                }
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return new String[]{memName, memAddr};
    }


    public static Integer getProductID(String pType, String pName, String pColor, int pSize, int pPrice) throws SQLException {
        Integer productId = 0;
        String query = "SELECT product_id FROM ProductTB WHERE p_type = ? AND p_name = ? AND p_color = ? AND p_size = ? AND p_price = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, pType);
            pstmt.setString(2, pName);
            pstmt.setString(3, pColor);
            pstmt.setInt(4, pSize);
            pstmt.setInt(5, pPrice);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    productId = rs.getInt("product_id");
                }
            }
        }
        return productId;
    }

    ResultSet rs = null;
    Statement stmt = null;
    public List<OrderVO>OrderSelect( String a ,String b ) {
        List<OrderVO> list = new ArrayList<>();
        try {
            conn = getConnection(); // 오라클 DB 연결
            stmt = conn.createStatement(); // statement 생성

            // executeQuery : select 문과 같이 결과값이 여러 개의 레코드로 반환되는 경우 사용
            // ResultSet : 여러 행의 결과값을 받아서 반복자 제공
            // next() : 현재 행에서 한 행 앞으로 이동
            // previous() : 현재 행에서 한 행 뒤로 이동
            // first() : 첫 번째 행으로 이동
            // last() : 마지막 행으로 이동
            while (rs.next()) {
                String P_name= rs.getString("P_name");
                int  P_price = rs.getInt(" P_price");
                OrderVO vo = new OrderVO( P_name, P_price );
                list.add(vo);
            }
            Common.close(rs);
            Common.close(stmt);
            Common.close(conn);

        } catch (Exception e) {
            System.out.println("EMP 조회 실패 !!!!");
        }
        return list;
    }

    // [3] 상품 주문 메소드
    public static void orderProduct() throws SQLException, ClassNotFoundException {
        Scanner sc = new Scanner(System.in);
        String pType = "";
        String pName = "";
        String pColor = "";
        int pPrice = 0;
        int pSize = 0;

        while (true) {
            System.out.println();
            System.out.println("======================== 상품 주문 ========================");
            System.out.println();
            System.out.println("                     [1] 주문 추가 ");
            System.out.println("                     [2] 주문 조회 ");
            System.out.println("                     [3] 메인 화면 ");
            System.out.println();
            System.out.println("=========================================================");
            System.out.println();
            System.out.print(" 메뉴 선택 [  ] : ");
            int choice = sc.nextInt();

            String memId;
            if (choice == 1) {
                boolean invalidSelection = false;  // Flag to check invalid selections

                // Gather order details
                System.out.println();
                System.out.println("======================== 신발 카테고리 선택 ========================");
                System.out.println();
                System.out.println("    [1][러닝화]  [2][일상화]  [3][농구화]  [4][축구화]  [5][샌들]");
                System.out.println();
                System.out.println("================================================================");
                System.out.println();
                System.out.print(" 메뉴 선택 [  ] : ");
                int num = sc.nextInt();

                ProductDetails details = switch (num) {
                    case 1 -> setProductDetails(sc, "R-1", 149000, "R-2", 147000, "R-3", 150000);
                    case 2 -> setProductDetails(sc, "D-1", 119000, "D-2", 117000, "D-3", 120000);
                    case 3 -> setProductDetails(sc, "B-1", 179000, "B-2", 176000, "B-3", 180000);
                    case 4 -> setProductDetails(sc, "F-1", 119000, "F-2", 117000, "F-3", 120000);
                    case 5 -> setProductDetails(sc, "S-1", 89000, "S-2", 87000, "S-3", 90000);
                    default -> {
                        System.out.println(" 잘못된 카테고리 선택입니다.");
                        invalidSelection = true;
                        yield new ProductDetails(null, 0);
                    }
                };

                if (invalidSelection || details.name == null) continue;  // Skip if invalid selection

                pType = switch (num) {
                    case 1 -> "RUNNING";
                    case 2 -> "DAILY";
                    case 3 -> "BASKETBALL";
                    case 4 -> "FOOTBALL";
                    case 5 -> "SANDAL";
                    default -> "";
                };

                pName = details.name;
                pPrice = details.price;

                System.out.println();
                System.out.println("======================== 신발 색상 선택 ========================");
                System.out.println();
                System.out.println("            [1] RED  [2] BLUE  [3] BLACK  [4] WHITE");
                System.out.println();
                System.out.println("=============================================================");
                System.out.println();
                System.out.print(" 메뉴 선택 [  ] : ");
                int colorChoice = sc.nextInt();
                pColor = switch (colorChoice) {
                    case 1 -> "RED";
                    case 2 -> "BLUE";
                    case 3 -> "BLACK";
                    case 4 -> "WHITE";
                    default -> {
                        System.out.println(" 잘못된 색상 선택입니다.");
                        invalidSelection = true;
                        yield "";
                    }
                };
                if (invalidSelection) continue;  // Skip further processing if invalid selection

                System.out.println();
                System.out.println("======================== 신발 사이즈 선택 ========================");
                System.out.println();
                System.out.println("              [1] 230  [2] 240  [3] 250  [4] 260  ");
                System.out.println("                 [5] 270  [6] 280  [7] 290");
                System.out.println();
                System.out.println("===============================================================");
                System.out.println();
                System.out.print(" 메뉴 선택 [  ] : ");
                int sizeChoice = sc.nextInt();
                pSize = switch (sizeChoice) {
                    case 1 -> 230;
                    case 2 -> 240;
                    case 3 -> 250;
                    case 4 -> 260;
                    case 5 -> 270;
                    case 6 -> 280;
                    case 7 -> 290;
                    default -> {
                        System.out.println(" 잘못된 사이즈 선택입니다.");
                        invalidSelection = true;
                        yield 0;
                    }
                };
                if (invalidSelection) continue;  // Skip further processing if invalid selection

                // Proceed to create the order if the product exists
                memId = MembersDAO.check_id;
                String[] memberDetails = getMemberDetails(memId);
                String memName = memberDetails[0];
                String memAddr = memberDetails[1];

                java.util.Date utilDate = new java.util.Date();
                java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(utilDate.getTime());
                Timestamp ordDStart = Timestamp.from(Instant.now());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(ordDStart);
                calendar.add(Calendar.DAY_OF_MONTH, 7);
                java.sql.Date ordDEnd = new java.sql.Date(calendar.getTime().getTime());

                OrderVO order = new OrderVO(memId, memName, memAddr, pType, pName, pColor, pSize, pPrice, ordDStart, ordDEnd);

                try {
                    addOrder(order);
                    System.out.println(" 주문이 추가되었습니다.");
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(" 주문 추가 중 오류가 발생했습니다: " + e.getMessage());
                }
            } else if (choice == 2) {
                // 주문 조회
                List<OrderVO> orders = OrderDAO.getOrdersByMember(MembersDAO.check_id);
                for (OrderVO order : orders) {
                    System.out.println(order);
                }

            } else if (choice == 3) {
                //System.out.println("프로그램 종료");
                break;
            }
//            else if (choice == 4) {
//                // 주문 삭제
//                System.out.println("삭제할 주문 ID 입력: ");
//                int orderId = sc.nextInt();
//                OrderDAO.deleteOrder(orderId);
//                System.out.println("주문이 삭제되었습니다.");
//            }
        }
    }


    // Helper method to handle product model selection and price assignment
    private static ProductDetails setProductDetails(Scanner sc, String model1, int price1, String model2, int price2, String model3, int price3) {
        System.out.println();
        System.out.println("======================== 신발 모델 선택 ========================");
        System.out.println();
        System.out.println("                [1] " + model1 + "  [2] " + model2 + "  [3] " + model3);
        System.out.println();
        System.out.println("=============================================================");
        System.out.println();
        System.out.print(" 메뉴 선택 [  ] : ");
        int modelChoice = sc.nextInt();

        return switch (modelChoice) {
            case 1 -> new ProductDetails(model1, price1);
            case 2 -> new ProductDetails(model2, price2);
            case 3 -> new ProductDetails(model3, price3);
            default -> {
                System.out.println(" 잘못된 모델 선택입니다.");
                yield new ProductDetails(null, 0);  // Null name to signify invalid choice
            }
        };
    }

    static class ProductDetails {
        String name;
        int price;

        public ProductDetails(String name, int price) {
            this.name = name;
            this.price = price;
        }
    }

}