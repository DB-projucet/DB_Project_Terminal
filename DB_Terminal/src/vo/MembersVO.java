package vo;

import java.sql.Date;

public class MembersVO {
    private int memberId;
    private String memId;
    private String memPwd;
    private String memName;
    private String memEmail;
    private String memAddr;
    private Date memBirth;
    private String memPhone;
    private Integer memShsize;

    public MembersVO(String memId, String memPwd) {
/*        if (memId == null || memPwd == null) {
            throw new IllegalArgumentException("memId, memPwd는 null이 될 수 없음");
        }*/
        this.memId = memId;
        this.memPwd = memPwd;
    }

    public MembersVO(String memId, String memPwd, String memName, String memEmail, String memAddr, Date memBirth, String memPhone, Integer memShsize) {
/*        if (memId == null || memPwd == null || memName == null || memEmail == null || memAddr == null || memBirth == null || memPhone == null || memShsize == null) {
            throw new IllegalArgumentException("모든 필드는 null이 될 수 없음.");
        }*/
        this.memId = memId;
        this.memPwd = memPwd;
        this.memName = memName;
        this.memEmail = memEmail;
        this.memAddr = memAddr;
        this.memBirth = memBirth;
        this.memPhone = memPhone;
        this.memShsize = memShsize;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getMemId() {
        return memId;
    }

    public void setMemId(String memId) {
        this.memId = memId;
    }

    public String getMemPwd() {
        return memPwd;
    }

    public void setMemPwd(String memPwd) {
        this.memPwd = memPwd;
    }

    public String getMemName() {
        return memName;
    }

    public void setMemName(String memName) {
        this.memName = memName;
    }

    public String getMemEmail() {
        return memEmail;
    }

    public void setMemEmail(String memEmail) {
        this.memEmail = memEmail;
    }

    public String getMemAddr() {
        return memAddr;
    }

    public void setMemAddr(String memAddr) {
        this.memAddr = memAddr;
    }

    public java.sql.Date getMemBirth() {
        return (java.sql.Date) memBirth;
    }

    public void setMemBirth(Date memBirth) {
        this.memBirth = memBirth;
    }

    public String getMemPhone() {
        return memPhone;
    }

    public void setMemPhone(String memPhone) {
        this.memPhone = memPhone;
    }

    public Integer getMemShsize() {
        return memShsize;
    }

    public void setMemShsize(Integer memShsize) {
        this.memShsize = memShsize;
    }
}
