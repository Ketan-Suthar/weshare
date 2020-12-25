package com.weshare.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.weshare.model.Category;
import com.weshare.model.Community;
import com.weshare.service.CategoryService;
import com.weshare.service.CommunityService;

@Controller
@RequestMapping("/user")
public class UserCommunityController {

	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private CommunityService communityService;
	
	
	@GetMapping("/category/all/communities")
	public String communityList(Model m)
	{
		List<Category> categoryList = categoryService.getAllCategories();
		m.addAttribute("catList", categoryList);
		
		List<Community> communityList=communityService.getAllCommunities();
		m.addAttribute("comList", communityList);
		
		m.addAttribute("allCom",true);
//		return "user/FindAllCommunitiesByCategory";
		return "user/getAllCommunities";
	}
	
	
	@GetMapping("/category/{categoryName}/communities")
	public String searchByCategory(@PathVariable("categoryName")String cname, Model m)
	{
		Category c=categoryService.getCategoryByName(cname);
		List<Community> communityList = communityService.findCommunitiesByCategory(c);
		
		if (communityList.isEmpty()) {
			m.addAttribute("emptyList", true);
		}
		
		m.addAttribute("comList", communityList);
		
		List<Category> categoryList = categoryService.getAllCategories();
		m.addAttribute("catList", categoryList);
		m.addAttribute("categoryName",cname);
		
		return "user/getAllCommunities";
	}
	
}
