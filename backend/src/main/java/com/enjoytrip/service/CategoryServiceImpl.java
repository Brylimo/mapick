package com.enjoytrip.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enjoytrip.model.dao.CategoryDAO;
import com.enjoytrip.model.dao.CommunityCategoryDAO;
import com.enjoytrip.model.dto.CategoryDTO;
import com.enjoytrip.model.dto.TagDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
	private final CategoryDAO cDao;
	private final CommunityCategoryDAO communityCategoryDAO;

	@Override
	@Transactional
	public List<CategoryDTO> getAllCategories() {
		return cDao.getAllCategories();
	}

	@Override
	@Transactional
	public void createCategory(CategoryDTO categoryDto) {
		cDao.createCategory(categoryDto);		
	}

	@Override
	@Transactional
	public CategoryDTO getCategoryById(int categoryId) {
		return cDao.getCategoryById(categoryId);
	}

	@Override
	@Transactional
	public void categoryUpdate(CategoryDTO categoryDto) {
		cDao.categoryUpdate(categoryDto);
	}

	@Override
	@Transactional
	public boolean categoryDelete(int id) {
		return cDao.categoryDelete(id);
	}

	@Override
	@Transactional
	public CategoryDTO findByName(String categoryName) {
		return cDao.findByName(categoryName);
	}
	
	@Override
	@Transactional
	public List<CategoryDTO> findByCommunityId(int communityId) {
		List<CategoryDTO> list = communityCategoryDAO.findByCommunityId(communityId);
		return list;
	}

}
