package com.enjoytrip.service;

import com.enjoytrip.model.dao.ContentTypeDAO;
import com.enjoytrip.model.dto.ContentTypeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentTypeServiceImpl implements ContentTypeService {
    private final ContentTypeDAO contentTypeDao;

    @Override
    @Transactional
    public List<ContentTypeDTO> getAllContentType() {
        return contentTypeDao.getAllContentType();
    }

    @Override
    @Transactional
    public void insertAllContentType() {
        List<ContentTypeDTO> list = new ArrayList<>();
        list.add(new ContentTypeDTO(12, "관광지"));
        list.add(new ContentTypeDTO(14, "문화시설"));
        list.add(new ContentTypeDTO(15, "행사/공연/축제"));
        list.add(new ContentTypeDTO(25, "여행코스"));
        list.add(new ContentTypeDTO(28, "레포츠"));
        list.add(new ContentTypeDTO(32, "숙박"));
        list.add(new ContentTypeDTO(38, "쇼핑"));
        list.add(new ContentTypeDTO(39, "음식점"));

        contentTypeDao.insertContentTypeList(list);
    }
}
