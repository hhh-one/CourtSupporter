package com.court.supporter.announce.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import com.court.supporter.command.TB_017VO;
import com.court.supporter.command.TB_002VO;
import com.court.supporter.command.TB_005VO;
import com.court.supporter.command.TB_010VO;
import com.court.supporter.util.Criteria;

@Mapper
public interface AnnounceMapper {

	 //공고 등록
	 public int announceRegist(TB_002VO vo); //공고 등록 
	 public int announceFileRegist(TB_017VO fileVO);
	 public String getTrial_flctt_proper_num(TB_010VO tb_010VO);
	 public String getTrial_fcltt_description(String trial_fcltt_proper_num);
	 public String getannouncepropernum(TB_002VO vo);
	 
	 //공고 목록 조회	
	 public ArrayList<TB_002VO> announce_getList(@Param("cri") Criteria cri); //공고 게시판(목록) //@Param("cri")
	 public int announce_getTotal(@Param("cri") Criteria cri);		 
	
	 public TB_002VO getDetail(String announce_proper_num); //공고 상세(게시글)	
	 public List<TB_017VO> getFileDetail(String announce_proper_num);
	 public int getUserInfo(@Param("announce_proper_num") String announce_proper_num, @Param("user_proper_num") String user_proper_num); //공고상세 - 신청버튼 클릭시 유저 신청 정보 확인
	 //public List<TB_005VO>  getUserInfo(String user_proper_num); //String announce_proper_num, 
	 
	 public TB_002VO getPrev(String announce_proper_num) ;
	 public TB_002VO getNext(String announce_proper_num) ;	 
	 
	 public int announceModify(TB_002VO vo); //공고 수정 , List<String> filelist 
	 
	 public void announceDelete(String announce_proper_num); //공고 삭제
	 public void announceFileDelete(String announce_proper_num); //file 삭제
	 public String announce_authcheck(String member_proper_num); //관리자 여부 확인
	 public ArrayList<TB_005VO> getUserInfo2(@Param("announce_proper_num") String announce_proper_num, @Param("user_proper_num") String user_proper_num);
		
}
