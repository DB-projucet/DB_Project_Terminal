package dao;

import common.Common;
import vo.OrderVO;
import vo.ReviewVO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static dao.OrderDAO.*;

public class ReviewDAO {
    private static Connection conn;

    public ReviewDAO(Connection conn) {
        this.conn = conn;
    }

    static ReviewDAO reviewDAO = new ReviewDAO(conn);

    // [5] 상품 후기 메소드
    public static void showReview() throws SQLException, ClassNotFoundException {
        Scanner sc = new Scanner(System.in);
        int cnt = 0;
        while (true) {
            System.out.println();
            System.out.println("============================== 상품 후기 ==============================");
            System.out.println();
            System.out.println("             [1] 리뷰 조회    [2] 리뷰 작성    [3] 내 리뷰 조회 ");
            System.out.println();
            System.out.println("                            [4] 메인 메뉴   ");
            System.out.println();
            System.out.println("=====================================================================");
            System.out.println();
            System.out.print(" 메뉴 선택 [  ] : ");
            int choice = sc.nextInt();
            boolean T;
            sc.nextLine();
            switch (choice) {
                case 1:
                    displayAllReviews(conn);
                case 2:
                    if (MembersDAO.check==true) {
                        System.out.println();
                        System.out.println(" 현재 [" + MembersDAO.check_id + "] 아이디로 로그인되어있습니다. ");
                        System.out.println();
                        if(doesOrderExistForMember(MembersDAO.check_id) == true){
                            System.out.println(" 해당 ["+ MembersDAO.check_id + "] 아이디로 주문한 내역이 존재합니다.");
                            System.out.println();
                            addReview(reviewDAO);
                        } else{
                            System.out.println(" 해당 ["+ MembersDAO.check_id + "] 아이디로 주문한 내역이 존재하지 않습니다.");
                        }
                    } else {
                        System.out.println();
                        System.out.println(" 로그인 후 상품을 주문할 수 있습니다.");
                        cnt = 1;
                        break;
                    }
                case 3:
                    viewReviews();
                    break;
                case 4:
                    cnt = 1;
                    break;
                default:
                    System.out.println(" 잘못된 값을 입력하셨습니다.");
            }
            if (cnt == 1) { break;}
        }
    }

