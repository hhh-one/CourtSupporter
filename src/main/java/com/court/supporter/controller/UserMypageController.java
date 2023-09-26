package com.court.supporter.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.court.supporter.aws.service.NoticeFileService;
import com.court.supporter.aws.service.UserMypageFileService;
import com.court.supporter.command.TB_001VO;
import com.court.supporter.command.TB_005VO;
import com.court.supporter.command.TB_006VO;
import com.court.supporter.command.TB_007VO;
import com.court.supporter.command.TB_008VO;
import com.court.supporter.command.TB_009VO;
import com.court.supporter.command.TB_010VO;
import com.court.supporter.command.TB_011VO;
import com.court.supporter.command.TB_012VO;
import com.court.supporter.command.TB_014VO;
import com.court.supporter.command.TB_019VO;
import com.court.supporter.security.DefaultUserDetails;
import com.court.supporter.security.jwt.JwtValidator;
import com.court.supporter.usermypage.service.UserMypageService;
import com.court.supporter.util.Criteria;
import com.court.supporter.util.PageVO;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/usermypage")
@RequiredArgsConstructor
public class UserMypageController {

  @Autowired
  @Qualifier("userMypageService")
  private UserMypageService userMypageService;

  @Autowired
  @Qualifier("userMypageFileService")
  private UserMypageFileService userMypageFileService;

  private final JwtValidator jwtValidator;

