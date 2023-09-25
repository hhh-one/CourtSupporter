package com.court.supporter.application.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import com.court.supporter.command.TB_001VO;
import com.court.supporter.command.TB_002VO;
import com.court.supporter.command.TB_005VO;
import com.court.supporter.command.TB_006VO;
import com.court.supporter.command.TB_007VO;
import com.court.supporter.command.TB_008VO;
import com.court.supporter.command.TB_009VO;
import com.court.supporter.command.TB_010VO;

@Mapper
public interface ApplicationMapper {

	//조력자 분류 번호 조회
	public TB_002VO getFclttNum(String announce);
	//기본 정보 페이지 - 신청인 기본 정보 불러오기
	public TB_001VO getUserInfo(TB_001VO tb_001vo);
	//기본 정보 페이지 - tb_005vo 데이터 가져오기
	public TB_005VO getApplicationBasicInfo(TB_005VO tb_005vo);
	// 기본 정보 페이지 - 희망법원 데이터 가져오기
	public ArrayList<TB_005VO> getCourtList(TB_005VO tb_005vo);
	//기본 정보 페이지 - 주민번호 조회
	public String getUserRrn(@Param("user_proper_num") String user_proper_num, 
							 @Param("user_name") String user_name, 
							 @Param("user_rrn") String user_rrn);
	//기본 정보 페이지 - user_id 불러오기
	public String getuser_id(String user_proper_num);
	//기본 정보 수정
	public void basicDelete(TB_005VO tb_005vo);
	//기본 정보 - 조력자 분류 코드, 조력자 추가 정보, 희망 법원 등록
	public int detailRegist(TB_005VO tb_005vo);
	//기본 정보 페이지 신청인 기본 정보 수정
	public void userInfoUpdate(TB_001VO tb_001vo);
	//학력 정보 등록
	public void educationRegist(TB_006VO tb_006vo);
	//학력 정보 페이지 - 입력한 학력 정보 불러오기
	public ArrayList<TB_006VO> getEducationList(TB_006VO tb_006vo);
	//학력 정보 수정 팝업 - 학력 정보 데이터 불러오기
	public TB_006VO getEducationInfo(TB_006VO tb_006vo);
	//학력 정보 페이지 - 학력 정보 수정
	public void educationModify(TB_006VO tb_006vo);
	//학력 정보 페이지 - 학력 정보 삭제
	public void educationInfoDelete(TB_006VO tb_006vo);
	//학력,경력,자격증 정보 파일 수정
	public List<TB_009VO> getFilepath(@Param("uuid") List<String> uuid, @Param("tb_009vo") TB_009VO tb_009vo);
	public void fileDelete(@Param("uuid") List<String> uuid, @Param("tb_009vo") TB_009VO tb_009vo);

	//학력 정보 페이지 - 최종 학력 입력
	public void finalEducation(TB_006VO tb_006vo);
	//학력,경력,자격증 정보 등록된 첨부 파일 불러오기
	public ArrayList<TB_009VO> fileList(TB_009VO tb_009vo);
	//학력,경력,자격증 정보 첨부 파일 등록
	public void attachmentRegist(TB_009VO tb_009vo);
	//경력 정보 등록
	public void workRegist(TB_007VO tb_007vo);
	//경력 정보 페이지 - 입력한 경력 정보 불러오기
	public ArrayList<TB_007VO> getWorkList(TB_007VO tb_007vo);
	//경력 정보 수정 팝업 - 경력 정보 데이터 불러오기
	public TB_007VO getWorkInfo(TB_007VO tb_007vo);
	//경력 정보 페이지 - 경력 정보 수정
	public void workModify(TB_007VO tb_007vo);
	//경력 정보 페이지 - 경력 정보 삭제
	public void workInfoDelete(TB_007VO tb_007vo);
	//경력 정보 페이지 - 활동 경력, 특기 사항 등록
	public void workEtcRegist(TB_007VO tb_007vo);
	//경력 정보 페이지 - 활동 경력, 특기 사항 수정
	public void workEtcUpdate(TB_007VO tb_007vo);
	//경력 정보 페이지 - 마지막 행 삭제시 경력 사항 수정
	public void workUpdate(TB_007VO tb_007vo);
	//경력 정보 페이지 - 활동 경력, 특기 사항 데이터 불러오기
	public TB_007VO getWorkEtc(TB_007VO tb_007vo);
	//자격증 정보 페이지 - 자격증 정보 등록
	public void certificateRegist(TB_008VO tb_008vo);
	//자격증 정보 페이지 - 입력한 자격증 정보 불러오기
	public ArrayList<TB_008VO> getCertificateList(TB_008VO tb_008vo);
	//자격증 정보 수정 팝업 - 자격증 정보 데이터 불러오기

	public TB_008VO getCertificateInfo(TB_008VO tb_008vo);
	//자격증 정보 페이지 - 자격증 정보 수정
	public void certificateModify(TB_008VO tb_008vo);
	//자격증 정보 페이지 - 자격증 정보 삭제
	public void certificateInfoDelete(TB_008VO tb_008vo);
	//신청서 페이지 - 조력자 분류
	public TB_010VO fclttDescription(TB_005VO tb_005vo);
	//신청서 페이지 - 학력
	public ArrayList<TB_006VO> getEdu(TB_005VO tb_005vo);
	//신청서 페이지 - 경력
	public ArrayList<TB_007VO> getCareer(TB_005VO tb_005vo);
	//신청서 페이지 - 자격증
	public ArrayList<TB_008VO> getCert(TB_005VO tb_005vo);
	//신청완료 업데이트
	public void completeUpdate(TB_005VO tb_005vo);
}
