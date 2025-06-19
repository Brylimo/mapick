package com.enjoytrip.model.dao;

import com.enjoytrip.model.dto.NoticeDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NoticeDAO {
    void insertNotice(NoticeDTO notice);
    NoticeDTO getNoticeById(long id);
    List<NoticeDTO> getAllNotices();
    void updateNotice(NoticeDTO notice);
    void deleteNotice(long id);
}
