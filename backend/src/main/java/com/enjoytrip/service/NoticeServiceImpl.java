package com.enjoytrip.service;

import java.util.List;

import com.enjoytrip.model.dao.NoticeDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.enjoytrip.model.dto.NoticeDTO;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

	private final NoticeDAO noticeDAO;

	@Override
	@Transactional
	public void createNotice(NoticeDTO notice) {
		noticeDAO.insertNotice(notice);
	}

	@Override
	@Transactional
	public NoticeDTO getNoticeById(long id) {
		return noticeDAO.getNoticeById(id);
	}

	@Override
	@Transactional
	public List<NoticeDTO> getAllNotices() {
		return noticeDAO.getAllNotices();
	}

	@Override
	@Transactional
	public boolean noticeDelete(long noticeId) {
		NoticeDTO notice = noticeDAO.getNoticeById(noticeId);
		if (notice != null) {
			noticeDAO.deleteNotice(noticeId);
			return true;
		}
		return false;
	}

	@Override
	@Transactional
	public void noticeUpdate(NoticeDTO noticeDto) {
		noticeDAO.updateNotice(noticeDto);
	}
}
