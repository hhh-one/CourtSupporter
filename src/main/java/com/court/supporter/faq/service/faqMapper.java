package com.court.supporter.faq.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import com.court.supporter.command.TB_003VO;
import com.court.supporter.command.TB_004VO;
import com.court.supporter.util.Criteria;

@Mapper
public interface faqMapper {

  public ArrayList<TB_004VO> faqList(@Param("cri") Criteria cri); // 목록

  public int getTotal(@Param("cri") Criteria cri); // 전체 데이터(페이징) 가져오기

  public int faqRegist(TB_004VO vo);

  public TB_004VO faqDetail(String faq_proper_num);

  public TB_004VO faqGetNext(String faq_proper_num);

  public TB_004VO faqGetPerv(String faq_proper_num);

  public int faqModify(TB_004VO vo);

  public int faqUpdate(TB_004VO vo);

  public void faqDelete(String faq_proper_num);
}
