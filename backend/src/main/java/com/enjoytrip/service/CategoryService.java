package com.enjoytrip.service;

import java.util.List;

import com.enjoytrip.model.dto.CategoryDTO;

public interface CategoryService {

	List<CategoryDTO> getAllCategories();

	void createCategory(CategoryDTO categoryDto);

	CategoryDTO getCategoryById(int categoryId);

	void categoryUpdate(CategoryDTO categoryDto);

	boolean categoryDelete(int id);
	
	CategoryDTO findByName(String categoryName);

	List<CategoryDTO> findByCommunityId(int communityId);

}
