package dao;

import common.Common;
import vo.MembersVO;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static java.lang.System.exit;

public class MembersDAO {
    private Connection conn;
    Statement stmt = null;
    PreparedStatement psmt = null;
    ResultSet rs = null;
    Scanner sc = null;

    public static String currentUsedId;

    public static boolean check = false;
    public static String check_id = null;

    public MembersDAO() {
        sc = new Scanner(System.in);
    }

    public MembersDAO(Connection conn) {
        this.conn = conn;
    }

    private boolean loggedIn = false; // 로그인 상태
    private String currentUsername; // 현재 로그인된 사용자
    private Map<String, String> members = new HashMap<>(); // 사용자 정보 저장 (예시)

    public MembersVO getMemberInfoById(String memId) throws SQLException {
        String query = "SELECT mem_name, mem_addr FROM MembersTB WHERE mem_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, memId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String memName = rs.getString("mem_name");
                    String memAddr = rs.getString("mem_addr");
                    return new MembersVO(memName, memAddr);
                } else {
                    return null;  // 해당 ID를 가진 회원이 없을 경우
                }
            }
        }
    }


    public boolean memberTbSignup(MembersVO vo) {
        String sql = "INSERT INTO MEMBERSTB (MEMBER_id, mem_id, mem_pwd, mem_name, mem_email, mem_addr, mem_birth, mem_phone, mem_shsize) " +
                "VALUES (MEMBER_ID_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            conn = Common.getConnection();
            psmt = conn.prepareStatement(sql);
//            psmt.setInt(1, vo.getMemberId());
            psmt.setString(1, vo.getMemId());
            psmt.setString(2, vo.getMemPwd());
            psmt.setString(3, vo.getMemName());
            psmt.setString(4, vo.getMemEmail());
            psmt.setString(5, vo.getMemAddr());
            psmt.setDate(6, vo.getMemBirth());
            psmt.setString(7, vo.getMemPhone());
            psmt.setInt(8, vo.getMemShsize());

/*            Integer shSize = vo.getMemShsize();
            if (shSize != null) {
                psmt.setInt(8, shSize);
            } else {
                throw new IllegalArgumentException("발 사이즈는 null 불가능");
            }*/

            int rst = psmt.executeUpdate();
            return true;
        } catch (Exception e) {
            System.out.println("INSERT 실패");
            e.printStackTrace();
            return false;
        } finally {
            Common.close(psmt);
            Common.close(conn);
        }
    }

    public static MembersVO memberSignUp() {
        Scanner sc = new Scanner(System.in);
        System.out.println("============== 회원가입 ==============");
        String memId;

        MembersDAO membersDAO = new MembersDAO();

        while (true) {
            System.out.print(" ID (5~10자, 영문 소문자, 숫자, 특수문자 가능) : ");
            memId = sc.next();

            if (memId.length() < 5 || memId.length() > 10 || !memId.matches("^[a-z0-9!@#\\$%^&*()_+=-]+$")) {
                System.out.println(" 입력 조건에 맞게 다시 입력하세요.");
                continue;
            }

            if (membersDAO.checkMemId(memId)) {
                System.out.println(" 이미 사용 중인 ID입니다. 다른 ID를 입력하세요.");
                continue;
            }
            break;
        }

        String memPwd;
        while (true) {
            System.out.print(" PW (8~16자, 영문 대/소문자, 숫자, 특수문자 가능) : ");
            memPwd = sc.next();
            if (memPwd.length() < 8 || memPwd.length() > 16 || !memPwd.matches("^[a-zA-Z0-9!@#\\$%^&*()_+=-]+$")) {
                System.out.println(" 입력 조건에 맞게 다시 입력하세요.");
            } else
                break;
        }

        String memName;
        while (true) {
            System.out.print(" 이름 (영문 대/소문자, 한글 가능) : ");
            memName = sc.next();
            if (!memName.matches("^[a-zA-Z가-힣]+$")) {
                System.out.println(" 입력 조건에 맞게 다시 입력하세요.");
            } else
                break;
        }

        String memEmail;
        while (true) {
            System.out.print(" 이메일 주소 (영문 소문자, 숫자 가능): ");
            memEmail = sc.next();
            if (!memEmail.matches("^[a-z0-9]+@[a-z0-9]+\\.[a-z]+$")) {
                System.out.println(" 이메일 형식이 아니거나 입력 조건에 맞지 않습니다. 다시 입력하세요.");
            } else
                break;
        }

        System.out.print(" 주소 : ");
        String memAddr = sc.next();

        String memBirth;
        while (true) {
            System.out.print(" 생년월일 (YYYY-MM-DD) : ");
            sc.nextLine();
            memBirth = sc.next();
            if (!memBirth.matches("^(\\d{4})-(\\d{2})-(\\d{2})$")) {
                System.out.println(" 입력 조건에 맞게 다시 입력하세요.");
            } else
                break;
        }

        String memPhone;
        while (true) {
            System.out.print(" 휴대전화번호 ('-' 포함): ");
            memPhone = sc.next();
            if (!memPhone.matches("^0(\\d{2})-(\\d{4})-(\\d{4})$")) {
                System.out.println(" 입력 조건에 맞게 다시 입력하세요.");
            } else
                break;
        }

/*        Integer memShsize = null;
        while (true) {
            System.out.print("발 사이즈 (230 ~ 290mm): ");
//            memShsize = sc.nextInt();
            if (sc.hasNextInt()) {
                memShsize = sc.nextInt();
                if (memShsize < 230 || memShsize > 290 || (memShsize % 10 == 5)) {
                    System.out.println("입력 가능 범위가 아닙니다. 다시 입력하세요.");
                } else {
                    break;
                }
            } else {
                System.out.println("정수를 입력하세요.");
                sc.next();
            }
        }*/

        Integer memShsize;
        while (true) {
            System.out.print(" 발 사이즈 (230 ~ 290mm): ");
            memShsize = sc.nextInt();
            if (memShsize < 230 || memShsize > 290 || (memShsize % 10 == 5)) {
                System.out.println(" 입력 가능 범위가 아닙니다. 다시 입력하세요.");
            } else
                break;
        }
        return new MembersVO(memId, memPwd, memName, memEmail, memAddr, Date.valueOf(memBirth), memPhone, memShsize);
    }

    public static MembersVO memberSignIn() {
        Scanner sc = new Scanner(System.in);
        System.out.println();
        System.out.println("================ 로그인 ================");
        System.out.println();
        String memId;
        String memPwd;

        MembersDAO membersDAO = new MembersDAO();

        while (true) {
            System.out.print("             ID : ");
            memId = sc.next();
            System.out.print("             PW : ");
            memPwd = sc.next();

            System.out.println();
            System.out.println("======================================");
            if (!membersDAO.checkMem(memId, memPwd)) {
                System.out.println();
                System.out.println(" 아이디 또는 비밀번호가 잘못 되었습니다. 다시 입력해 주세요.");
            } else
                break;
        }
        return new MembersVO(memId, memPwd);
    }

    public boolean checkMemId(String memId) {
        String sql = "SELECT COUNT(*) FROM MEMBERSTB WHERE mem_id = ?";
        try {
            conn = Common.getConnection();
            psmt = conn.prepareStatement(sql);
            psmt.setString(1, memId);
            rs = psmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Common.close(rs);
            Common.close(psmt);
            Common.close(conn);
        }
        return false;
    }

    public boolean checkMem(String memId, String memPwd) {
        String sql = "SELECT COUNT(*) FROM MEMBERSTB WHERE mem_id = ? AND mem_pwd = ?";
        try {
            conn = Common.getConnection();
            psmt = conn.prepareStatement(sql);
            psmt.setString(1, memId);
            psmt.setString(2, memPwd);
            rs = psmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Common.close(rs);
            Common.close(psmt);
            Common.close(conn);
        }
        return false;
    }

    // [1] 회원가입 / 로그인 메소드
    public static void signUpIn() {
        Scanner sc = new Scanner(System.in);
        MembersDAO dao = new MembersDAO();

        while (true) {
            System.out.println();
            System.out.println("============ 회원가입 / 로그인 ============");
            System.out.println();
            System.out.println("   [1] 회원가입  [2] 로그인  [3] 메인화면    ");
            System.out.println();
            System.out.println("=======================================");
            System.out.println();
            System.out.print(" 메뉴 선택 [  ] : ");
            int choice = sc.nextInt();
            boolean isSuccess = false;
            switch (choice) {
                case 1:
                    isSuccess = dao.memberTbSignup(memberSignUp());
                    if (isSuccess) System.out.println("회원가입 성공");
                    else System.out.println("회원가입 실패");
                    break;
                case 2:
                    MembersVO loginInfo = memberSignIn();
                    check_id = loginInfo.getMemId();
                    isSuccess = dao.checkMem(loginInfo.getMemId(), loginInfo.getMemPwd());
                    //if (isSuccess) { check = true; }
                    if (isSuccess){
                        check = true;
                        System.out.println();
                        System.out.println("["+ check_id + "] 아이디로 로그인에 성공했습니다!!");
                        System.out.println();
                        while(true) {
                            System.out.print(" 전 메뉴로 돌아가시겠습니까? [1] 예  [2] 아니오 (프로그램 종료) : ");
                            int num = sc.nextInt();
                            if (num == 1) {
                                break;
                            } else if (num == 2) {
                                exit(0);
                            }
                            System.out.println("잘못된 값을 입력하셨습니다.");
                        }
                    }
                    else System.out.println("로그인 실패");
                    break;
                case 3:
                    return;
                default:
                    System.out.println("잘못된 선택입니다.");
                    break;
            }
            if (currentUsedId != null)
                break;
        }
    }
}
