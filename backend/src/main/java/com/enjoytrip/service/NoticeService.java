package com.enjoytrip.service;

import java.util.List;

import com.enjoytrip.model.dto.NoticeDTO;

public interface NoticeService {
	void createNotice(NoticeDTO notice);
	NoticeDTO getNoticeById(long id);
	List<NoticeDTO> getAllNotices();
	boolean noticeDelete(long noticeId);
	void noticeUpdate(NoticeDTO noticeDto);
}
