package com.court.supporter.faq.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.court.supporter.command.TB_003VO;
import com.court.supporter.command.TB_004VO;
import com.court.supporter.util.Criteria;

public interface faqService {

  public ArrayList<TB_004VO> faqList(Criteria cri); // 목록

  public int getTotal(Criteria cri); // 전체 데이터(페이징) 가져오기

  public int faqRegist(TB_004VO vo);

  public TB_004VO faqDetail(String faq_proper_num);

  public TB_004VO faqGetNext(String faq_proper_num);

  public TB_004VO faqGetPrev(String faq_proper_num);

  public int faqModify(TB_004VO vo);

  public int faqUpdate(TB_004VO vo);

  public void faqDelete(String faq_proper_num);
}
