package com.court.supporter.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import com.court.supporter.notice.service.noticeService;
import com.court.supporter.security.DefaultUserDetails;
import com.court.supporter.security.jwt.JwtValidator;
import com.court.supporter.adminmypage.service.AdminMypageService;
import com.court.supporter.aws.service.NoticeFileService;
import com.court.supporter.command.TB_002VO;
import com.court.supporter.command.TB_003VO;
import com.court.supporter.command.TB_016VO;
import com.court.supporter.util.Criteria;
import com.court.supporter.util.PageVO;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

  @Autowired
  @Qualifier("noticeService")
  private noticeService noticeService;

  @Autowired
  @Qualifier("noticeFileService")
  private NoticeFileService noticeFileService;

  @Autowired
  @Qualifier("adminMypageService")
  private AdminMypageService adminMypageService;

  private final JwtValidator jwtValidator;

  // 공지사항 목록
  @GetMapping("/noticeList")
  public String noticeList(Model model, Criteria cri, HttpServletRequest request) {

    ArrayList<TB_003VO> list = noticeService.noticeList(cri);

    int total = noticeService.getTotal(cri);
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

    return "notice/noticeList";
  }

  // 공지사항 글 내용
  @GetMapping("/noticeDetail")
  public String noticeDetail(@RequestParam("notice_proper_num") String notice_proper_num, Model model,
      HttpServletRequest request) {

    TB_003VO vo = noticeService.noticeDetail(notice_proper_num);

    Map<TB_016VO, String> file = new HashMap<TB_016VO, String>();

    List<TB_016VO> filevo = noticeService.noticeFileDetail(notice_proper_num);

    // 이전 글과 다음 글 조회
    TB_003VO previousPost = noticeService.noticeGetPrev(notice_proper_num);
    TB_003VO nextPost = noticeService.noticeGetNext(notice_proper_num);

    for (int i = 0; i < filevo.size(); i++) {
      file.put(filevo.get(i), filevo.get(i).getOriginal_file_name());
      try {
        filevo.get(i).setOriginal_file_name(URLEncoder.encode(filevo.get(i).getOriginal_file_name(), "UTF-8"));
      } catch (UnsupportedEncodingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    List<String> file_name = new ArrayList<>();

    model.addAttribute("vo", vo);
    model.addAttribute("filevo", filevo);

    model.addAttribute("file", file);

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

    return "notice/noticeDetail";
  }

  // 공지사항 작성/등록 페이지
  @GetMapping("/noticeRegist")
  public String noticeRegist(HttpServletRequest request) {

    // 시큐리티
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();
      String member_role = adminMypageService.adminmypage_authcheck(member_proper_num);
      if (!member_role.equals("ROLE_ADMIN")) {

        return "redirect:/notice/noticeList";
      }

    }

    return "notice/noticeRegist";
  }

  // 공지사항 등록요청
  @PostMapping("/noticeRegistForm")
  public String noticeRegistForm(TB_003VO vo, RedirectAttributes ra, HttpServletRequest request,
      @RequestParam("file") List<MultipartFile> list) {

    // 시큐리티
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();
      String member_role = adminMypageService.adminmypage_authcheck(member_proper_num);
      if (!member_role.equals("ROLE_ADMIN")) {

        return "redirect:/notice/noticeList";
      }

      vo.setAdmin_proper_num(member_proper_num);
    }

    list = list.stream().filter(t -> t.isEmpty() == false).collect(Collectors.toList());

    for (MultipartFile file : list) {
      if (file.getContentType().contains("image") == false
          && file.getContentType().contains("application/pdf") == false
          && file.getContentType().contains("text/plain") == false
          && file.getContentType().contains("application/x-hwp") == false) {

        // 허용되지 않는 MIME타입인 경우 처리

        ra.addFlashAttribute("msg", "올바른 파일 형식이 아닙니다");

        return "redirect:/notice/noticeList";
      }
    }

    // 파일 업로드하고 저장경로를 리스트로 받음
    List<String> filelist = noticeFileService.noticeFileRegist(list);

    // 파일리스트와 같이 등록 진행
    int result = noticeService.noticeRegist(vo, filelist);

    System.out.println("filelist = " + filelist);

    System.out.println("등록됨 : " + vo.toString());

    String msg = result == 1 ? "등록되었습니다." : "등록실패";

    ra.addFlashAttribute("msg", msg);

    return "redirect:/notice/noticeList";
  }

  // 공지사항 수정 페이지
  @GetMapping("/noticeModify")
  public String noticeModify(@RequestParam("notice_proper_num") String notice_proper_num, Model model,
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

        return "redirect:/notice/noticeList";
      }

    }

    TB_003VO vo = noticeService.noticeDetail(notice_proper_num);

    Map<TB_016VO, String> file = new HashMap<TB_016VO, String>();

    List<TB_016VO> filevo = noticeService.noticeFileDetail(notice_proper_num);

    for (int i = 0; i < filevo.size(); i++) {
      file.put(filevo.get(i), filevo.get(i).getOriginal_file_name());
      try {
        filevo.get(i).setOriginal_file_name(URLEncoder.encode(filevo.get(i).getOriginal_file_name(), "UTF-8"));
      } catch (UnsupportedEncodingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    List<String> file_name = new ArrayList<>();

    model.addAttribute("vo", vo);
    model.addAttribute("filevo", filevo);

    model.addAttribute("file", file);

    return "notice/noticeModify";
  }

  // 공지사항 수정(업데이트)
  @PostMapping("/noticeUpdateForm")
  public String noticeUpdate(TB_003VO vo, RedirectAttributes ra, @RequestParam("file") List<MultipartFile> list,
      @RequestParam("notice_proper_num") String notice_proper_num,
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

        return "redirect:/notice/noticeList";
      }

    }

    list = list.stream().filter(t -> t.isEmpty() == false).collect(Collectors.toList());

    for (MultipartFile file : list) {
      if (file.getContentType().contains("image") == false
          && file.getContentType().contains("application/pdf") == false
          && file.getContentType().contains("text/plain") == false
          && file.getContentType().contains("application/x-hwp") == false) {

        // 허용되지 않는 MIME타입인 경우 처리

        ra.addFlashAttribute("msg", "올바른 파일 형식이 아닙니다");

        return "redirect:/notice/noticeList";
      }
    }
    // 기존업로드 파일 삭제
    // 데이터 이름 가져오기
    List<TB_016VO> filevo = noticeService.noticeFileDetail(notice_proper_num);
    // 파일삭제
    noticeFileService.noticeFileDelete(filevo);
    // sql삭제
    noticeService.noticeFileDelete(notice_proper_num);

    // 파일 업로드하고 저장경로를 리스트로 받음
    List<String> filelist = noticeFileService.noticeFileRegist(list);

    // 파일리스트와 같이 등록 진행
    int result = noticeService.noticeUpdate(vo, filelist);

    String msg = "저장되었습니다.";// 왜안됨?

    System.out.println(vo.toString());

    return "redirect:/notice/noticeList";
  }

  // 공지사항 삭제
  @GetMapping("/noticeDelete")
  public String noticeDelete(@RequestParam("notice_proper_num") String notice_proper_num, RedirectAttributes ra,
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

        return "redirect:/notice/noticeList";
      }

    }

    noticeService.noticeDelete(notice_proper_num);
    // 기존업로드 파일 삭제
    // 데이터 이름 가져오기
    List<TB_016VO> filevo = noticeService.noticeFileDetail(notice_proper_num);
    // 파일삭제
    noticeFileService.noticeFileDelete(filevo);
    // sql삭제
    noticeService.noticeFileDelete(notice_proper_num);

    return "redirect:/notice/noticeList";
  }

}
