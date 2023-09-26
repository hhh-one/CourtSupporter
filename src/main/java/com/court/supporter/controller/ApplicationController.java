package com.court.supporter.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import com.court.supporter.application.service.ApplicationService;
import com.court.supporter.aws.service.ApplicationFileService;
import com.court.supporter.command.TB_001VO;
import com.court.supporter.command.TB_002VO;
import com.court.supporter.command.TB_005VO;
import com.court.supporter.command.TB_006VO;
import com.court.supporter.command.TB_007VO;
import com.court.supporter.command.TB_008VO;
import com.court.supporter.command.TB_009VO;
import com.court.supporter.command.TB_010VO;
import com.court.supporter.command.TB_011VO;
import com.court.supporter.security.DefaultUserDetails;
import com.court.supporter.security.jwt.JwtValidator;
import com.court.supporter.usermypage.service.UserMypageService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/application")
@RequiredArgsConstructor
public class ApplicationController {

  private final JwtValidator jwtValidator;

  @Autowired
  @Qualifier("applicationService")
  private ApplicationService applicationService;

  @Autowired
  @Qualifier("applicationFileService")
  private ApplicationFileService applicationFileService;

  @Autowired
  @Qualifier("userMypageService")
  private UserMypageService userMypageService;

  // 동의 화면
  @GetMapping("/applicationAgree")
  public String agreeForm(HttpServletRequest request) {

    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");

    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      return "application/applicationAgree";
    }
    return "redirect:/";
  }

  // 기본 정보 입력 화면으로 이동
  @PostMapping("/agreeForm")
  public String basic(TB_005VO tb_005vo, HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");

    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      String announce = tb_005vo.getAnnounce_proper_num();
      String trial_fcltt_proper_num = applicationService.getFclttNum(announce).getTrial_fcltt_proper_num();
      return "redirect:/application/applicationBasic?announce=" + announce + "&fcltt_num=" + trial_fcltt_proper_num;
    }
    return "redirect:/";
  }

  // 기본 정보 화면
  @GetMapping("/applicationBasic")
  public String basicForm(TB_001VO tb_001vo, TB_005VO tb_005vo, Model model, HttpServletRequest request,
      @RequestParam("announce") String announce) {

    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      tb_001vo.setUser_proper_num(member_proper_num);
      // 기본 정보 페이지 - user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);
      tb_001vo.setUser_id(user_id);
      tb_005vo.setUser_id(user_id);
      tb_005vo.setUser_proper_num(member_proper_num);
      tb_005vo.setAnnounce_proper_num(announce);

      // 신청인 정보 가져오기
      TB_001VO vo1 = applicationService.getUserInfo(tb_001vo);
      model.addAttribute("tb_001vo", vo1);

      // tb_005vo 데이터 가져오기
      TB_005VO vo5 = applicationService.getApplicationBasicInfo(tb_005vo);
      model.addAttribute("tb_005vo", vo5);

      return "application/applicationBasic";
    }
    return "redirect:/";
  }

  // 기본 정보 페이지 - 희망법원 데이터 가져오기
  @PostMapping("/courtList")
  public ResponseEntity<ArrayList<TB_005VO>> courtList(@RequestBody TB_005VO tb_005vo, HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      tb_005vo.setUser_proper_num(member_proper_num);
      // 기본 정보 페이지 - user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);
      tb_005vo.setUser_id(user_id);
      tb_005vo.setAnnounce_proper_num(tb_005vo.getAnnounce_proper_num());

      ArrayList<TB_005VO> list = applicationService.getCourtList(tb_005vo);

      return new ResponseEntity<>(list, HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

  // 기본 정보 페이지 - 주민번호 조회
  @PostMapping("/fetchData")
  public ResponseEntity<String> fetchData(@RequestBody TB_001VO tb_001vo, HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      boolean isDataAvailable = applicationService.getUserRrn(tb_001vo.getUser_proper_num(),
          tb_001vo.getUser_name(), tb_001vo.getUser_rrn());

      if (isDataAvailable) {
        return new ResponseEntity<>("{\"success\": true}", HttpStatus.OK);
      } else {
        return new ResponseEntity<>("{\"success\": false}", HttpStatus.OK);
      }
    }
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

  // 학력사항 입력 화면으로 이동
  @PostMapping("/basicForm")
  public String education(TB_001VO tb_001vo, TB_005VO tb_005vo, TB_010VO tb_010vo, HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      tb_001vo.setUser_proper_num(member_proper_num);
      tb_005vo.setUser_proper_num(member_proper_num);
      // 기본 정보 페이지 - user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);
      tb_005vo.setUser_id(user_id);
      if (applicationService.getCourtList(tb_005vo) != null) {
        applicationService.basicDelete(tb_005vo);
        applicationService.basicRegist(tb_001vo, tb_005vo, tb_010vo);
      } else {
        // 기본 정보 등록
        applicationService.basicRegist(tb_001vo, tb_005vo, tb_010vo);
      }
      TB_005VO vo = applicationService.getApplicationBasicInfo(tb_005vo);
      return "redirect:/application/applicationEducation?announce=" + vo.getAnnounce_proper_num() + "&fcltt_num="
          + vo.getTrial_fcltt_proper_num() + "&detail=" + vo.getAplcn_dtls_proper_num();
    }
    return "redirect:/";
  }

  // 학력사항 화면
  @GetMapping("/applicationEducation")
  public String educationForm(TB_006VO tb_006vo, TB_009VO tb_009vo, Model model, HttpServletRequest request,
      @RequestParam("announce") String announce, @RequestParam("detail") String detail) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      // 기본 정보 페이지 - user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);
      tb_006vo.setUser_id(user_id);
      tb_009vo.setUser_id(user_id);
      tb_006vo.setUser_proper_num(member_proper_num);
      tb_006vo.setAplcn_dtls_proper_num(detail);

      tb_009vo.setFile_type("EDUCATIONLEVEL");
      // 학력 정보 불러오기
      ArrayList<TB_006VO> list = applicationService.getEducationList(tb_006vo);
      model.addAttribute("list", list);

      // 첨부파일 불러오기
      tb_009vo.setAplcn_dtls_proper_num(detail);
      ArrayList<TB_009VO> fileList = applicationService.fileList(tb_009vo);
      model.addAttribute("fileList", fileList);

      return "application/applicationEducation";
    }
    return "redirect:/";
  }

  // 학력사항 입력 팝업창
  @GetMapping("/applicationEducationPopup")
  public String educationPopupForm(HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      return "application/applicationEducationPopup";
    }
    return "redirect:/";
  }

  // 학력사항 등록
  @PostMapping("/educationPopupForm")
  public String educationPopup(TB_006VO tb_006vo, HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      // user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);
      tb_006vo.setUser_id(user_id);
      tb_006vo.setUser_proper_num(member_proper_num);

      applicationService.educationRegist(tb_006vo);

      return "redirect:/application/applicationEducation";
    }
    return "redirect:/";
  }

  // 학력사항 입력 정보 전송
  @PostMapping("/educationInfo")
  public ResponseEntity<ArrayList<TB_006VO>> educationInfo(@RequestBody TB_006VO tb_006vo, HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();
      // user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);
      tb_006vo.setUser_id(user_id);
      tb_006vo.setUser_proper_num(member_proper_num);
      ArrayList<TB_006VO> list = applicationService.getEducationList(tb_006vo);

      return new ResponseEntity<>(list, HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

  // 학력사항 수정 팝업창
  @GetMapping("/applicationEducationModifyPopup")
  public String educationModifyPopupForm(TB_006VO tb_006vo, Model model, @RequestParam("data") String data,
      @RequestParam("detail") String detail, HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      // user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);
      tb_006vo.setUser_id(user_id);
      tb_006vo.setEdctn_dtls_proper_num(data);
      tb_006vo.setAplcn_dtls_proper_num(detail);

      // 학력 정보 불러오기
      TB_006VO vo = applicationService.getEducationInfo(tb_006vo);
      model.addAttribute("vo", vo);
      return "application/applicationEducationModifyPopup";
    }
    return "redirect:/";
  }

  // 학력사항 수정
  @PostMapping("/educationModifyPopupForm")
  public String educationModifyPopup(TB_006VO tb_006vo, HttpServletRequest request) {

    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      // user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);
      tb_006vo.setUser_id(user_id);
      tb_006vo.setUser_proper_num(member_proper_num);

      applicationService.educationModify(tb_006vo);

      return "redirect:/application/applicationEducation";
    }
    return "redirect:/";
  }

  // 학력 정보 삭제
  @PostMapping("/educationInfoDelete")
  public ResponseEntity<ArrayList<TB_006VO>> educationInfoDelete(@RequestBody TB_006VO tb_006vo,
      HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      // user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);
      tb_006vo.setUser_id(user_id);

      applicationService.educationInfoDelete(tb_006vo);
      ArrayList<TB_006VO> list = applicationService.getEducationList(tb_006vo);
      return new ResponseEntity<>(list, HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

  // 학력 정보 파일 수정
  @PostMapping("/educationFileModify")
  public ResponseEntity<String> educationFileModify(@RequestBody Map<String, Object> dataMap, TB_009VO tb_009vo,
      HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      List<String> uuids = (List<String>) dataMap.get("uuids");
      String detail = (String) dataMap.get("detail");

      tb_009vo.setFile_type("EDUCATIONLEVEL");

      // user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);
      tb_009vo.setUser_id(user_id);
      tb_009vo.setAplcn_dtls_proper_num(detail);
      List<String> keyNames = applicationService.getFilepath(uuids, tb_009vo);

      applicationFileService.ApplicationFileDelete(keyNames);

      return new ResponseEntity<>("응답", HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

  // 경력사항 입력 화면으로 이동
  @PostMapping("/educationForm")
  public String work(TB_005VO tb_005vo, TB_006VO tb_006vo, TB_009VO tb_009vo,
      @RequestParam(value = "file", required = false) List<MultipartFile> list, HttpServletRequest request) {

    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      // user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);

      // 최종학력 입력
      tb_006vo.setUser_id(user_id);
      applicationService.finalEducation(tb_006vo);
      // 파일 업로드
      tb_009vo.setUser_id(user_id);
      tb_009vo.setUser_proper_num(member_proper_num);
      tb_009vo.setFile_code("PE");
      tb_009vo.setFile_type("EDUCATIONLEVEL");
      if (list != null) {

        List<String> fileList = applicationFileService.ApplicationFileRegist(list, tb_006vo.getUser_id());

        applicationService.attachmentRegist(fileList, tb_009vo);
      }
      return "redirect:/application/applicationWork?announce=" + tb_005vo.getAnnounce_proper_num() + "&fcltt_num="
          + tb_005vo.getTrial_fcltt_proper_num() + "&detail=" + tb_009vo.getAplcn_dtls_proper_num();
    }
    return "redirect:/";
  }

  // 경력사항 화면
  @GetMapping("/applicationWork")
  public String workForm(TB_007VO tb_007vo, TB_009VO tb_009vo, Model model, HttpServletRequest request,
      @RequestParam("detail") String detail) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      // user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);
      tb_007vo.setUser_id(user_id);
      tb_009vo.setUser_id(user_id);
      tb_009vo.setFile_type("CARRER");
      tb_007vo.setAplcn_dtls_proper_num(detail);
      // 경력사항 데이터 불러오기
      TB_007VO vo = applicationService.getWorkEtc(tb_007vo);
      ArrayList<TB_007VO> list = applicationService.getWorkList(tb_007vo);
      model.addAttribute("list", list);
      model.addAttribute("vo", vo);

      // 첨부파일 불러오기
      tb_009vo.setAplcn_dtls_proper_num(detail);
      ArrayList<TB_009VO> fileList = applicationService.fileList(tb_009vo);
      model.addAttribute("fileList", fileList);
      return "application/applicationWork";
    }
    return "redirect:/";
  }

  // 경력사항 입력 팝업창
  @GetMapping("/applicationWorkPopup")
  public String workPopupForm(HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      return "application/applicationWorkPopup";
    }
    return "redirect:/";
  }

  // 경력 사항 등록
  @PostMapping("/workPopupForm")
  public String workPopup(TB_007VO tb_007vo, HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      // user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);
      tb_007vo.setUser_id(user_id);
      tb_007vo.setUser_proper_num(member_proper_num);

      // if(applicationService.getWorkList(tb_007vo).size() == 0) {
      applicationService.workRegist(tb_007vo);
      // } else if (applicationService.getWorkList(tb_007vo).size() != 0 &&
      // applicationService.getWorkList(tb_007vo).get(0).getCompany_name() != null) {
      // applicationService.workRegist(tb_007vo);
      // } else if (applicationService.getWorkList(tb_007vo).size() != 0 &&
      // applicationService.getWorkList(tb_007vo).get(0).getCompany_name() == null) {
      // String proper_num =
      // applicationService.getWorkList(tb_007vo).get(0).getAplcn_carer_proper_num();
      // String aplcn_dtls_proper_num =
      // applicationService.getWorkList(tb_007vo).get(0).getAplcn_dtls_proper_num();
      // tb_007vo.setAplcn_carer_proper_num(proper_num);
      // tb_007vo.setAplcn_dtls_proper_num(aplcn_dtls_proper_num);
      // applicationService.workModify(tb_007vo);
      // }

      return "redirect:/application/applicationWork";
    }
    return "redirect:/";
  }

  // 경력사항 입력 정보 전송
  @PostMapping("/workInfo")
  public ResponseEntity<ArrayList<TB_007VO>> workInfo(@RequestBody TB_007VO tb_007vo, HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      // user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);
      tb_007vo.setUser_id(user_id);
      tb_007vo.setUser_proper_num(member_proper_num);

      ArrayList<TB_007VO> list = applicationService.getWorkList(tb_007vo);
      return new ResponseEntity<>(list, HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

  // 경력사항 수정 팝업창
  @GetMapping("/applicationWorkModifyPopup")
  public String workModifyPopupForm(TB_007VO tb_007vo, Model model, @RequestParam("data") String data,
      @RequestParam("detail") String detail, HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      // user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);
      tb_007vo.setUser_id(user_id);
      tb_007vo.setUser_proper_num(member_proper_num);
      tb_007vo.setAplcn_dtls_proper_num(detail);
      tb_007vo.setAplcn_carer_proper_num(data);

      // 경력 정보 불러오기
      TB_007VO vo = applicationService.getWorkInfo(tb_007vo);
      model.addAttribute("vo", vo);
      return "application/applicationWorkModifyPopup";
    }
    return "redirect:/";
  }

  // 경력사항 수정
  @PostMapping("/workModifyPopupForm")
  public String workModifyPopup(TB_007VO tb_007vo, HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      // user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);
      tb_007vo.setUser_id(user_id);
      tb_007vo.setUser_proper_num(member_proper_num);

      applicationService.workModify(tb_007vo);
      return "redirect:/application/applicationWork?detail=" + tb_007vo.getAplcn_dtls_proper_num();
    }
    return "redirect:/";
  }

  // 경력 정보 삭제
  @PostMapping("/workInfoDelete")
  public ResponseEntity<ArrayList<TB_007VO>> workInfoDelete(@RequestBody TB_007VO tb_007vo,
      HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      // user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);
      tb_007vo.setUser_id(user_id);
      tb_007vo.setUser_proper_num(member_proper_num);

      // if(applicationService.getWorkList(tb_007vo).size() == 1) {
      // applicationService.workUpdate(tb_007vo);
      // } else {
      applicationService.workInfoDelete(tb_007vo);
      // }

      ArrayList<TB_007VO> list = applicationService.getWorkList(tb_007vo);
      return new ResponseEntity<>(list, HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

  // 경력 정보 파일 수정
  @PostMapping("/workFileModify")
  public ResponseEntity<String> workFileModify(@RequestBody Map<String, Object> dataMap, TB_009VO tb_009vo,
      HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      List<String> uuids = (List<String>) dataMap.get("uuids");
      String detail = (String) dataMap.get("detail");

      tb_009vo.setFile_type("CARRER");

      // user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);
      tb_009vo.setUser_id(user_id);
      tb_009vo.setAplcn_dtls_proper_num(detail);
      List<String> keyNames = applicationService.getFilepath(uuids, tb_009vo);

      applicationFileService.ApplicationFileDelete(keyNames);

      return new ResponseEntity<>("응답", HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

  // 자격증 정보 화면으로 이동
  @PostMapping("/workForm")
  public String certificate(TB_005VO tb_005vo, TB_007VO tb_007vo, TB_009VO tb_009vo,
      @RequestParam(value = "file", required = false) List<MultipartFile> list, HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      // user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);

      // 활동 경력, 특기 사항 등록
      tb_007vo.setUser_id(user_id);
      tb_007vo.setUser_proper_num(member_proper_num);
      if (applicationService.getWorkList(tb_007vo).size() == 0) {
        applicationService.workEtcRegist(tb_007vo);
      } else {
        applicationService.workEtcUpdate(tb_007vo);
      }

      // 파일 업로드
      tb_009vo.setUser_id(user_id);
      tb_009vo.setUser_proper_num(member_proper_num);
      tb_009vo.setFile_code("PE");
      tb_009vo.setFile_type("CARRER");
      if (list != null) {

        List<String> fileList = applicationFileService.ApplicationFileRegist(list, tb_007vo.getUser_id());

        applicationService.attachmentRegist(fileList, tb_009vo);
      }
      return "redirect:/application/applicationCertificate?announce=" + tb_005vo.getAnnounce_proper_num()
          + "&fcltt_num=" + tb_005vo.getTrial_fcltt_proper_num() + "&detail=" + tb_009vo.getAplcn_dtls_proper_num();
    }
    return "redirect:/";
  }

  // 자격증 정보 화면
  @GetMapping("/applicationCertificate")
  public String certificateForm(TB_008VO tb_008vo, TB_009VO tb_009vo, Model model, HttpServletRequest request,
      @RequestParam("detail") String detail) {

    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      // user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);
      tb_008vo.setUser_id(user_id);
      tb_009vo.setUser_id(user_id);
      tb_009vo.setFile_type("CERTIFICATE");
      tb_008vo.setAplcn_dtls_proper_num(detail);
      // 자격증 정보 불러오기
      ArrayList<TB_008VO> list = applicationService.getCertificateList(tb_008vo);
      model.addAttribute("list", list);

      // 첨부파일 불러오기
      tb_009vo.setAplcn_dtls_proper_num(detail);
      ArrayList<TB_009VO> fileList = applicationService.fileList(tb_009vo);
      model.addAttribute("fileList", fileList);
      return "application/applicationCertificate";
    }
    return "redirect:/";
  }

  // 자격증 정보 입력 팝업창
  @GetMapping("/applicationCertificatePopup")
  public String certificatePopupForm(HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      return "application/applicationCertificatePopup";
    }
    return "redirect:/";
  }

  // 자격증 정보 등록
  @PostMapping("/certificatePopupForm")
  public String certificatePopup(TB_008VO tb_008vo, HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      // user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);
      tb_008vo.setUser_id(user_id);
      tb_008vo.setUser_proper_num(member_proper_num);

      applicationService.certificateRegist(tb_008vo);
      return "redirect:/application/applicationCertificate";
    }
    return "redirect:/";
  }

  // 자격증 입력 정보 전송
  @PostMapping("/certificateInfo")
  public ResponseEntity<ArrayList<TB_008VO>> certificateInfo(@RequestBody TB_008VO tb_008vo,
      HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      // user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);
      tb_008vo.setUser_id(user_id);
      tb_008vo.setUser_proper_num(member_proper_num);

      ArrayList<TB_008VO> list = applicationService.getCertificateList(tb_008vo);
      return new ResponseEntity<>(list, HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

  // 자격증 정보 수정 팝업창
  @GetMapping("/applicationCertificateModifyPopup")
  public String certificateModifyPopupForm(TB_008VO tb_008vo, Model model, @RequestParam("data") String data,
      @RequestParam("detail") String detail, HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      // user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);
      tb_008vo.setUser_id(user_id);
      tb_008vo.setUser_proper_num(member_proper_num);
      tb_008vo.setAplcn_dtls_proper_num(detail);
      tb_008vo.setAplcn_crtfc_proper_num(data);

      // 자격증 정보 불러오기
      TB_008VO vo = applicationService.getCertificateInfo(tb_008vo);
      model.addAttribute("vo", vo);
      return "application/applicationCertificateModifyPopup";
    }
    return "redirect:/";
  }

  // 자격증 정보 수정
  @PostMapping("/certificateModifyPopupForm")
  public String certificateModifyPopup(TB_008VO tb_008vo, HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      // user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);
      tb_008vo.setUser_id(user_id);
      tb_008vo.setUser_proper_num(member_proper_num);

      applicationService.certificateModify(tb_008vo);
      return "redirect:/application/applicationCertificate";
    }
    return "redirect:/";
  }

  // 자격증 정보 삭제
  @PostMapping("/certificateInfoDelete")
  public ResponseEntity<ArrayList<TB_008VO>> certificateInfoDelete(@RequestBody TB_008VO tb_008vo,
      HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      // user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);
      tb_008vo.setUser_id(user_id);

      applicationService.certificateInfoDelete(tb_008vo);
      ArrayList<TB_008VO> list = applicationService.getCertificateList(tb_008vo);
      return new ResponseEntity<>(list, HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

  // 자격증 정보 파일 수정
  @PostMapping("/certificateFileModify")
  public ResponseEntity<String> certificateFileModify(@RequestBody Map<String, Object> dataMap, TB_009VO tb_009vo,
      HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      List<String> uuids = (List<String>) dataMap.get("uuids");
      String detail = (String) dataMap.get("detail");

      tb_009vo.setFile_type("CERTIFICATE");

      // user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);
      tb_009vo.setUser_id(user_id);
      tb_009vo.setAplcn_dtls_proper_num(detail);
      List<String> keyNames = applicationService.getFilepath(uuids, tb_009vo);

      applicationFileService.ApplicationFileDelete(keyNames);

      return new ResponseEntity<>("응답", HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

  // 증빙서류 화면으로 이동
  @PostMapping("/certificateForm")
  public String attachment(TB_005VO tb_005vo, TB_008VO tb_008vo, TB_009VO tb_009vo,
      @RequestParam(value = "file", required = false) List<MultipartFile> list, HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      // user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);

      // 파일 업로드
      tb_008vo.setUser_id(user_id);
      tb_009vo.setUser_id(user_id);
      tb_009vo.setUser_proper_num(member_proper_num);
      tb_009vo.setFile_code("PE");
      tb_009vo.setFile_type("CERTIFICATE");
      if (list != null) {

        List<String> fileList = applicationFileService.ApplicationFileRegist(list, tb_008vo.getUser_id());

        applicationService.attachmentRegist(fileList, tb_009vo);
      }
      return "redirect:/application/applicationAttachment?announce=" + tb_005vo.getAnnounce_proper_num() + "&fcltt_num="
          + tb_005vo.getTrial_fcltt_proper_num() + "&detail=" + tb_009vo.getAplcn_dtls_proper_num();
    }
    return "redirect:/";
  }

  // 증빙서류 입력 화면
  @GetMapping("/applicationAttachment")
  public String attachmentForm(TB_009VO tb_009vo, Model model, HttpServletRequest request,
      @RequestParam("detail") String detail) {

    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      // user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);
      tb_009vo.setUser_id(user_id);
      tb_009vo.setAplcn_dtls_proper_num(detail);

      String[] file_type = { "BUSINESSLICENSE", "BUSINESSREPORT", "TAXCONFIRM", "RESUME", "EDUCATIONLEVEL", "CARRER",
          "CERTIFICATE", "OTHER" };
      // 첨부파일 불러오기
      for (int i = 0; i < file_type.length; i++) {
        tb_009vo.setFile_type(file_type[i]);
        ArrayList<TB_009VO> fileList = new ArrayList<>();
        fileList = applicationService.fileList(tb_009vo);
        model.addAttribute(file_type[i], fileList);
      }
      return "application/applicationAttachment";
    }
    return "redirect:/";
  }

  // 신청서 제출
  @PostMapping("/attachmentForm")
  public String file(TB_005VO tb_005vo, TB_009VO tb_009vo,
      @RequestParam(value = "co_license", required = false) List<MultipartFile> license,
      @RequestParam(value = "co_report", required = false) List<MultipartFile> report,
      @RequestParam(value = "co_tax", required = false) List<MultipartFile> tax,
      @RequestParam(value = "pe_resume", required = false) List<MultipartFile> resume,
      @RequestParam(value = "pe_edu", required = false) List<MultipartFile> edu,
      @RequestParam(value = "pe_work", required = false) List<MultipartFile> work,
      @RequestParam(value = "pe_cert", required = false) List<MultipartFile> cert,
      @RequestParam(value = "ot_file", required = false) List<MultipartFile> ot_file, HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      // 공통 서류 파일 등록
      String[] co_file_type = { "BUSINESSLICENSE", "BUSINESSREPORT", "TAXCONFIRM" };

      // user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);
      tb_009vo.setUser_id(user_id);
      tb_009vo.setUser_proper_num(member_proper_num);

      List<List<MultipartFile>> co_files = new ArrayList<>();
      co_files.add(license);
      co_files.add(report);
      co_files.add(tax);

      for (int i = 0; i < co_files.size(); i++) {

        tb_009vo.setFile_code("CO");
        tb_009vo.setFile_type(co_file_type[i]);
        if (co_files.get(i) != null) {
          List<String> fileList = applicationFileService.ApplicationFileRegist(co_files.get(i), tb_009vo.getUser_id());

          applicationService.attachmentRegist(fileList, tb_009vo);
        }
      }

      // 개인 서류 파일 등록
      String[] pe_file_type = { "RESUME", "EDUCATIONLEVEL", "CARRER", "CERTIFICATE" };

      List<List<MultipartFile>> pe_files = new ArrayList<>();
      pe_files.add(resume);
      pe_files.add(edu);
      pe_files.add(work);
      pe_files.add(cert);

      for (int i = 0; i < pe_files.size(); i++) {
        tb_009vo.setFile_code("PE");
        tb_009vo.setFile_type(pe_file_type[i]);
        if (pe_files.get(i) != null) {
          List<String> fileList = applicationFileService.ApplicationFileRegist(pe_files.get(i),
              tb_009vo.getUser_id());

          applicationService.attachmentRegist(fileList, tb_009vo);
        }
      }

      // 기타 서류 파일 등록
      tb_009vo.setFile_code("OT");
      tb_009vo.setFile_type("OTHER");
      if (ot_file != null) {
        List<String> fileList = applicationFileService.ApplicationFileRegist(ot_file, tb_009vo.getUser_id());

        applicationService.attachmentRegist(fileList, tb_009vo);
      }

      try {
        Thread.sleep(300);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return "redirect:/application/applicationCheck?announce=" + tb_005vo.getAnnounce_proper_num() + "&fcltt_num="
          + tb_005vo.getTrial_fcltt_proper_num() + "&detail=" + tb_009vo.getAplcn_dtls_proper_num();
    }
    return "redirect:/";
  }

  // 증빙 서류 첨부 파일 수정
  @PostMapping("/attachmentFileModify")
  public ResponseEntity<String> attachmentFileModify(@RequestBody Map<String, Object> requestData,
      TB_009VO tb_009vo, HttpServletRequest request) {

    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      List<String> businesslicense = new ArrayList<>();
      List<String> businessreport = new ArrayList<>();
      List<String> taxconfirm = new ArrayList<>();
      List<String> resume = new ArrayList<>();
      List<String> educationlevel = new ArrayList<>();
      List<String> carrer = new ArrayList<>();
      List<String> certificate = new ArrayList<>();
      List<String> other = new ArrayList<>();

      List<List<String>> file_types = new ArrayList<>();
      file_types.add(businesslicense);
      file_types.add(businessreport);
      file_types.add(taxconfirm);
      file_types.add(resume);
      file_types.add(educationlevel);
      file_types.add(carrer);
      file_types.add(certificate);
      file_types.add(other);

      List<String> uuids = (List<String>) requestData.get("uuids");
      List<String> types = (List<String>) requestData.get("types");
      String detail = (String) requestData.get("detail");

      // user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);
      tb_009vo.setUser_id(user_id);
      tb_009vo.setUser_proper_num(member_proper_num);
      tb_009vo.setAplcn_dtls_proper_num(detail);

      for (int i = 0; i < uuids.size(); i++) {

        String uuid = uuids.get(i);
        String type = types.get(i);

        switch (type) {

          case "BUSINESSLICENSE":
            businesslicense.add(uuid);
            break;
          case "BUSINESSREPORT":
            businessreport.add(uuid);
            break;
          case "TAXCONFIRM":
            taxconfirm.add(uuid);
            break;
          case "RESUME":
            resume.add(uuid);
            break;
          case "EDUCATIONLEVEL":
            educationlevel.add(uuid);
            break;
          case "CARRER":
            carrer.add(uuid);
            break;
          case "CERTIFICATE":
            certificate.add(uuid);
            break;
          case "OTHER":
            other.add(uuid);
            break;
          default:
            break;

        }

      }

      String[] type_list = { "BUSINESSLICENSE", "BUSINESSREPORT", "TAXCONFIRM", "RESUME", "EDUCATIONLEVEL", "CARRER",
          "CERTIFICATE", "OTHER" };

      for (int i = 0; i < file_types.size(); i++) {

        tb_009vo.setFile_type(type_list[i]);
        List<String> keyNames = applicationService.getFilepath(file_types.get(i), tb_009vo);

        applicationFileService.ApplicationFileDelete(keyNames);
      }

      // try {
      // Thread.sleep(300);
      // } catch (Exception e) {
      // e.printStackTrace();
      // }

      return new ResponseEntity<>("응답메시지", HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

  // pdf
  @GetMapping("/applicationCheck")
  public String applicationCheckForm(TB_001VO tb_001vo, TB_002VO tb_002vo, TB_005VO tb_005vo, TB_006VO tb_006vo,
      TB_007VO tb_007vo,
      TB_009VO tb_009vo, TB_011VO tb_011vo, Model model, HttpServletRequest request,
      @RequestParam("announce") String announce, @RequestParam("detail") String detail) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      tb_001vo.setUser_proper_num(member_proper_num);
      tb_005vo.setUser_proper_num(member_proper_num);
      // 기본 정보 페이지 - user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);
      tb_001vo.setUser_id(user_id);
      TB_001VO vo1 = applicationService.getUserInfo(tb_001vo);
      model.addAttribute("tb_001vo", vo1);

      // 기본 정보 - 상세 정보
      tb_005vo.setUser_id(user_id);
      tb_005vo.setAnnounce_proper_num(announce);
      tb_005vo.setAplcn_dtls_proper_num(detail);
      TB_005VO vo5 = userMypageService.usermypage_getapplicationdetail1(tb_005vo);
      model.addAttribute("tb_005vo", vo5);

      TB_010VO vo10 = applicationService.fclttDescription(tb_005vo);
      System.out.println("===========" + vo10.toString());
      model.addAttribute("tb_010vo", vo10);

      ArrayList<TB_011VO> vo11 = userMypageService.usermypage_getapplicationdetail7(tb_005vo);
      model.addAttribute("tb_011vo", vo11);

      // 학력 정보
      ArrayList<TB_006VO> vo6 = applicationService.getEdu(tb_005vo);
      model.addAttribute("tb_006vo", vo6);

      // 경력 정보
      ArrayList<TB_007VO> vo7 = applicationService.getCareer(tb_005vo);
      model.addAttribute("tb_007vo", vo7);

      // 자격증 정보
      ArrayList<TB_008VO> vo8 = applicationService.getCert(tb_005vo);
      model.addAttribute("tb_008vo", vo8);

      // 첨부파일 목록
      ArrayList<TB_009VO> vo9 = userMypageService.usermypage_getapplicationdetail5(tb_005vo);
      model.addAttribute("tb_009vo", vo9);

      return "application/applicationCheck";
    }
    return "redirect:/";
  }

  // 신청서 s3 저장
  @PostMapping("/applicationFile")
  public ResponseEntity<String> applicationFile(@RequestBody Map<String, String> dataMap, TB_005VO tb_005vo,
      TB_009VO tb_009vo, HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      String dataUri = dataMap.get("dataUri");
      String detail = dataMap.get("detail");
      String announce = dataMap.get("announce");

      List<String> filePath = applicationFileService.ApplicationFormRegist(dataUri);

      // user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);

      tb_009vo.setUser_id(user_id);
      tb_009vo.setUser_proper_num(member_proper_num);
      tb_009vo.setFile_code("AR");
      tb_009vo.setFile_type("APPLICATIONRESULT");
      tb_009vo.setAplcn_dtls_proper_num(detail);

      applicationService.attachmentRegist(filePath, tb_009vo);

      tb_005vo.setUser_id(user_id);
      tb_005vo.setUser_proper_num(member_proper_num);
      tb_005vo.setAnnounce_proper_num(announce);

      applicationService.completeUpdate(tb_005vo);

      return new ResponseEntity<>("success", HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

  // 신청완료 화면으로 이동
  @PostMapping("/checkForm")
  public String applicationCheck(TB_009VO tb_009vo, HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      // user_id 가져오기
      String user_id = applicationService.getuser_id(member_proper_num);
      tb_009vo.setUser_id(user_id);
      tb_009vo.setUser_proper_num(member_proper_num);

      return "redirect:/application/applicationComplete";
    }
    return "redirect:/";
  }

  // 신청완료 화면
  @GetMapping("/applicationComplete")
  public String applicationComplete(HttpServletRequest request) {
    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      return "application/applicationComplete";
    }
    return "redirect:/";
  }

}
