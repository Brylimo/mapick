package com.enjoytrip.model.dao;

import com.enjoytrip.model.dto.ContentTypeDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ContentTypeDAO {
    List<ContentTypeDTO> getAllContentType();
    void insertContentTypeList(List<ContentTypeDTO> list);
}
