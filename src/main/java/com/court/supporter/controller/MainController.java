package com.court.supporter.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.court.supporter.command.TB_002VO;
import com.court.supporter.command.TB_003VO;
import com.court.supporter.command.TB_004VO;
import com.court.supporter.main.service.MainService;
import com.court.supporter.security.DefaultUserDetails;
import com.court.supporter.security.jwt.JwtValidator;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class MainController {

  private final JwtValidator jwtValidator;

  @Autowired
  @Qualifier("mainService")
  private MainService mainService;

  @GetMapping("/")
  public String main(@CookieValue(name = "Authorization", required = false) String token,
      HttpServletRequest request, Model model) {
    // 로그인한 상태
    if (token != null) {
      HttpSession session = request.getSession();
      session.setAttribute("token", token);

      Authentication authentication = jwtValidator.getAuthentication(token);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();

      List<GrantedAuthority> member_role = (List<GrantedAuthority>) userDetails.getAuthorities();
      session.setAttribute("member_role", member_role.get(0).getAuthority());
    }

    ArrayList<TB_002VO> tb_002list = mainService.getmainannounce();
    ArrayList<TB_003VO> tb_003list = mainService.getmainnotice();

    model.addAttribute("tb_002list", tb_002list);
    model.addAttribute("tb_003list", tb_003list);

    return "main";
  }

  @GetMapping("/guide/guideInfo")
  public String guideInfo(@CookieValue(name = "Authorization", required = false) String token,
      HttpServletRequest request) {
    // 로그인한 상태
    if (token != null) {
      HttpSession session = request.getSession();
      session.setAttribute("token", token);
    }

    return "guide/guideInfo";
  }

  @GetMapping("/guide/guideProcedure")
  public String guideProcedure(@CookieValue(name = "Authorization", required = false) String token,
      HttpServletRequest request) {
    // 로그인한 상태
    if (token != null) {
      HttpSession session = request.getSession();
      session.setAttribute("token", token);
    }

    return "guide/guideProcedure";
  }

  @GetMapping("total_search")
  public String total_search(@RequestParam("search") String search, Model model) {
    ArrayList<TB_002VO> tb_002list = mainService.getannouncelist(search);
    ArrayList<TB_003VO> tb_003list = mainService.getnoticelist(search);
    ArrayList<TB_004VO> tb_004list = mainService.getfaqlist(search);

    int tb_002total = mainService.getannouncetotal(search);
    int tb_003total = mainService.getnoticetotal(search);
    int tb_004total = mainService.getfaqtotal(search);

    for (int i = 0; i < tb_002list.size(); i++) {
      TB_002VO tb_002 = tb_002list.get(i);
      String cleanedData = tb_002.getAnnounce_content().replaceAll("\\<.*?\\>", "").replaceAll("&nbsp;", "")
          .replaceAll("-", "");
      tb_002.setAnnounce_content(cleanedData);
    }

    model.addAttribute("tb_002list", tb_002list);
    model.addAttribute("tb_003list", tb_003list);
    model.addAttribute("tb_004list", tb_004list);
    model.addAttribute("tb_002total", tb_002total);
    model.addAttribute("tb_003total", tb_003total);
    model.addAttribute("tb_004total", tb_004total);
    model.addAttribute("search", search);

    return "total-search";
  }

}