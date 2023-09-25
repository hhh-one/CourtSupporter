package com.court.supporter.command;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TB_005VO {

   private String aplcn_dtls_proper_num;
   private String ligtn_case_carer_yn;
   private String ligtn_case_carer_etc;
   private String insrn_indst_carer_yn;
   private String insrn_indst_carer_etc;
   private String criminal_penalty_carer_yn;
   private String criminal_penalty_carer_etc;
   private String aplicn_dtls_sts;
   private String aplicn_dtls_date;
   private String user_proper_num;
   private Integer trial_fcltt_proper_num;
   private Integer court_proper_num;
   private String user_id;

   //멀티셀렉트 값
   private List<Integer> court_proper_num_list;
   
   //TB_002VO join
   private String announce_proper_num;
   private String announce_title;
   
   //TB_010VO join
   private String trial_fcltt_description;
   
   //TB_011VO join
   private String court_name;
}

