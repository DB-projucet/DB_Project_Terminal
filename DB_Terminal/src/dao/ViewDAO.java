package dao;

import java.util.Scanner;

import static java.lang.System.exit;

public class ViewDAO {

    public static void viewProduct() {
        Scanner sc = new Scanner(System.in);
        System.out.println();
        System.out.println("========================= 상품 목록 =========================");
        System.out.println();
        System.out.println("   [1][러닝화]  [2][일상화]  [3][농구화]  [4][축구화]  [5][샌들]   ");
        System.out.println();
        System.out.println("                       [6] 돌아가기                          ");
        System.out.println();
        System.out.println("===========================================================");
        System.out.println();

        System.out.print("번호 입력 [  ] : ");
        int n_s = sc.nextInt();
        switch (n_s) {
            case 1:
                System.out.println("=================== [러닝화 / RUNNING] ====================");
                System.out.println();
                System.out.println("        [1][R - 1]    [2][R - 2]    [3][R - 3]           ");
                System.out.println();
                System.out.println("=========================================================");
                System.out.println();
                while(true) {
                    System.out.print(" 전 메뉴로 돌아가시겠습니까? [1] 예  [2] 아니오 (프로그램 종료) : ");
                    int num = sc.nextInt();
                    if (num == 1) {
                        break;
                    } else if (num == 2) {
                        exit(0);
                    }
                    System.out.println(" 잘못된 값을 입력하셨습니다.");
                }
                return;
            case 2:
                System.out.println("==================== [일상화 / DAILY] ====================");
                System.out.println();
                System.out.println("        [1][D - 1]    [2][D - 2]    [3][D - 3]           ");
                System.out.println();
                System.out.println("=========================================================");
                System.out.println();
                while(true) {
                    System.out.print(" 전 메뉴로 돌아가시겠습니까? [1] 예  [2] 아니오 (프로그램 종료) : ");
                    int num = sc.nextInt();
                    if (num == 1) {
                        break;
                    } else if (num == 2) {
                        exit(0);
                    }
                    System.out.println(" 잘못된 값을 입력하셨습니다.");
                }
                return;
            case 3:
                System.out.println("================== [농구화 / BASKETBALL] ==================");
                System.out.println();
                System.out.println("        [1][B - 1]    [2][B - 2]    [3][B - 3]           ");
                System.out.println();
                System.out.println("=========================================================");
                System.out.println();
                while(true) {
                    System.out.print(" 전 메뉴로 돌아가시겠습니까? [1] 예  [2] 아니오 (프로그램 종료) : ");
                    int num = sc.nextInt();
                    if (num == 1) {
                        break;
                    } else if (num == 2) {
                        exit(0);
                    }
                    System.out.println(" 잘못된 값을 입력하셨습니다.");
                }
                return;
            case 4:
                System.out.println("=================== [축구화 / FOOTBALL] ===================");
                System.out.println();
                System.out.println("        [1][F - 1]    [2][F - 2]    [3][F - 3]           ");
                System.out.println();
                System.out.println("=========================================================");
                System.out.println();
                while(true) {
                    System.out.print(" 전 메뉴로 돌아가시겠습니까? [1] 예  [2] 아니오 (프로그램 종료) : ");
                    int num = sc.nextInt();
                    if (num == 1) {
                        break;
                    } else if (num == 2) {
                        exit(0);
                    }
                    System.out.println(" 잘못된 값을 입력하셨습니다.");
                }
                return;
            case 5:
                System.out.println("==================== [샌들 / SANDLE] ====================");
                System.out.println();
                System.out.println("        [1][S - 1]    [2][S - 2]    [3][S - 3]           ");
                System.out.println();
                System.out.println("=========================================================");
                System.out.println();
                while(true) {
                    System.out.print(" 전 메뉴로 돌아가시겠습니까? [1] 예  [2] 아니오 (프로그램 종료) : ");
                    int num = sc.nextInt();
                    if (num == 1) {
                        break;
                    } else if (num == 2) {
                        exit(0);
                    }
                    System.out.println(" 잘못된 값을 입력하셨습니다.");
                }
                return;
            case 6:
                System.out.println("전 메뉴로 돌아갑니다. ");
                System.out.println();
                return;
            default:
                System.out.println("잘못된 값을 입력하셨습니다.");
                break;
        }
    }
}
