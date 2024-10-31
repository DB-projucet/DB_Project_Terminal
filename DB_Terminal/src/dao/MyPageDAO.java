package dao;

import common.Common;
import vo.MyPageVO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MyPageDAO {
    Connection conn = null;
    Statement stmt = null;
    PreparedStatement psmt = null;
    ResultSet rs = null;
    Scanner sc = null;

    public static String currentUsedId = MembersDAO.check_id;

    public MyPageDAO() {
        sc = new Scanner(System.in);
    }

    // 로그인 유무 체크
    public boolean isLogIn(String memId) {
        String query = "SELECT * FROM MEMBERSTB WHERE mem_id = ?";
        try {
            conn = Common.getConnection();
            psmt = conn.prepareStatement(query);
            psmt.setString(1, memId);
            rs = psmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
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

    // 내 주문 내역
    public List<MyPageVO> orderSelect(String currentUsedId) {
        List<MyPageVO> list = new ArrayList<>();
        String query = "SELECT * FROM ORDERTB WHERE mem_id = ?";
        try {
            conn = Common.getConnection();
            psmt = conn.prepareStatement(query);
            psmt.setString(1, currentUsedId);
            rs = psmt.executeQuery();

            while (rs.next()) {
                String memId = rs.getString("mem_id");
                String memName = rs.getString("mem_name");
                String memAddr = rs.getString("mem_addr");

                String pType = rs.getString("p_type");
                String pName = rs.getString("p_name");
                String pColor = rs.getString("p_color");
                Integer pSize = rs.getInt("p_size");
                Integer pPrice = rs.getInt("p_price");
                Date ordDStart = rs.getDate("ord_d_start");
                Date ordDEnd = rs.getDate("ord_d_end");

                MyPageVO vo = new MyPageVO(memId, memName, memAddr, pType, pName, pColor, pSize, pPrice, ordDStart, ordDEnd);
                list.add(vo);
            }
        } catch (Exception e) {
            System.out.println(" SELECT 실패");
            e.printStackTrace();
        } finally {
            Common.close(rs);
            Common.close(stmt);
            Common.close(conn);
        }
        return list;
    }

    // 내 정보 조회
    public MyPageVO memberSelect(String memId) {
        MyPageVO vo = null;
        String query = "SELECT * FROM MEMBERSTB WHERE mem_id = ?";
        try {
            conn = Common.getConnection();
            psmt = conn.prepareStatement(query);
            psmt.setString(1, memId);
            rs = psmt.executeQuery();

            if (rs.next()) {
                String memPwd = rs.getString("mem_pwd");
                String memName = rs.getString("mem_name");
                String memEmail = rs.getString("mem_email");
                String memAddr = rs.getString("mem_addr");
                Date memBirth = rs.getDate("mem_birth");
                String memPhone = rs.getString("mem_phone");
                Integer memShsize = rs.getInt("mem_shsize");

                // 비밀번호 값을 출력하여 확인
                System.out.println(" 조회된 비밀번호: '" + memPwd + "'");
                vo = new MyPageVO(memId, memPwd, memName, memEmail, memAddr, memBirth, memPhone, memShsize);
            }
        } catch (Exception e) {
            System.out.println(" SELECT 실패");
            e.printStackTrace();
        } finally {
            Common.close(rs);
            Common.close(stmt);
            Common.close(conn);
        }
        return vo;
    }

    // 내 정보 수정
    public boolean memberUpdate(MyPageVO vo) {
        Integer shSize = vo.getMemShsize();
        if (shSize == null) {
            shSize = 0;
        }

        // 비밀번호가 NULL 또는 비어있지 않은지 체크
        String memPwd = vo.getMemPwd();
        if (memPwd == null || memPwd.trim().isEmpty()) {
            System.out.println(" 비밀번호는 비어있을 수 없습니다.");
            return false; // 또는 적절한 에러 처리를 합니다.
        }

        // 회원 정보 업데이트
        String sqlUpdateMember = "UPDATE MEMBERSTB SET mem_pwd = ?, mem_name = ?, mem_email = ?, mem_addr = ?, mem_birth = ?, mem_phone = ?, mem_shsize = ? WHERE mem_id = ?";

        try {
            conn = Common.getConnection();
            conn.setAutoCommit(false);

            psmt = conn.prepareStatement(sqlUpdateMember);
            psmt.setString(1, vo.getMemPwd());
            psmt.setString(2, vo.getMemName());
            psmt.setString(3, vo.getMemEmail());
            psmt.setString(4, vo.getMemAddr());
            psmt.setDate(5, vo.getMemBirth());
            psmt.setString(6, vo.getMemPhone());
            psmt.setInt(7, vo.getMemShsize());
            psmt.setString(8, vo.getMemId());

            int memberUpdateResult = psmt.executeUpdate();

            if (memberUpdateResult > 0) {
                System.out.println("회원 정보 업데이트 성공");

                // 주문 내역 업데이트
                String sqlUpdateOrders = "UPDATE ORDERTB SET mem_name = ?, mem_addr = ? WHERE mem_id = ?";
                psmt = conn.prepareStatement(sqlUpdateOrders);
                psmt.setString(1, vo.getMemName());
                psmt.setString(2, vo.getMemAddr());
                psmt.setString(3, vo.getMemId());

                int orderUpdateResult = psmt.executeUpdate();

                if (orderUpdateResult > 0) {
                    System.out.println(" 주문 내역 업데이트 성공");
                    System.out.println();
                } else {
                    System.out.println(" 주문 내역 업데이트 실패: 주문 내역이 존재하지 않습니다.");
                    System.out.println();
                }
                conn.commit();
                return true;
            } else {
                System.out.println(" 회원 정보 업데이트 실패");
                System.out.println();
            }

        } catch (Exception e) {
            System.out.println("UPDATE 실패: " + e.getMessage());
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            return false;
        } finally {
            Common.close(psmt);
            Common.close(conn);
        }
        return false;
    }

    // 회원 탈퇴
    public boolean memberDelete(String memId, String memPwd) {
        String sql = "DELETE FROM MEMBERSTB WHERE mem_id = ? AND mem_pwd = ?";
        try {
            conn = Common.getConnection();
            psmt = conn.prepareStatement(sql);
            psmt.setString(1, memId);
            psmt.setString(2, memPwd);
            int rst = psmt.executeUpdate();
            return rst > 0;
        } catch (Exception e) {
            System.out.println(" DELETE 실패");
            e.printStackTrace();
            return false;
        } finally {
            Common.close(psmt);
            Common.close(conn);
        }
    }

    // 주문 내역 출력 메서드
    public void orderSelectResult(List<MyPageVO> list) {
        System.out.println();
        System.out.println("============================== 내 주문 내역 보기 ==============================");
        for (MyPageVO e : list) {
            System.out.println("                주문 ID : " + e.getMemId() + " ");
            System.out.println("                주문자 이름 : " + e.getMemName() + " ");
            System.out.println("                배송지 : " + e.getMemAddr() + " ");
            System.out.println("                제품명 : " + e.getpName() + " ");
            System.out.println("                종류 : " + e.getpType() + " ");
            System.out.println("                색상 : " + e.getpColor() + " ");
            System.out.println("                가격 : " + e.getpPrice() + " ");
            System.out.println("                배송 시작일 : " + e.getOrdDStart() + " ");
            System.out.println("                배송 도착일(예상) : " + e.getOrdDEnd() + " ");
            System.out.println();
        }
        System.out.println("===========================================================================");
        System.out.println();
    }

    // 회원 정보 출력 메서드
    public void memberSelectResult(MyPageVO vo) {
        if (vo != null) {
            System.out.println("============================== 내 정보 보기 ==============================");
            System.out.println();
            System.out.print("아이디 : " + vo.getMemId() + " ");
            System.out.print("비밀번호 : " + vo.getMemPwd() + " ");
            System.out.print("이름 : " + vo.getMemName() + " ");
            System.out.print("이메일 : " + vo.getMemEmail() + " ");
            System.out.print("주소 : " + vo.getMemAddr() + " ");
            System.out.print("생년월일 : " + vo.getMemBirth() + " ");
            System.out.print("연락처 : " + vo.getMemPhone() + " ");
            System.out.print("발 사이즈 : " + vo.getMemShsize() + " ");
            System.out.println();
            System.out.println("=======================================================================");
        } else {
            System.out.println("회원 정보를 찾을 수 없습니다.");
        }
    }

    public static void myPage() {
        Scanner sc = new Scanner(System.in);
        MyPageDAO dao = new MyPageDAO();

        while (true) {
            System.out.println();
            System.out.println("=================================== 마이페이지 ===================================");
            System.out.println();
            System.out.println("    [1] 주문 내역 보기  [2] 내 정보 보기  [3] 내 정보 수정  [4] 로그아웃  [5] 회원 탈퇴 ");
            System.out.println();
            System.out.println("================================================================================");
            System.out.println();
            System.out.print(" 메뉴 선택 [  ] : ");
            int choice = sc.nextInt();
            boolean isSuccess = false;

            switch (choice) {
                case 1:
                    List<MyPageVO> olist = dao.orderSelect(currentUsedId);
                    dao.orderSelectResult(olist);
                    break;
                case 2:
                    MyPageVO memberInfo = dao.memberSelect(currentUsedId);
                    if (memberInfo != null) {
                        System.out.println();
                        System.out.println("============================== 내 정보 보기 ==============================");
                        System.out.println("              아이디 : " + memberInfo.getMemId() + " ");
                        System.out.println("              비밀번호 : " + memberInfo.getMemPwd() + " ");
                        System.out.println("              이름 : " + memberInfo.getMemName() + " ");
                        System.out.println("              이메일 : " + memberInfo.getMemEmail() + " ");
                        System.out.println("              주소 : " + memberInfo.getMemAddr() + " ");
                        System.out.println("              생년월일 : " + memberInfo.getMemBirth() + " ");
                        System.out.println("              연락처 : " + memberInfo.getMemPhone() + " ");
                        System.out.println("              발 사이즈 : " + memberInfo.getMemShsize() + " ");
                        System.out.println();
                        System.out.println("========================================================================");
                        System.out.println();
                    } else {
                        System.out.println(" 회원 정보를 찾을 수 없습니다.");
                        System.out.println();
                    }
                    break;
                case 3:
                    MyPageDAO myPageDAO = new MyPageDAO();
                    MyPageVO existingInfo = myPageDAO.memberSelect(currentUsedId);

                    if (existingInfo != null) {
                        MyPageVO updateInfo = memberUpdateInput();

                        updateInfo.setMemId(currentUsedId);

                        try {
                            isSuccess = dao.memberUpdate(updateInfo);
                            if (isSuccess) {
                                System.out.println(" 내 정보 수정 성공");
                                System.out.println();
                            } else {
                                System.out.println(" 내 정보 수정 실패");
                                System.out.println();
                            }
                        } catch (Exception e) {
                            System.out.println(" 수정 실패" + e.getMessage());
                            System.out.println();
                        }
                    } else {
                        System.out.println(" 사용자 정보를 조회할 수 없습니다.");
                        System.out.println();
                    }
                    break;
                case 4:
                    System.out.println(" 로그아웃 성공");
                    System.out.println();
                    currentUsedId = null;
                    return;
                case 5:
                    System.out.print(" 타인에 의한 탈퇴 방지를 위해 비밀번호를 입력해주세요.");
                    System.out.println();
                    String memPwd = sc.next();

                    isSuccess = dao.memberDelete(currentUsedId, memPwd);
                    if (isSuccess) {
                        System.out.println(" 회원 탈퇴 성공");
                        System.out.println();
                        currentUsedId = null;
                        return;
                    } else {
                        System.out.println(" 회원 탈퇴 실패");
                        System.out.println();
                    }
                    break;
                default:
                    System.out.println(" 잘못된 선택입니다.");
                    System.out.println();
                    break;
            }
        }
    }

    public static MyPageVO memberUpdateInput() {
        Scanner sc = new Scanner(System.in);
        System.out.println();
        System.out.println("============================== 회원 정보 수정 ==============================");
        System.out.println();
        System.out.println(" 아이디는 변경 불가능합니다.");

        String memPwd;
        while (true) {
            System.out.print(" PW (8~16자, 영문 대/소문자, 숫자, 특수문자 가능) : ");
            memPwd = sc.next();
            if (memPwd.length() < 8 || memPwd.length() > 16 || !memPwd.matches("^[a-zA-Z0-9!@#\\$%^&*()_+=-]+$")) {
                System.out.println(" 입력 조건에 맞게 다시 입력하세요.");
                System.out.println();
            } else
                break;
        }

        String memName;
        while (true) {
            System.out.print(" 이름 (영문 대/소문자, 한글 가능) : ");
            memName = sc.next();
            if (!memName.matches("^[a-zA-Z가-힣]+$")) {
                System.out.println(" 입력 조건에 맞게 다시 입력하세요.");
                System.out.println();
            } else
                break;
        }

        String memEmail;
        while (true) {
            System.out.print(" 이메일 주소 (영문 소문자, 숫자 가능) : ");
            memEmail = sc.next();
            if (!memEmail.matches("^[a-z0-9]+@[a-z0-9]+\\.[a-z]+$")) {
                System.out.println(" 이메일 형식이 아니거나 입력 조건에 맞지 않습니다. 다시 입력하세요.");
                System.out.println();
            } else
                break;
        }

        System.out.print(" 주소 : ");
        sc.nextLine();
        String memAddr = sc.nextLine();

        String memBirth;
        while (true) {
            System.out.print(" 생년월일 (YYYY-MM-DD) : ");
            memBirth = sc.next();
            if (!memBirth.matches("^(\\d{4})-(\\d{2})-(\\d{2})$")) {
                System.out.println(" 입력 조건에 맞게 다시 입력하세요.");
                System.out.println();
            } else
                break;
        }

        String memPhone;
        while (true) {
            System.out.print(" 휴대전화번호 ('-' 포함): ");
            memPhone = sc.next();
            if (!memPhone.matches("^0(\\d{2})-(\\d{4})-(\\d{4})$")) {
                System.out.println(" 입력 조건에 맞게 다시 입력하세요.");
                System.out.println();
            } else
                break;
        }

        Integer memShsize = null;
        while (true) {
            System.out.print(" 발 사이즈 (230 ~ 290mm): ");
            memShsize = sc.nextInt();
            if (memShsize < 230 || memShsize > 290 || (memShsize % 10 == 5)) {
                System.out.println(" 입력 가능 범위가 아닙니다. 다시 입력하세요.");
                System.out.println();
            } else
                break;
        }
        return new MyPageVO(memPwd, memName, memEmail, memAddr, java.sql.Date.valueOf(memBirth), memPhone, memShsize);
    }

    public static String memberDeleteInput() {
        Scanner sc = new Scanner(System.in);
        System.out.print(" 타인에 의한 탈퇴 방지를 위해 비밀번호를 입력해주세요.");
        System.out.println();
        return sc.next();
    }
}
