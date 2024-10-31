import dao.*;
import vo.MembersVO;
import vo.MyPageVO;
import vo.OrderVO;
import vo.ProductVO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.time.LocalDate;
//import java.util.Calendar;;['']
import java.util.*;

import static dao.MembersDAO.*;
import static dao.MyPageDAO.myPage;
import static dao.OrderDAO.orderProduct;
import static dao.ProductDAO.menuSelect;
import static dao.ReviewDAO.showReview;
import static dao.ViewDAO.viewProduct;
import static java.lang.String.valueOf;
import static java.lang.System.exit;


public class Main {

    public static String currentUsedId;

    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);

          // 자동 커밋 설정
        // Oracle 데이터베이스 연결 설정
        String jdbcURL = "jdbc:oracle:thin:@localhost:1521:xe";  // Oracle DB URL
        String dbUser = "SCOTT";  // Oracle DB 사용자 이름
        String dbPassword = "TIGER";  // Oracle DB 비밀번호


        Connection conn = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
        conn.setAutoCommit(true);

        // 데이터베이스 연결을 위한 Connection 객체 생성
        try (conn) {
            if (conn != null) {
                System.out.println("Oracle 데이터베이스 연결 성공!");
            } else {
                System.out.println("데이터베이스 연결 실패!");
                return;
            }

            // MembersDAO와 OrderDAO에 Connection 전달
            OrderDAO orderDAO = new OrderDAO(conn);
            MembersDAO membersDAO = new MembersDAO(conn);  // Connection 객체 전달
            ProductDAO productDAO = new ProductDAO(conn);
            ReviewDAO reviewDAO = new ReviewDAO(conn);

            menu();  // 메뉴 메서드 실행

        } catch (SQLException e) {
            System.err.println(" 데이터베이스 연결 오류: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean check = false;
    public static String check_id = null;

    public static void menu() throws SQLException, ClassNotFoundException {
        MembersDAO dao = new MembersDAO();
        Scanner sc = new Scanner(System.in);
        boolean isSuccess = false;
        while (true) {
            System.out.println();
            System.out.println("============ 신발 주문 프로그램 ============");
            System.out.println();
            System.out.println("          [1] 로그인 / 회원가입            ");
            System.out.println("          [2] 상품 둘러보기               ");
            System.out.println("          [3] 상품 주문                  ");
            System.out.println("          [4] 상품 추천                  ");
            System.out.println("          [5] 상품 후기                  ");
            System.out.println("          [6] 마이페이지                  ");
            System.out.println();
            System.out.println("========================================");
            System.out.println();
            System.out.print(" 메뉴 선택 [  ] : ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    signUpIn();
                    break;
                case 2:
                    viewProduct();
                    break;
                case 3:
                    if (MembersDAO.check==true) {
                        System.out.println();
                        System.out.println(" 현재 [" + MembersDAO.check_id + "] 아이디로 로그인되어있습니다. ");
                        System.out.println();
                        orderProduct();  // 주문 처리 메서드 호출
                    } else {
                        System.out.println();
                        System.out.println(" 로그인 후 상품을 주문할 수 있습니다.");
                    }
                    break;
                case 4:
                    menuSelect();
                    break;
                case 5:
                    showReview();
                    break;
                case 6:
                    if (MembersDAO.check_id != null){
                        myPage();
                    } else{
                        break;
                    }
                default:
                    System.out.println(" 잘못 입력하셨습니다.");
                    break;
            }
        }
    }
}

