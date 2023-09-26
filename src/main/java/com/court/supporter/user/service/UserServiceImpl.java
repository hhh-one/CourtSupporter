package com.court.supporter.user.service;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.court.supporter.command.TB_001VO;
import com.court.supporter.command.TB_005VO;
import com.court.supporter.command.TB_018VO;

import lombok.RequiredArgsConstructor;

@Service("userService")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserMapper userMapper;
  private final JavaMailSender javaMailSender;

  // 회원가입
  @Override
  public int join(TB_001VO tb_001vo) {
    return userMapper.join(tb_001vo);
  }

  // 로그인 시 아이디로 회원 찾기
  @Override
  public TB_018VO findByMemberId(String memberId) {
    return userMapper.findByMemberId(memberId).orElseThrow(RuntimeException::new);
  }

  // 세션에서 claims 받을 때 claims의 proper_num로 회원 찾기
  @Override
  public TB_018VO findByMemberProperNum(String memberProperNum) {
    return userMapper.findByMemberProperNum(memberProperNum).orElseThrow(RuntimeException::new);
  }

  // 회원가입 시 아이디 중복 확인
  @Override
  public int checkId(String userId) {
    return userMapper.checkId(userId);
  }

  // 로그인 시 아이디로 user 탈퇴 여부 확인
  @Override
  public TB_001VO findByUserId(String userId) {
    return userMapper.findByUserId(userId);
  }

  // 메일 인증
  @Override
  public String sendMail(TB_001VO tb_001vo) {
    int number = (int) (Math.random() * 90000) + 100000;
    MimeMessage message = javaMailSender.createMimeMessage();
    try {
      message.setRecipients(MimeMessage.RecipientType.TO, tb_001vo.getUser_email());
      message.setFrom("904lhw@gmail.com");
      message.setSubject("[재판조력자선발] 메일 인증");

      String body = "";
      body += "<h3>요청하신 인증번호입니다.</h3>";
      body += "<h1>" + number + "</h1>";
      body += "<h3>감사합니다.</h3>";

      message.setText(body, "UTF-8", "html");
    } catch (MessagingException e) {
      e.printStackTrace();
    }
    javaMailSender.send(message);
    return String.valueOf(number);
  }

  public MimeMessage createMessage(TB_001VO tb_001VO, TB_005VO tb_005VO, String aplicn_dtls_sts)
      throws MessagingException, UnsupportedEncodingException {

    MimeMessage message = javaMailSender.createMimeMessage();

    message.addRecipients(RecipientType.TO, tb_001VO.getUser_email());// 보내는 대상
    message.setSubject(tb_005VO.getAnnounce_title() + "재판조력자 선발 결과");// 제목

    String test = "";
    String result = "";
    if (aplicn_dtls_sts.equals("04")) {
      test = "1차평가";
      result = "서류반려(1차 불합격)";
    } else if (aplicn_dtls_sts.equals("05")) {
      test = "1차평가";
      result = "1차합격";
    } else if (aplicn_dtls_sts.equals("07")) {
      test = "최종평가";
      result = "불합격(최종 불합격)";
    } else if (aplicn_dtls_sts.equals("08")) {
      test = "최종평가";
      result = "최종합격";
    }

    String msgg = "";
    msgg += "<div style='margin:100px;'>";
    msgg += "<div align='center' style='border:2px solid black; font-family:verdana; height: 230px'>";
    msgg += "<h1> " + tb_001VO.getUser_name() + "님 안녕하세요</h1>";
    msgg += "<p>재판조력자 선발 담당자입니다.</p>";
    msgg += "<p>" + tb_005VO.getAnnounce_title() + "에 대한 신청 결과를 알려드립니다</p>";
    msgg += "<br>";
    msgg += "<h3 style='color:blue;'>" + test + " 결과</h3>";
    msgg += "<h1 style='color:red;'>" + result + "입니다.</h1>";
    msgg += "</div>";
    msgg += "</div>";
    message.setText(msgg, "utf-8", "html");
    message.setFrom(new InternetAddress("CourtSupporter@naver.com", "CourtSupporter_ADMIN"));// 보내는 사람
    javaMailSender.send(message);
    return message;
  }

  // 아이디 찾기 정보에 맞는 user 확인
  @Override
  public String findUsersForId(TB_001VO tb_001vo) {
    return userMapper.findUsersForId(tb_001vo);
  }

  // 비밀번호 찾기 정보에 맞는 user 확인
  @Override
  public String findUsersForPw(TB_001VO tb_001vo) {
    return userMapper.findUsersForPw(tb_001vo);
  }

  // 비밀번호 재설정
  @Override
  public int updatePw(TB_001VO tb_001vo) {
    int tb001Update = userMapper.updatePwTB001(tb_001vo);
    int tb018Update = userMapper.updatePwTB018(tb_001vo);
    if (tb001Update == 1 && tb018Update == 1) {
      return 1;
    }
    return 0;
  }

}