  // 사용자 정보 화면
  @GetMapping("/usermypage")
  public String usermypage(Model model, HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();
      model.addAttribute("vo", userMypageService.usermypage_getInfo(member_proper_num));
      return "usermypage/usermypage";
    }
    return "redirect:/";
  }

  // 사용자 정보 비밀번호 확인 창
  @GetMapping("/usermypage_modify_pw_check_view")
  public String usermypage_modify_pw_check_view(HttpServletRequest request, Model model) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();
      model.addAttribute("member_proper_num", member_proper_num);
      return "usermypage/usermypage-modify-pw-check";
    }
    return "redirect:/";
  }

  // 사용자 정보 수정 비밀번호 확인
  @PostMapping("/usermypage_modify_pw_check")
  public String usermypage_modify_pw_check(TB_001VO vo, Model model, HttpSession se, HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();
      vo.setUser_proper_num(member_proper_num);
      int result = userMypageService.usermypage_modify_pw_check(vo);
      if (result == 1) {
        return "redirect:/usermypage/usermypage_modify";
      } else {
        return "redirect:/usermypage/usermypage_modify_pw_check_view";
      }
    }
    return "redirect:/";
  }

  // 사용자 정보 수정 창
  @GetMapping("/usermypage_modify")
  public String usermypage_modify(Model model, HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      TB_019VO tb_019VO = userMypageService.usermypage_getimg(member_proper_num);

      if (tb_019VO != null) {
        try {
          tb_019VO.setOriginal_file_name(URLEncoder.encode(tb_019VO.getOriginal_file_name(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
        }
        model.addAttribute("tb_019VO", tb_019VO);

      }

      model.addAttribute("vo", userMypageService.usermypage_getInfo(member_proper_num));

      return "usermypage/usermypage-modify";
    }
    return "redirect:/";
  }

  // 사용자 정보 수정
  @PostMapping("/usermypage_modify_confirm")
  public String usermypage_modify(TB_001VO vo, RedirectAttributes ra, HttpServletRequest request,
      @RequestParam("file") MultipartFile file) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      if (file.getContentType().contains("image") == true) {
        TB_019VO tb_019VO = new TB_019VO();
        tb_019VO.setUser_proper_num(member_proper_num);
        String savepath = userMypageFileService.imgFileRegist(file);
        int imgresult = userMypageService.usermypage_registimg(savepath, tb_019VO);
      }

      vo.setUser_proper_num(member_proper_num);
      int result = userMypageService.usermypage_modifyInfo(vo);
      if (result == 1) {
        ra.addFlashAttribute("result", 1);
        ra.addFlashAttribute("msg", "정보를 변경하였습니다.");
      } else {
        ra.addFlashAttribute("result", 0);
        ra.addFlashAttribute("msg", "정보 변경에 실패하였습니다.");
      }
      return "redirect:/usermypage/usermypage";
    }
    return "redirect:/";
  }

  ////////////////////////////////////////////////////////////////////////////////////////

  // 사용자 탈퇴 화면
  @GetMapping("/usermypage_withdrawl")
  public String usermypage_withdrawl(HttpServletRequest request, Model model) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();
      model.addAttribute("member_proper_num", member_proper_num);
      return "usermypage/usermypage-withdrawl";
    }
    return "redirect:/";
  }

  // 사용자 탈퇴 체크 화면
  @GetMapping("/usermypage_withdrawl_popup")
  public String usermypage_withdrawl_popup(HttpServletRequest request, Model model) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();
      model.addAttribute("member_proper_num", member_proper_num);
      return "usermypage/usermypage-withdrawl-popup";
    }
    return "redirect:/";
  }

  // 사용자 탈퇴
  @PostMapping("/usermypage_withdrawl_check")
  public String usermypage_withdrawl_check(TB_001VO vo, Model model, HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();
      vo.setUser_proper_num(member_proper_num);
      int result = userMypageService.usermypage_withdrawl(vo);
      model.addAttribute("result", result);
      return "usermypage/usermypage-withdrawl-popup";
    }
    return "redirect:/";
  }

  ////////////////////////////////////////////////////////////////////////////////////////

  // 사용자 신청 현황 리스트
  @GetMapping("/usermypage_applicationlist")
  public String usermypage_applicationlist(Model model, Criteria cri, HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();
      ArrayList<TB_005VO> list = userMypageService.usermypage_application_getlist(member_proper_num, cri);
      int total = userMypageService.usermypage_application_gettotal(member_proper_num, cri);
      PageVO pageVO = new PageVO(cri, total);
      model.addAttribute("total", total);
      model.addAttribute("pageVO", pageVO);
      model.addAttribute("list", list);
      return "usermypage/usermypage-applicationlist";
    }
    return "redirect:/";
  }

  // 사용자 신청 현황 상세
  @GetMapping("/usermypage_applicationdetail")

  public String usermypage_applicationdetail(Model model, TB_005VO vo, HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();
      vo.setUser_proper_num(member_proper_num);

      Map<TB_009VO, String> tb_009VOlist = new HashMap<TB_009VO, String>();

      TB_001VO tb_001VO = userMypageService.usermypage_getInfo(member_proper_num);
      TB_005VO tb_005VO = userMypageService.usermypage_getapplicationdetail1(vo);
      ArrayList<TB_006VO> tb_006VOlist = userMypageService.usermypage_getapplicationdetail2(vo);
      ArrayList<TB_007VO> tb_007VOlist = userMypageService.usermypage_getapplicationdetail3(vo);
      ArrayList<TB_008VO> tb_008VOlist = userMypageService.usermypage_getapplicationdetail4(vo);
      ArrayList<TB_009VO> fileVO = userMypageService.usermypage_getapplicationdetail5(vo);
      TB_010VO tb_010VO = userMypageService.usermypage_getapplicationdetail6(vo);
      ArrayList<TB_011VO> tb_011VOlist = userMypageService.usermypage_getapplicationdetail7(vo);

      fileVO.removeIf(Objects::isNull);
      for (int i = 0; i < fileVO.size(); i++) {
        String url = URLEncoder.encode(fileVO.get(i).getOriginal_file_name());
        tb_009VOlist.put(fileVO.get(i), fileVO.get(i).getOriginal_file_name());
        fileVO.get(i).setOriginal_file_name(url);
      }

      tb_006VOlist.removeIf(Objects::isNull);
      tb_007VOlist.removeIf(Objects::isNull);
      tb_008VOlist.removeIf(Objects::isNull);
      tb_011VOlist.removeIf(Objects::isNull);

      model.addAttribute("tb_001VO", tb_001VO);
      model.addAttribute("tb_005VO", tb_005VO);
      model.addAttribute("tb_006VOlist", tb_006VOlist);
      model.addAttribute("tb_007VOlist", tb_007VOlist);
      model.addAttribute("tb_008VOlist", tb_008VOlist);
      model.addAttribute("tb_009VOlist", tb_009VOlist);
      model.addAttribute("tb_010VO", tb_010VO);
      model.addAttribute("tb_011VOlist", tb_011VOlist);

      return "usermypage/usermypage-applicationdetail";
    }
    return "redirect:/";
  }

  @PostMapping("/usermypage_evaluation_result")
  public String usermypage_evaluation_result(TB_005VO vo, HttpServletRequest request, Model model) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();
      vo.setUser_proper_num(member_proper_num);
      TB_001VO tb_001VO = userMypageService.usermypage_getInfo(member_proper_num);
      TB_005VO tb_005VO = userMypageService.usermypage_getapplicationdetail1(vo);
      model.addAttribute("tb_001VO", tb_001VO);
      model.addAttribute("tb_005VO", tb_005VO);
      model.addAttribute("evaluate_result", 1);
      return "usermypage/usermypage-evaluation-result";
    }
    return "redirect:/";
  }

  ////////////////////////////////////////////////////////////////////////////////////////

  // 사용자 활동 내역 리스트
  @GetMapping("/usermypage_activitylist")
  public String usermypage_activitylist(Model model, Criteria cri, HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();
      ArrayList<TB_012VO> list = userMypageService.usermypage_activity_getlist(member_proper_num, cri);
      int total = userMypageService.usermypage_activity_gettotal(member_proper_num, cri);
      PageVO pageVO = new PageVO(cri, total);
      model.addAttribute("total", total);
      model.addAttribute("pageVO", pageVO);
      model.addAttribute("list", list);

      return "usermypage/usermypage-activitylist";
    }
    return "redirect:/";
  }

  // 사용자 활동 내역 상세
  @GetMapping("/usermypage_activitydetail")
  public String usermypage_activitydetail(Model model, TB_012VO vo, HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();
      vo.setUser_proper_num(member_proper_num);
      TB_012VO result = userMypageService.usermypage_getactivitydetail(vo);
      model.addAttribute("result", result);
      return "usermypage/usermypage-activitydetail";
    }
    return "redirect:/";
  }

  // 사용자 활동 내역 등록 창
  @GetMapping("/usermypage_activity_regist")
  public String usermypage_activity_regist(HttpServletRequest request, Model model) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();
      ArrayList<TB_014VO> list = userMypageService.usermypage_getacceptinfo(member_proper_num);
      if (list.size() == 0) {
        return "redirect:/";
      }
      model.addAttribute("list,", list);
      return "usermypage/usermypage-activity-regist";
    }
    return "redirect:/";
  }

  // 사용자 활동 내역 등록
  @PostMapping("/usermypage_activity_regist_form")
  public String usermypage_activity_regist_form(TB_012VO vo, HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {

      return "redirect:/usermypage/usermypage_activitylist";
    }
    return "redirect:/";
  }
  ////////////////////////////////////////////////////////////////////////////////////////

  // 사용자 중지 리스트
  @GetMapping("/usermypage_pauselist")
  public String usermypage_pause(Model model, Criteria cri, HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();
      ArrayList<TB_014VO> list = userMypageService.usermypage_pauselist(member_proper_num, cri);
      int total = userMypageService.usermypage_pausetotal(member_proper_num, cri);
      PageVO pageVO = new PageVO(cri, total);
      model.addAttribute("total", total);
      model.addAttribute("pageVO", pageVO);
      model.addAttribute("list", list);
      return "usermypage/usermypage-pause";
    }
    return "redirect:/";
  }

  // 사용자 중지 상세
  @GetMapping("/usermypage_pausedetail")
  public String usermypage_pausedetail(Model model, TB_014VO vo, HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();
      vo.setUser_proper_num(member_proper_num);
      TB_014VO result = userMypageService.usermypage_getpausedetail(vo);
      model.addAttribute("result", result);
      return "usermypage/usermypage-pausedetail";
    }
    return "redirect:/";
  }

  // 사용자 중지/해제 신청
  @PostMapping("/usermypage_pauseapplication")
  public String usermypage_pauseapplication(TB_014VO vo, HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();
      vo.setUser_proper_num(member_proper_num);
      int result = userMypageService.usermypage_pauseapplication(vo);
      return "redirect:/usermypage/usermypage_pauselist";
    }
    return "redirect:/";
  }

}
