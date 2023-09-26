package com.court.supporter.controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.court.supporter.adminmypage.service.AdminMypageService;
import com.court.supporter.announce.service.AnnounceService;
import com.court.supporter.application.service.ApplicationService;
import com.court.supporter.aws.service.AnnounceFileService;
import com.court.supporter.command.TB_002VO;
import com.court.supporter.command.TB_005VO;
import com.court.supporter.command.TB_010VO;
import com.court.supporter.command.TB_017VO;
import com.court.supporter.security.DefaultUserDetails;
import com.court.supporter.security.jwt.JwtValidator;
import com.court.supporter.util.Criteria;
import com.court.supporter.util.PageVO;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Controller
@RequestMapping("/announce")
@RequiredArgsConstructor
public class AnnounceController {

  private final JwtValidator jwtValidator;

  @Autowired
  @Qualifier("announceService")
  private AnnounceService announceService;

  @Autowired
  @Qualifier("announceFileService")
  private AnnounceFileService announceFileService;

  @Autowired
  @Qualifier("applicationService")
  private ApplicationService applicationService;

  @Autowired
  @Qualifier("adminMypageService")
  private AdminMypageService adminMypageService;

  @Autowired
  private S3Client s3;

  @Value("${aws_bucket_name}")
  private String aws_bucket_name;
  private String uploadPath = "announce/" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

  // 공고 등록 화면으로 이동
  @GetMapping("/announceReg")
  public String announceRegist(HttpServletRequest request, RedirectAttributes ra) { // , RedirectAttributes ra

    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");

    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();
      // token에서 member_role 가져오기
      // role은 Collection으로 받아와서 배열로 바꾼 뒤 roles[0]를 가져오면 됩니다 :)
      Collection<? extends GrantedAuthority> member_role = userDetails.getAuthorities();
      Object[] roles = member_role.toArray();

      String auth = announceService.announce_authcheck(member_proper_num);
      System.out.println(auth);
      if (auth.equals("ROLE_ADMIN")) {
        return "announce/announceReg";
      }
    }
    ra.addFlashAttribute("msg", "접근 권한이 없습니다.");

