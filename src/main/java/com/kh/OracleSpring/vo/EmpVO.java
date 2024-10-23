package com.kh.OracleSpring.vo;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EmpVO {
    private int empNO;
    private String name;
    private String job;
    private int mgr;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
    private BigDecimal sal;
    private BigDecimal comm;
    private int deptNO;
}