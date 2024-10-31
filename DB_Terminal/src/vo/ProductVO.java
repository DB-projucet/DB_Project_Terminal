package vo;

public class ProductVO {
    private int product_id;
    private String P_type;
    private String P_name;
    private String p_color;
    private int P_size;
    private int  P_price;
    private int p_cnt;

    public ProductVO(String p_name, int p_price) {
        this.P_name = p_name; // 변수명 확인
        this.P_price = p_price;
    }

    public ProductVO(int product_id, String p_type, String p_name, String p_color, int p_size, int p_price, int p_cnt) {
        this.product_id = product_id;
        P_type = p_type;
        P_name = p_name;
        this.p_color = p_color;
        P_size = p_size;
        P_price = p_price;
        this.p_cnt = p_cnt;
    }

    public int getProduct_id() {
        return product_id;
    }

    public String getP_type() {
        return P_type;
    }

    public String getP_name() {
        return P_name;
    }

    public String getP_color() {
        return p_color;
    }

    public int getP_size() {
        return P_size;
    }

    public int getP_price() {
        return P_price;
    }

    public int getP_cnt() {
        return p_cnt;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public void setP_type(String p_type) {
        P_type = p_type;
    }

    public void setP_name(String p_name) {
        P_name = p_name;
    }

    public void setP_color(String p_color) {
        this.p_color = p_color;
    }

    public void setP_size(int p_size) {
        P_size = p_size;
    }

    public void setP_price(int p_price) {
        P_price = p_price;
    }

    public void setP_cnt(int p_cnt) {
        this.p_cnt = p_cnt;
    }
}