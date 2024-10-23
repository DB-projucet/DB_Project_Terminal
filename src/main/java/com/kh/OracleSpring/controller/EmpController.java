package com.kh.OracleSpring.controller;

import com.kh.OracleSpring.dao.EmpDAO;
import com.kh.OracleSpring.vo.EmpVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/emp")     //http://localhost:8112/emp
public class EmpController {
    private final EmpDAO empDAO;

    public EmpController(EmpDAO empDAO) {
        this.empDAO = empDAO;
    }
    @GetMapping("/select")  //http://localhost:8112/select
    public String selectViewEmp(Model model) {
        List<EmpVO> emps = empDAO.empSelect();
        model.addAttribute("employees", emps);
        return "thymeleaf/empSelect";
    }
    @GetMapping("/insert")
    public String insertViewEmp(Model model) {
        model.addAttribute("employees", new EmpVO());
        return "thymeleaf/empInsert";
    }

    @PostMapping("/insert")
    public String insertDBEmp(@ModelAttribute("employees") EmpVO empVO, Model model) {
        boolean isSuccess = empDAO.empInsert(empVO);
        model.addAttribute("isSuccess", isSuccess);
        return "thymeleaf/empResult";
    }
}
