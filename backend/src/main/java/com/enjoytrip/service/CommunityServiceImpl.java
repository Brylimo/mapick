package com.enjoytrip.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enjoytrip.model.dao.CategoryDAO;
import com.enjoytrip.model.dao.CommunityCategoryDAO;
import com.enjoytrip.model.dao.CommunityDAO;
import com.enjoytrip.model.dao.CommunityMemberDAO;
import com.enjoytrip.model.dao.CommunityTagDAO;
import com.enjoytrip.model.dao.TagDAO;
import com.enjoytrip.model.dto.CategoryDTO;
import com.enjoytrip.model.dto.CommunityCategoryDTO;
import com.enjoytrip.model.dto.CommunityDTO;
import com.enjoytrip.model.dto.CommunityMemberDTO;
import com.enjoytrip.model.dto.CommunityTagDTO;
import com.enjoytrip.model.dto.TagDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {
	private final CommunityDAO communityDao;
	private final CommunityTagDAO communityTagDao;
	private final CommunityCategoryDAO communityCategoryDao;
	private final TagDAO tagDao;
	private final CategoryDAO categoryDao;
	private final CommunityMemberDAO communityMemberDao;
	
	@Override
	@Transactional
	public void createCommunity(CommunityDTO communityDto) {
		// 1. 커뮤니티 저장
		communityDao.insertCommunity(communityDto);
		int communityId = communityDto.getCommunityId();
		
		// 2. 태그 저장 및 연결
        if (communityDto.getTagList() != null) {
            for (TagDTO tag : communityDto.getTagList()) {
                TagDTO existing = tagDao.findByName(tag.getName());
                if (existing == null) {
                    tagDao.createTag(tag);
                    existing = tagDao.findByName(tag.getName());
                }
                CommunityTagDTO communityTagDTO = new CommunityTagDTO();
                communityTagDTO.setTagId(existing.getTagId());
                communityTagDTO.setCommunityId(communityId);
                communityTagDao.insertCommunityTag(communityTagDTO);
            }
        }

        // 3. 카테고리 저장 및 연결
        if (communityDto.getCategoryList() != null) {
            for (CategoryDTO category : communityDto.getCategoryList()) {
                CategoryDTO existing = categoryDao.findByName(category.getCategoryName());
                if (existing == null) {
                    categoryDao.createCategory(category);  
                    existing = categoryDao.findByName(category.getCategoryName());
                }
                CommunityCategoryDTO communityCategoryDto = new CommunityCategoryDTO();
                communityCategoryDto.setCommunityId(communityId);
                communityCategoryDto.setCategoryId(existing.getCategoryId());
                communityCategoryDao.insertCommunityCategory(communityCategoryDto);
            }
        }
        
        // 커뮤니티 방장 저장
        CommunityMemberDTO communityMemberDto = new CommunityMemberDTO();
        communityMemberDto.setCommunityId(communityId);
        communityMemberDto.setMemberId(communityDto.getRegisteredBy());
        communityMemberDao.insertCommunityMember(communityMemberDto);
    }
	
	@Override
	@Transactional
	public CommunityDTO getCommunitiesByCommunityName(String communityName) {
		CommunityDTO result = communityDao.findByName(communityName);
		return result;
	}

	@Override
	@Transactional
	public List<CommunityDTO> getCommunitiesByCategory(String categoryName) {
		CategoryDTO categoryDto = categoryDao.findByName(categoryName);
		if (categoryDto == null) {
			return new ArrayList<>();
		}
		List<CommunityDTO> list = communityCategoryDao.findByCategoryId(categoryDto.getCategoryId());
		return list;
	}

	@Override
	@Transactional
	public List<CommunityDTO> getCommunitiesByTag(String tagName) {
		TagDTO tagDto = tagDao.findByName(tagName);
		if (tagDto == null) {
			return new ArrayList<>();
		}
		List<CommunityDTO> list = communityTagDao.findByTagId(tagDto.getTagId());
		return list;
	}

	@Override
	@Transactional
	public List<CommunityDTO> getAllCommunities() {
		return communityDao.getAllCommunities();
	}

	@Override
	@Transactional
	public CommunityDTO getCommunityById(int communityId) {
		return communityDao.findById(communityId);
	}

	@Override
	@Transactional
	public void updateCommunity(CommunityDTO communityDto) {
		communityDao.updateCommunity(communityDto);
		int communityId = communityDto.getCommunityId();
		communityTagDao.deleteByCommunityId(communityId);
		communityCategoryDao.deleteByCommunityId(communityId);
		communityDto = communityDao.findById(communityId);
		System.out.println("update: " + communityDto);
		if(communityDto.getTagList() != null) {
            for (TagDTO tag : communityDto.getTagList()) {
                TagDTO existing = tagDao.findByName(tag.getName());
                if (existing == null) {
                    tagDao.createTag(tag);
                    existing = tagDao.findByName(tag.getName());
                }
                CommunityTagDTO communityTagDTO = new CommunityTagDTO();
                communityTagDTO.setTagId(existing.getTagId());
                communityTagDTO.setCommunityId(communityId);
                communityTagDao.insertCommunityTag(communityTagDTO);
            }
	     }
		
		if (communityDto.getCategoryList() != null) {
            for (CategoryDTO category : communityDto.getCategoryList()) {
                CategoryDTO existing = categoryDao.findByName(category.getCategoryName());
                if (existing == null) {
                    categoryDao.createCategory(category);  
                    existing = categoryDao.findByName(category.getCategoryName());
                }
                CommunityCategoryDTO communityCategoryDto = new CommunityCategoryDTO();
                communityCategoryDto.setCommunityId(communityId);
                communityCategoryDto.setCategoryId(existing.getCategoryId());
                communityCategoryDao.insertCommunityCategory(communityCategoryDto);
            }
        }
	}

	@Override
	@Transactional
	public void communityDelete(int communityId) {
		// 커뮤니티 태그 테이블 삭제
		communityTagDao.deleteByCommunityId(communityId);
		// 커뮤니티 카테고리 테이블 삭제
		communityCategoryDao.deleteByCommunityId(communityId);
		// 커뮤니티 멤버 테이블 삭제
		communityMemberDao.deleteByCommunityId(communityId);
		// 커뮤니티 테이블 삭제
		communityDao.deleteById(communityId);
	}

	@Override
	@Transactional
	public List<CommunityDTO> getTop3PopularCommunities() {
		return communityDao.getTopLikedCommunities();
	}


}
