package com.court.supporter.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.court.supporter.adminmypage.service.AdminMypageService;
import com.court.supporter.command.TB_003VO;
import com.court.supporter.command.TB_004VO;
import com.court.supporter.faq.service.faqService;
import com.court.supporter.security.DefaultUserDetails;
import com.court.supporter.security.jwt.JwtValidator;
import com.court.supporter.util.Criteria;
import com.court.supporter.util.PageVO;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/faq")
@RequiredArgsConstructor
public class FaqController {

  @Autowired
  @Qualifier("faqService")
  private faqService faqService;

  @Autowired
  @Qualifier("adminMypageService")
  private AdminMypageService adminMypageService;

  private final JwtValidator jwtValidator;

  // faq 목록
  @GetMapping("/faqList")
  public String faqList(Model model, Criteria cri, HttpServletRequest request) {

    ArrayList<TB_004VO> list = faqService.faqList(cri);

    int total = faqService.getTotal(cri);
    PageVO pageVO = new PageVO(cri, total);

    model.addAttribute("total", total);
    model.addAttribute("list", list);
    model.addAttribute("pageVO", pageVO);

    System.out.println(pageVO.toString());
    System.out.println(total);

    // 시큐리티
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();
      String member_role = adminMypageService.adminmypage_authcheck(member_proper_num);
      if (member_role.equals("ROLE_ADMIN")) {

        model.addAttribute("member_role", member_role);
      }

    }
    return "faq/faqList";
  }

  // faq 글 내용
  @GetMapping("/faqDetail")
  public String faqDetail(@RequestParam("faq_proper_num") String faq_proper_num, Model model,
      HttpServletRequest request) {

    TB_004VO vo = faqService.faqDetail(faq_proper_num);

    // 이전 글과 다음 글 조회
    TB_004VO previousPost = faqService.faqGetPrev(faq_proper_num);
    TB_004VO nextPost = faqService.faqGetNext(faq_proper_num);

    model.addAttribute("vo", vo);

    model.addAttribute("previousPost", previousPost);
    model.addAttribute("nextPost", nextPost);

    // 시큐리티
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();
      String member_role = adminMypageService.adminmypage_authcheck(member_proper_num);
      if (member_role.equals("ROLE_ADMIN")) {

        model.addAttribute("member_role", member_role);
      }

    }

    return "faq/faqDetail";
  }

  // faq 작성/등록 페이지
  @GetMapping("/faqRegist")
  public String faqRegist(HttpServletRequest request) {

    // 시큐리티
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();
      String member_role = adminMypageService.adminmypage_authcheck(member_proper_num);
      if (!member_role.equals("ROLE_ADMIN")) {

        return "redirect:/faq/faqList";
      }

    }

    return "faq/faqRegist";
  }

  // faq 등록요청
  @PostMapping("/faqRegistForm")
  public String faqRegistForm(TB_004VO vo, RedirectAttributes ra, HttpServletRequest request) {

    // 시큐리티
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();
      String member_role = adminMypageService.adminmypage_authcheck(member_proper_num);
      if (!member_role.equals("ROLE_ADMIN")) {

        return "redirect:/faq/faqList";
      }

      vo.setAdmin_proper_num(member_proper_num);
    }

    System.out.println(vo.toString());
    int result = faqService.faqRegist(vo);

    String msg = result == 1 ? "등록되었습니다." : "등록실패";

    ra.addFlashAttribute("msg", msg);

    return "redirect:/faq/faqList";
  }

  // faq 수정 페이지
  @GetMapping("/faqModify")
  public String faqModify(@RequestParam("faq_proper_num") String faq_proper_num, Model model,
      HttpServletRequest request) {

    // 시큐리티
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();
      String member_role = adminMypageService.adminmypage_authcheck(member_proper_num);
      if (!member_role.equals("ROLE_ADMIN")) {

        return "redirect:/faq/faqList";
      }

    }

    TB_004VO vo = faqService.faqDetail(faq_proper_num);

    model.addAttribute("vo", vo);

    return "faq/faqModify";
  }

  // faq 수정(업데이트)
  @PostMapping("/faqUpdateForm")
  public String faqUpdate(TB_004VO vo, RedirectAttributes ra, HttpServletRequest request) {

    // 시큐리티
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();
      String member_role = adminMypageService.adminmypage_authcheck(member_proper_num);
      if (!member_role.equals("ROLE_ADMIN")) {

        return "redirect:/faq/faqList";
      }

    }
    System.out.println(vo.toString());

    int result = faqService.faqUpdate(vo);

    String msg = result == 1 ? "등록되었습니다." : "등록실패";

    ra.addFlashAttribute("msg", msg);

    return "redirect:/faq/faqList";
  }

  // faq 삭제
  @GetMapping("/faqDelete")
  public String faqDelete(@RequestParam("faq_proper_num") String faq_proper_num, RedirectAttributes ra,
      HttpServletRequest request) {

    // 시큐리티
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();
      String member_role = adminMypageService.adminmypage_authcheck(member_proper_num);
      if (!member_role.equals("ROLE_ADMIN")) {

        return "redirect:/faq/faqList";
      }

    }

    faqService.faqDelete(faq_proper_num);

    return "redirect:/faq/faqList";
  }

}
