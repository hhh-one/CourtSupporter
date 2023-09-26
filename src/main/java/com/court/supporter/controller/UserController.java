
package com.court.supporter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.court.supporter.command.TB_001VO;
import com.court.supporter.command.TB_018VO;
import com.court.supporter.security.jwt.JwtToken;
import com.court.supporter.user.service.UserLoginService;
import com.court.supporter.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  private final UserService userService;
  private final UserLoginService userLoginService;

  // 회원가입
  @PostMapping("/joinForm")
  public ResponseEntity<Integer> join(@RequestBody @Validated TB_001VO tb_001vo) {
    // 비밀번호 암호화
    String pw = bCryptPasswordEncoder.encode(tb_001vo.getUser_pw());
    tb_001vo.setUser_pw(pw);

    return ResponseEntity.ok(userService.join(tb_001vo));
  }

  // 로그인
  @PostMapping("/login")
  public ResponseEntity<JwtToken> login(@RequestBody TB_018VO tb_018vo) {
    return ResponseEntity.ok(userLoginService.login(tb_018vo));
  }

  // 아이디 중복확인
  @PostMapping("/checkId")
  public ResponseEntity<Integer> checkId(@RequestBody String userId) {
    userId = userId.replace("=", "");
    System.out.println(userId);
    return ResponseEntity.ok(userService.checkId(userId));
  }

  // 회원가입 메일 인증
  @PostMapping("/mail")
  public ResponseEntity<String> sendMail(@RequestBody TB_001VO tb_001vo) {
    return ResponseEntity.ok(userService.sendMail(tb_001vo));
  }

  // 아이디 찾기 정보에 맞는 user 확인
  @PostMapping("/findUsers")
  public ResponseEntity<String> findUsersForId(@RequestBody TB_001VO tb_001vo) {
    System.out.println(tb_001vo.toString());
    if (tb_001vo.getUser_id() == null) {
      return ResponseEntity.ok(userService.findUsersForId(tb_001vo));
    } else {
      return ResponseEntity.ok(userService.findUsersForPw(tb_001vo));
    }
  }

  // 비밀번호 재설정
  @PostMapping("/updatePw")
  public ResponseEntity<Integer> updatePw(@RequestBody TB_001VO tb_001vo) {
    // 비밀번호 암호화
    String pw = bCryptPasswordEncoder.encode(tb_001vo.getUser_pw());
    tb_001vo.setUser_pw(pw);
    return ResponseEntity.ok(userService.updatePw(tb_001vo));
  }
}
