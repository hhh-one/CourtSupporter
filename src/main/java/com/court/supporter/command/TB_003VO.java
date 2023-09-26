package com.court.supporter.command;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TB_003VO {
  // faq 테이블
  private String notice_proper_num;
  private String notice_title;
  private String notice_content;
  private String notice_date;
  private String admin_proper_num;
}
