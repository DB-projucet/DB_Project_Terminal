package vo;

import java.sql.Date;

public class ReviewVO {
    private int review_id;
    private String mem_id;
    private int order_id;
    private Date buy_date;
    private String p_type;
    private String p_color;
    private String r_content;
    private int star_rate;

    public ReviewVO(String mem_id, int order_id, Date buy_date, String p_type, String p_color, String r_content, int star_rate) {
        this.mem_id = mem_id;
        this.order_id = order_id;
        this.buy_date = buy_date;
        this.p_type = p_type;
        this.p_color = p_color;
        this.r_content = r_content;
        this.star_rate = star_rate;
    }

    public ReviewVO(String mem_id, String p_type, Date buy_date, String p_color, int star_rate) {
        this.mem_id = mem_id;
        this.p_type = p_type;
        this.buy_date = buy_date;
        this.p_color = p_color;
        this.star_rate = star_rate;
    }

    @Override
    public String toString() {
        return String.format(
                "< 주문 ID: %-10s   신발 종류: %-15s   주문일: %-20s >" +
                "============================ 리뷰 내용 ============================" +
                        " 내용: %-20s"+
                "================================================================="+
                " < 별점 : %-1d",
                mem_id,
                p_type,
                buy_date,
                r_content,
                star_rate

        );
    }

    public int getReview_id() {
        return review_id;
    }

    public String getMem_id() {
        return mem_id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public Date getBuy_date() {
        return buy_date;
    }

    public String getP_type() {
        return p_type;
    }

    public String getP_color() {
        return p_color;
    }

    public String getR_content() {
        return r_content;
    }

    public int getStar_rate() {
        return star_rate;
    }

    public void setReview_id(int review_id) {
        this.review_id = review_id;
    }

    public void setMem_id(String mem_id) {
        this.mem_id = mem_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public void setBuy_date(Date buy_date) {
        this.buy_date = buy_date;
    }

    public void setP_color(String p_color) {
        this.p_color = p_color;
    }

    public void setP_type(String p_type) {
        this.p_type = p_type;
    }

    public void setStar_rate(int star_rate) {
        this.star_rate = star_rate;
    }

    public void setR_content(String r_content) {
        this.r_content = r_content;
    }
}