    return "redirect:/announce/announceList";
  }

  // 등록시 목록으로 이동
  @PostMapping("/announceRegForm")
  public String regist(HttpServletRequest request, TB_002VO vo, TB_010VO tb_010VO, RedirectAttributes ra,
      @RequestParam("file") List<MultipartFile> list) { // @RequestParam("announce_file") List<MultipartFile> list
                                                        // MultipartFile file

    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");

    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();
      // token에서 member_role 가져오기
      // role은 Collection으로 받아와서 배열로 바꾼 뒤 roles[0]를 가져오면 됩니다 :)
      Collection<? extends GrantedAuthority> member_role = userDetails.getAuthorities();
      Object[] roles = member_role.toArray();

      // 임의로 관리자 고유번호 설정
      vo.setAdmin_proper_num(member_proper_num); // "1"
      System.out.println(vo.getAdmin_proper_num());

      // file upload
      list = list.stream().filter(t -> t.isEmpty() == false).collect(Collectors.toList());

      for (MultipartFile file : list) {
        if (file.getContentType().contains("image") == false &&
            file.getContentType().contains("application/pdf") == false &&
            file.getContentType().contains("text/plain") == false &&
            file.getContentType().contains("application/x-hwp") == false) {

          // 허용되지 않는 MIME타입인 경우 처리
          ra.addFlashAttribute("msg", "올바른 파일 형식이 아닙니다");
          return "redirect:/announce/announceList";
        }
      }

      List<String> fileList = announceFileService.announcefileRegist(list);

      String trialNum = announceService.getTrial_flctt_proper_num(tb_010VO);
      vo.setTrial_fcltt_proper_num(trialNum);
      announceService.announceRegist(vo, fileList);

      return "redirect:/announce/announceList";
    }
    return "redirect:/announce/announceList";
  }

  // 공고 목록 게시판 조회 + 검색
  @GetMapping("/announceList")
  public String announceList(HttpServletRequest request, Model model, Criteria cri, RedirectAttributes ra) { // ,
                                                                                                             // @RequestParam(value
                                                                                                             // =
                                                                                                             // "categoryValue",
                                                                                                             // required
                                                                                                             // = false)
                                                                                                             // ,
                                                                                                             // @RequestParam("trial_fcltt_proper_num")
                                                                                                             // int
                                                                                                             // trial_fcltt_proper_num

    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");

    List<TB_002VO> list = announceService.announce_getList(cri);
    int total = announceService.announce_getTotal(cri);
    PageVO pageVO = new PageVO(cri, total);

    // 날짜 비교
    for (TB_002VO vo : list) { // TB_002VO vo
      String endDate = vo.getAnnounce_end_date();
      DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      LocalDate localEndDate = LocalDate.parse(endDate, dateformat); // LocalDate
      LocalDate today = LocalDate.now(); // LocalDate

      if (today.isAfter(localEndDate)) {
        vo.setAnnounceStatus("모집완료");
      } else {
        vo.setAnnounceStatus("모집중");
      }
    }

    System.out.println(cri.getTrial_fcltt_proper_num());
    model.addAttribute("list", list);
    model.addAttribute("pageVO", pageVO);

    if (jwt != null) { // 목록 조회시 로그인이 된 경우
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();
      // token에서 member_role 가져오기 //role은 Collection으로 받아와서 배열로 바꾼 뒤 roles[0]를 가져오면
      // 됩니다 :)
      Collection<? extends GrantedAuthority> member_role = userDetails.getAuthorities();
      Object[] roles = member_role.toArray();
      String auth = announceService.announce_authcheck(member_proper_num);
      // model.addAttribute("member_role", member_role);
      if (auth.equals("ROLE_ADMIN")) {
        model.addAttribute("auth", auth);
        return "announce/announceList";
      }
    }

    return "announce/announceList";
  }

  // 공고 상세 화면(게시글)
  @GetMapping("/announceDetail") // 파일첨부기능도 떠야함
  public String announceDetail(HttpServletRequest request,
      @RequestParam("announce_proper_num") String announce_proper_num, Model model, RedirectAttributes ra) { // @RequestParam("announce_file_proper_num")
                                                                                                             // int
                                                                                                             // announce_file_proper_num)

    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");

    TB_002VO vo = announceService.getDetail(announce_proper_num);
    List<TB_017VO> filevo = announceService.getFileDetail(announce_proper_num);

    // 조력자 종류
    TB_010VO tb_010VO = new TB_010VO();
    String num = vo.getTrial_fcltt_proper_num();
    tb_010VO.setTrial_fcltt_description(num);
    String name = announceService.getTrial_fcltt_description(num);

    // 파일 다운로드. 특수문자로 떠서 수정해야함.
    Map<TB_017VO, String> file = new HashMap<TB_017VO, String>();
    for (int i = 0; i < filevo.size(); i++) {
      file.put(filevo.get(i), filevo.get(i).getOriginal_file_name());
      try {
        filevo.get(i).setOriginal_file_name(URLEncoder.encode(filevo.get(i).getOriginal_file_name(), "UTF-8"));
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
    }

    System.out.println("announce_proper_num: " + announce_proper_num);
    System.out.println("jwt: " + jwt);

    // 이전 글과 다음 글 조회
    TB_002VO previousPost = announceService.getPrev(announce_proper_num);
    TB_002VO nextPost = announceService.getNext(announce_proper_num);

    model.addAttribute("previousPost", previousPost);
    model.addAttribute("nextPost", nextPost);
    model.addAttribute("tb_010VO", tb_010VO);
    model.addAttribute("filevo", filevo);
    model.addAttribute("file", file);
    model.addAttribute("name", name);
    model.addAttribute("vo", vo);

    if (jwt != null) { // 상세화면 로그인 했을 때(= 토큰이 있을 때)
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();

      // token에서 member_proper_num 가져오기
      String member_proper_num = userDetails.getUsername();
      Collection<? extends GrantedAuthority> member_role = userDetails.getAuthorities();
      Object[] roles = member_role.toArray();
      String auth = announceService.announce_authcheck(member_proper_num);
      if (auth.equals("ROLE_ADMIN")) {
        model.addAttribute("auth", auth);
        return "announce/announceDetail";
      }
    }
    return "announce/announceDetail"; // "redirect:/announce/announceList"; //"redirect:/";
  }

  @GetMapping("/applicationAgree") ///
  public String applicationAgree(@RequestParam("announce_proper_num") String announce_proper_num, Model model,
      HttpServletRequest request, RedirectAttributes ra) {
    // @RequestParam("trial_fcltt_proper_num") String trial_fcltt_proper_num

    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");

    if (jwt != null) { // 회원인 경우
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      String member_proper_num = userDetails.getUsername();

      // 회원 고유번호과 공고 고유번호를 매개변수로 받아 해당 데이터가 있다면
      int userInfo = announceService.getUserInfo(announce_proper_num, member_proper_num);
      System.out.println(userInfo);
      ArrayList<TB_005VO> list = announceService.getUserInfo2(announce_proper_num, member_proper_num);

      // user 해당 공고 신청기록이 없다면
      // user 해당 공고 신청기록이 없다면
      if (userInfo == 0) { // int 라면 == 0일 경우,
        // String mainCode = "0" + announce_proper_num.substring(0, 1); //대분류코드 넘길 때
        return "redirect:/application/applicationAgree?announce_proper_num=" + announce_proper_num;
      } else if (list.size() != 0) {
        if (list.get(0).getAplicn_dtls_sts().equals("01")) {
          return "redirect:/application/applicationAgree?announce_proper_num=" + announce_proper_num;
        } else {
          return "redirect:/"; // 에러
        }
      } else { // 이미 해당 공고를 신청한 경우
        ra.addFlashAttribute("result", userInfo);
        return "redirect:/announce/announceList";

      }
    } else { // else if (jwt == null)
      ra.addFlashAttribute("loginresult", 1);
      return "redirect:/announce/announceList";
    }

    // return "redirect:/announce/announceList";
  }

  // 공고 수정 화면
  @GetMapping("/announceModify")
  public String announceModify(HttpServletRequest request,
      @RequestParam("announce_proper_num") String announce_proper_num, Model model, TB_017VO tb_017VO) {

    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");

    // 로그인 했을 때(= 토큰이 있을 때)
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      ;

      // token에서 member_proper_num 가져오기
      String member_proper_num = userDetails.getUsername();
      Collection<? extends GrantedAuthority> member_role = userDetails.getAuthorities();
      Object[] roles = member_role.toArray();

      TB_002VO vo = announceService.getDetail(announce_proper_num);
      vo.setAdmin_proper_num(member_proper_num);
      tb_017VO.setOriginal_file_name(announce_proper_num);
      String file = tb_017VO.getOriginal_file_name();
      model.addAttribute("vo", vo);
      model.addAttribute("file", file);

      System.out.println(vo.toString());

      return "announce/announceModify";

    }
    return "redirect:/announce/announceList";
  }

  // 공고 수정 후 목록으로 이동
  @PostMapping("/announceModifyForm")
  public String modify(HttpServletRequest request, @ModelAttribute("vo") TB_002VO vo, RedirectAttributes ra,
      @RequestParam("file") List<MultipartFile> list, @RequestParam("announce_proper_num") String announce_proper_num,
      TB_010VO tb_010VO) { // , @RequestParam("file") List<MultipartFile> list

    // System.out.println(vo); //확인

    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");

    // 로그인 했을 때(= 토큰이 있을 때)
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
      // token에서 member_proper_num 가져오기
      String member_proper_num = userDetails.getUsername();
      Collection<? extends GrantedAuthority> member_role = userDetails.getAuthorities();
      Object[] roles = member_role.toArray();

      vo.setAdmin_proper_num(member_proper_num); // "1"
      System.out.println(vo.getAdmin_proper_num());
      String trialNum = announceService.getTrial_flctt_proper_num(tb_010VO);
      vo.setTrial_fcltt_proper_num(trialNum);

      // file upload
      list = list.stream().filter(t -> t.isEmpty() == false).collect(Collectors.toList());
      for (MultipartFile file : list) {
        if (file.getContentType().contains("image") == false &&
            file.getContentType().contains("application/pdf") == false &&
            file.getContentType().contains("text/plain") == false &&
            file.getContentType().contains("application/x-hwp") == false) {

          // 허용되지 않는 MIME타입인 경우 처리
          ra.addFlashAttribute("msg", "올바른 파일 형식이 아닙니다");
          return "redirect:/announce/announceList";
        }
      }
      // 파일 업로드하고 저장경로를 리스트로 받음
      List<String> fileList = announceFileService.announcefileRegist(list);
      // 기존업로드 파일 삭제 //데이터 이름 가져오기
      List<TB_017VO> filevo = announceService.getFileDetail(announce_proper_num);

      // 파일삭제
      announceFileService.announceFileDelete(filevo);
      // sql삭제
      announceService.announceFileDelete(announce_proper_num);

      // 파일리스트와 같이 등록 진행
      int result = announceService.announceModify(vo, fileList); // , fileList
      String msg = result == 1 ? "수정 완료" : "수정 실패";
      ra.addFlashAttribute("msg", msg);
      System.out.println(announce_proper_num + "fsdfsafsdfasldfjk;vkj;lk");
      return "redirect:/announce/announceList";
    }
    return "redirect:/announce/announceList";
  }

  // 공고 삭제
  @PostMapping("/deleteForm")
  public String deleteForm(HttpServletRequest request, @RequestParam("announce_proper_num") String announce_proper_num,
      RedirectAttributes ra) {

    HttpSession session = request.getSession();
    String jwt = (String) session.getAttribute("token");

    // 로그인 했을 때(= 토큰이 있을 때)
    if (jwt != null) {
      Authentication authentication = jwtValidator.getAuthentication(jwt);
      DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();

      // token에서 member_proper_num 가져오기
      String member_proper_num = userDetails.getUsername();
      Collection<? extends GrantedAuthority> member_role = userDetails.getAuthorities();
      Object[] roles = member_role.toArray();

      String auth = announceService.announce_authcheck(member_proper_num);
      if (auth.equals("ROLE_ADMIN")) {

        // 기존업로드 파일 삭제 //데이터 이름 가져오기
        List<TB_017VO> filevo = announceService.getFileDetail(announce_proper_num);
        // 파일삭제
        announceFileService.announceFileDelete(filevo);
        // sql삭제
        announceService.announceFileDelete(announce_proper_num);
        announceService.announceDelete(announce_proper_num);
        ra.addFlashAttribute("deleteForm", "삭제하시겠습니까?");
        return "redirect:/announce/announceList";
      }
    }
    return "redirect:/announce/announceList";
  }

}