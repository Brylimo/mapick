package com.enjoytrip.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.enjoytrip.model.dto.CategoryDTO;

@Mapper
public interface CategoryDAO {

	List<CategoryDTO> getAllCategories();

	void createCategory(CategoryDTO categoryDto);

	CategoryDTO getCategoryById(int categoryId);

	void categoryUpdate(CategoryDTO categoryDto);

	boolean categoryDelete(int id);

	CategoryDTO findByName(String categoryName);
	
}
