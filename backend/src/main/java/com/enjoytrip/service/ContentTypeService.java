package com.enjoytrip.service;

import com.enjoytrip.model.dto.ContentTypeDTO;

import java.util.List;

public interface ContentTypeService {
    List<ContentTypeDTO> getAllContentType();
    void insertAllContentType();
}
