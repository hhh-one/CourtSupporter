package com.court.supporter.main.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.court.supporter.command.TB_002VO;
import com.court.supporter.command.TB_003VO;
import com.court.supporter.command.TB_004VO;

@Service("mainService")
public class MainServiceImpl implements MainService {

  @Autowired
  private MainMapper mainMapper;

  @Override
  public ArrayList<TB_002VO> getannouncelist(String search) {
    return mainMapper.getannouncelist(search);
  }

  @Override
  public ArrayList<TB_003VO> getnoticelist(String search) {
    return mainMapper.getnoticelist(search);
  }

  @Override
  public ArrayList<TB_004VO> getfaqlist(String search) {
    return mainMapper.getfaqlist(search);
  }

  @Override
  public int getannouncetotal(String search) {
    return mainMapper.getannouncetotal(search);
  }

  @Override
  public int getnoticetotal(String search) {
    return mainMapper.getnoticetotal(search);
  }

  @Override
  public int getfaqtotal(String search) {
    return mainMapper.getfaqtotal(search);
  }

  @Override
  public ArrayList<TB_002VO> getmainannounce() {
    return mainMapper.getmainannounce();
  }

  @Override
  public ArrayList<TB_003VO> getmainnotice() {
    return mainMapper.getmainnotice();
  }

}