    public void InsertReview(ReviewVO review) throws SQLException {
        String sql = "INSERT INTO ReviewTB (review_id, mem_id, order_id, buy_date, p_type, p_color, r_content, star_rate) VALUES (REVIEW_ID_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, review.getMem_id());
            pstmt.setInt(2, review.getOrder_id());
            pstmt.setDate(3, review.getBuy_date());
            pstmt.setString(4, review.getP_type());
            pstmt.setString(5, review.getP_color());
            pstmt.setString(6, review.getR_content());
            pstmt.setInt(7, review.getStar_rate());
            pstmt.executeUpdate();
        }
    }

    public static List<ReviewVO> getReviewsByMember(String memId) throws SQLException, ClassNotFoundException {
        List<ReviewVO> reviews = new ArrayList<>();
        // Verify if the connection is open, reinitialize if closed
        if (conn == null || conn.isClosed()) {
            conn = Common.getConnection();
        }
        String query = "SELECT * FROM REVIEWTB WHERE mem_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, memId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ReviewVO review = new ReviewVO(
                        rs.getString("mem_id"),
                        rs.getString("p_type"),
                        rs.getDate("buy_date"),
                        rs.getString("r_content"),
                        rs.getInt("star_rate")
                );
                reviews.add(review);
            }
        }
        return reviews;
    }

    public static void printReviewsByMemberId(String memId) throws SQLException, ClassNotFoundException {
        List<ReviewVO> reviews = getReviewsByMember(memId);
        for (ReviewVO review : reviews) {
            System.out.println(review);
        }
    }

    public List<ReviewVO> getReviews() throws  SQLException {
        List<ReviewVO> reviews = new ArrayList<>();
        String sql = "SELECT * FROM ReviewTB";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                ReviewVO review = new ReviewVO(
                        //rs.getInt("review_id"),
                        rs.getString("mem_id"),
                        rs.getInt("order_id"),
                        rs.getDate("buy_date"),
                        rs.getString("p_type"),
                        rs.getString("p_color"),
                        rs.getString("r_content"),
                        rs.getInt("star_rate")
                );
                reviews.add(review);
            }
        }
        return null;
    }

    public ReviewVO getReview(int reviewId) throws SQLException {
        String sql = "SELECT * FROM ReviewTB WHERE review_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reviewId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new ReviewVO(
                        //rs.getInt("review_id"),
                        rs.getString("mem_id"),
                        rs.getInt("order_id"),
                        rs.getDate("buy_date"),
                        rs.getString("p_type"),
                        rs.getString("p_color"),
                        rs.getString("r_content"),
                        rs.getInt("star_rate")
                );
            }
        }
        return null;
    }

    public void updateReview(ReviewVO review) throws SQLException {
        String sql = "UPDATE ReviewTB SET r_content = ?, star_rate = ? WHERE review_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, review.getR_content());
            pstmt.setInt(2, review.getStar_rate());
            pstmt.setInt(3, review.getReview_id());
            pstmt.executeUpdate();
        }
    }

    public void deleteReview(int review_id) throws SQLException {
        String sql = "DELETE FROM ReviewTB WHERE reviewId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, review_id);
            pstmt.executeUpdate();
        }
    }

    public static void viewReviews() throws SQLException, ClassNotFoundException {
        String mem_id = MembersDAO.check_id;
        printReviewsByMemberId(mem_id);

    }

    public static boolean doesOrderExistForMember(String mem_id) {
        String sql = "SELECT order_id FROM OrderTB WHERE mem_id = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "SCOTT", "TIGER");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, mem_id);
            ResultSet rs = pstmt.executeQuery();

            // Check if any order_id exists for the given mem_id
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean doesOrderExistForOrder(int order_id, String mem_id) {
        String sql = "SELECT order_id FROM OrderTB WHERE order_id = ? AND mem_id = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "SCOTT", "TIGER");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, order_id);
            pstmt.setString(2, mem_id);
            ResultSet rs = pstmt.executeQuery();

            // Check if any order_id exists for the given mem_id
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String[] getOrderProductDetails(int order_id, Connection conn) throws SQLException {
        String p_type = null;
        String p_color = null;

        String query = "SELECT p_type, p_color FROM OrderTB WHERE order_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, order_id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    p_type = rs.getString("p_type");
                    p_color = rs.getString("p_color");
                }
            }
        }
        return new String[]{p_type, p_color};  // Return as an array
    }

    public static void displayAllReviews(Connection conn) throws SQLException {
        String query = "SELECT mem_id, p_type, buy_date, r_content, star_rate FROM ReviewTB";

        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println();
            while (rs.next()) {
                String mem_id = rs.getString("mem_id");
                String p_type = rs.getString("p_type");
                Date buy_date = rs.getDate("buy_date");
                String r_content = rs.getString("r_content");
                int star_rate = rs.getInt("star_rate");

                // Display the review details
                System.out.println("< 작성자 ID : " + mem_id + "   구매 품목 : " + p_type + "  구매 일자: " + buy_date + ">");
                System.out.println("=============================== 리뷰 ===============================");
                System.out.println();
                System.out.println(" 내용 :  " + r_content);
                System.out.println();
                System.out.println("===================================================================");
                System.out.println("< 별점 : " + star_rate + " >");
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(" 리뷰를 불러오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    public static void addReview(ReviewDAO reviewDAO) throws SQLException, ClassNotFoundException {
        Scanner sc = new Scanner(System.in);
        String mem_id = MembersDAO.check_id;
        int order_id;
        String[] productDetails;
        String p_type;
        String p_color;


        System.out.println();
        System.out.println("============================== < 주문 목록 > ==============================");
        System.out.println();

        printOrdersByMemberId(mem_id);
        System.out.println();

        while (true) {
            System.out.println();
            System.out.print(" 주문 ID 입력 : ");
            order_id = sc.nextInt();
            sc.nextLine();

            if (doesOrderExistForOrder(order_id, mem_id) == true){
                System.out.println(" 주문 내역에 존재하는 주문 ID 입니다.");
                System.out.println();
                productDetails = getOrderProductDetails(order_id, conn);
                p_type = productDetails[0];
                p_color = productDetails[1];
                break;
            } else {
                System.out.println(" 해당 주문 ID에 대한 주문내역이 존재하지 않습니다. 다시 입력하세요.");
                System.out.println();
            }
        }
        System.out.println("======================== 리뷰 작성하기 ========================");
        System.out.println();
        System.out.print("    내용 입력 (500자 이내) : ");
        String r_content = sc.nextLine();
        System.out.println();
        System.out.println("============================================================");
        System.out.println();

        System.out.println();
        System.out.println("============================ 별점 ============================");
        System.out.println();
        System.out.print("                입력 (1 ~ 5 점) : ");
        int star_rate = sc.nextInt();
        if (star_rate < 1 || star_rate > 5) {
            System.out.println(" 1 ~ 5 사이의 정수값으로 입력해주세요. ");
            return;
        }
        System.out.println();
        System.out.println("=============================================================");

        // 구매날짜를 자동으로 입력
        Date buy_date = Date.valueOf(java.time.LocalDate.now());

        ReviewVO newReview = new ReviewVO(mem_id, order_id, buy_date, p_type, p_color, r_content, star_rate);
        reviewDAO.InsertReview(newReview);

        System.out.println();
        System.out.println(" 리뷰가 성공적으로 추가되었습니다.");
    }

    public static boolean isMemberExists(Connection conn, String mem_id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM membersTB WHERE mem_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, mem_id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public static int getNextReview_id(Connection conn) throws SQLException {
        String sql = "SELECT NVL(MAX(review_id), 0) + 1 FROM ReviewTB";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 1;
    }

    private static boolean isOrderExists(Connection conn, int order_id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM OrderTB WHERE order_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, order_id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    private static void showReviewHistory(Connection conn, String mem_id) throws SQLException {
        String sql = "SELECT * FROM ReviewTB WHERE mem_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, mem_id);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("리뷰 내역:");
            while (rs.next()) {
                int review_id = rs.getInt("review_id");
                int order_id = rs.getInt("order_id");
                Date buy_date = rs.getDate("buy_date");
                String p_type = rs.getString("p_type");
                String p_color = rs.getString("p_color");
                String r_content = rs.getString("r_content");
                int star_rate = rs.getInt("star_rate");

                System.out.printf("리뷰 ID: %d, 주문 ID: %d, 구매 날짜: %s, 제품 종류: %s, 색상: %s, 내용: %s, 별점: %d%n",
                        review_id, order_id, buy_date, p_type, p_color, r_content, star_rate);
            }
        }
    }
}