package com.court.supporter.adminmypage.service;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.court.supporter.command.TB_001VO;
import com.court.supporter.command.TB_002VO;
import com.court.supporter.command.TB_005VO;
import com.court.supporter.command.TB_013VO;
import com.court.supporter.command.TB_018VO;
import com.court.supporter.user.service.UserService;
import com.court.supporter.usermypage.service.UserMypageService;
import com.court.supporter.util.Criteria;

@Service("adminMypageService")
public class AdminMypageServiceImpl implements AdminMypageService {

  @Autowired
  private AdminMypageMapper adminMypageMapper;

  @Autowired
  @Qualifier("userService")
  private UserService userService;

  @Autowired
  @Qualifier("userMypageService")
  private UserMypageService userMypageService;

  // 관리자 권한 검사
  @Override
  public String adminmypage_authcheck(String member_proper_num) {
    return adminMypageMapper.adminmypage_authcheck(member_proper_num);
  }

  // 관리자페이지 사용자 신청 리스트 총합(사법)
  @Override
  public int adminmypage_juris_getapplicationtotal(Criteria cri) {
    return adminMypageMapper.adminmypage_juris_getapplicationtotal(cri);
  }

  // 관리자페이지 사용자 신청 리스트(사법)
  @Override
  public ArrayList<TB_005VO> adminmypage_juris_getapplicationlist(Criteria cri) {
    return adminMypageMapper.adminmypage_juris_getapplicationlist(cri);
  }

  // 관리자페이지 사용자 신청 리스트 총합(법원)
  @Override
  public int adminmypage_court_getapplicationtotal(Criteria cri) {
    return adminMypageMapper.adminmypage_court_getapplicationtotal(cri);
  }

  // 관리자페이지 사용자 신청 리스트(법원)
  @Override
  public ArrayList<TB_005VO> adminmypage_court_getapplicationlist(Criteria cri) {
    return adminMypageMapper.adminmypage_court_getapplicationlist(cri);
  }

  // 관리자페이지 사용자 신청 리스트 총합(전체)
  @Override
  public int adminmypage_admin_getapplicationtotal(Criteria cri) {
    return adminMypageMapper.adminmypage_admin_getapplicationtotal(cri);
  }

  // 관리자페이지 사용자 신청 리스트(전체)
  @Override
  public ArrayList<TB_005VO> adminmypage_admin_getapplicationlist(Criteria cri) {
    return adminMypageMapper.adminmypage_admin_getapplicationlist(cri);
  }

  @Override
  public TB_005VO adminmypage_getapplicationdetail(String aplcn_dtls_proper_num) {
    return adminMypageMapper.adminmypage_getapplicationdetail(aplcn_dtls_proper_num);
  }

  @Override
  public int adminmypage_checkevaluatecomplete(TB_005VO vo) {
    ArrayList<TB_005VO> list = adminMypageMapper.adminmypage_checkevaluatecomplete(vo);

    int first_count = 0;
    int final_count = 0;

    for (int i = 0; i < list.size(); i++) {
      if (list.get(i).getAplicn_dtls_sts().equals("03") ||
          list.get(i).getAplicn_dtls_sts().equals("04")) { // 1차평가 모든 신청자 심사중 or 서류반려 상태라면?
        first_count++;
      } else if (list.get(i).getAplicn_dtls_date().equals("06") ||
          list.get(i).getAplicn_dtls_date().equals("07")) {// 최종평가 모든 신청자 최종심사중 상태라면?
        final_count++;
      }
    }

    if (first_count == list.size()) {
      return 1; // 1차평가 모든 신청자 심사중 상태
    } else if (first_count != list.size()) {
      return 2; // 1차평가 모든 신청자가 심사중 상태는 아님
    }

    if (final_count == list.size()) {
      return 3; // 최종평가 모든 신청자 최종심사중 상태
    } else if (final_count != list.size()) {
      return 4; // 최종평가 모든 신청자가 최종심사중 상태는 아님
    }

    return 5; // 에러
  }

  @Override
  public int adminmypage_checkannounceenddate(TB_005VO vo) {
    // 오늘날짜
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String today = dateFormat.format(date);

    // 공고종료날짜
    TB_002VO tb_002VO = adminMypageMapper.adminmypage_checkannounceenddate(vo);
    String announce_end = tb_002VO.getAnnounce_end_date();
    try {
      Date todaydate = dateFormat.parse(today);
      Date announce_end_date = dateFormat.parse(announce_end);

      if (todaydate.after(announce_end_date)) { // 오늘날짜가 공고종료날짜보다 이후라면
        return 1; // 1 반환 -> 상대평가 충족조건 만족
      } else {
        return 0; // 0 반환 -> 상대평가 충족조건 불만족
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return 2;
  }

  @Override
  public int adminmypage_evaluate(TB_013VO vo) {
    return adminMypageMapper.adminmypage_evaluate(vo);
  }

  @Override
  public ArrayList<TB_013VO> adminmypage_getresult(TB_005VO vo) {
    return adminMypageMapper.adminmypage_getresult(vo);
  }

  @Override
  public int adminmypage_getauthtotal(Criteria cri) {
    return adminMypageMapper.adminmypage_getauthtotal(cri);
  }

  @Override
  public ArrayList<TB_018VO> adminmypage_getauthlist(Criteria cri) {
    return adminMypageMapper.adminmypage_getauthlist(cri);
  }

  @Override
  public int adminmypage_updateauth(TB_018VO vo) {
    return adminMypageMapper.adminmypage_updateauth(vo);
  }

  @Override
  public String adminmypage_getmember_id(String member_proper_num) {
    return adminMypageMapper.adminmypage_getmember_id(member_proper_num);
  }

  ///////////////////////////////////////////////////////////////////////////////////
  @Override
  public int juris_fail(TB_013VO tb_013vo, TB_005VO tb_005vo, TB_001VO tb_001vo) {
    int result = adminMypageMapper.update04(tb_005vo);

    return result;
  }

  @Override
  public int juris_pass(TB_013VO tb_013vo, TB_005VO tb_005vo, TB_001VO tb_001vo) {
    int result = adminMypageMapper.update05(tb_005vo);

    return result;
  }

  @Override
  public int court_fail(TB_013VO tb_013vo, TB_005VO tb_005vo, TB_001VO tb_001vo) {
    int result = adminMypageMapper.update07(tb_005vo);
    return result;
  }

  @Override
  public int court_pass(TB_013VO tb_013vo, TB_005VO tb_005vo, TB_001VO tb_001vo) {
    int result = adminMypageMapper.update08(tb_005vo);
    ArrayList<TB_005VO> list = adminMypageMapper.adminmypage_get_court_pass(tb_005vo);
    for (int i = 0; i < list.size(); i++) {
      adminMypageMapper.adminmypage_accept(list.get(i)); // 최종 등재
    }
    return result;
  }

  @Override
  public ArrayList<TB_002VO> getannouncelist(Criteria cri) {
    return adminMypageMapper.getannouncelist(cri);
  }

  @Override
  public int getannouncecount(Criteria cri) {
    return adminMypageMapper.getannouncecount(cri);
  }

  @Override
  public ArrayList<TB_013VO> getaplicndetail(String announce_proper_num) {
    // TODO Auto-generated method stub
    return null;
  }

}