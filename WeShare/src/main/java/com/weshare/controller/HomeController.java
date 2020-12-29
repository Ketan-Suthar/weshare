package com.weshare.controller;

import java.security.Principal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.weshare.model.Community;
import com.weshare.model.Pager;
import com.weshare.model.Post;
import com.weshare.model.User;
import com.weshare.service.CommunityService;
import com.weshare.service.PostService;
import com.weshare.service.SavePostService;
import com.weshare.service.UserService;
import com.weshare.service.VoteService;

@Controller
public class HomeController {
	
	private static final int NUM_OF_BUTTONS = 5;
    private static final int INITIAL_PAGE = 0;
    private static final int INITIAL_PAGE_SIZE = 10;
    private static final int[] PAGE_SIZES = {5, 10, 15};

	@Autowired
	private UserService userService;

	@Autowired
	private PostService postService;
	
	@Autowired
	private SavePostService savePostService;
	
	@Autowired
	private VoteService voteService;
	
	@Autowired
	private CommunityService communityService;

	@GetMapping(value = { "/", "/login" })
	public String index(Model model) {
		User user = new User();
		model.addAttribute("user", user);
		model.addAttribute("classActiveLogin", true);
		return "index";
	}

	@GetMapping("/admin/home")
	public String adminHome(Model model) {
		
		List<Post> posts = postService.getAllPosts().stream()
				  .sorted(Comparator.comparing(Post::getCreationDate).reversed())
				  .collect(Collectors.toList());
		List<Community> topCommunities = communityService.findTopCommunities(5);
		
		model.addAttribute("posts", posts);
		model.addAttribute("topCommunities", topCommunities);
		
		return "admin/home";
	}
	
	@GetMapping("/user/home")
	public String userHome(Model model, Principal principal,
			@RequestParam("pageSize") Optional<Integer> pageSize)
	{
		User user=this.userService.findUserByUserName(principal.getName());
		
		int setPageSize = pageSize.orElse(INITIAL_PAGE_SIZE);
		
		Set<Community> joinedCommunities = user.getJoinedCommunityList();
		if(joinedCommunities.isEmpty())
		{
			model.addAttribute("notJoined",true);
		}		
		
		List<Community> topCommunities = communityService.findTopCommunities(5);
		
//		model.addAttribute("posts", null);
		model.addAttribute("pageSizes", PAGE_SIZES);
		model.addAttribute("selectedPageSize", setPageSize);
		model.addAttribute("savePostService",savePostService);
		model.addAttribute("voteService", voteService);
		model.addAttribute("user", user);
		model.addAttribute("joinedCommunities", joinedCommunities);
		model.addAttribute("topCommunities", topCommunities);
		return "user/home_new";
	}
	
	
	@PostMapping("/user/home/getData")
	@ResponseBody
	public HashMap<String, Object> getHomePageData(Model model, Principal principal,
			@RequestParam("pageSize") Optional<Integer> pageSize,
            @RequestParam("page") Optional<Integer> page)
	{
		User user=this.userService.findUserByUserName(principal.getName());
		HashMap<String, Object> data = new HashMap<String, Object>();
		System.out.println("\n\ninsize /user/home/getData\n");
		int setPageSize = pageSize.orElse(INITIAL_PAGE_SIZE);
        int setPage = (page.orElse(0) < 1) ? INITIAL_PAGE : page.get() - 1;
		
		Page<Post> posts = postService.findByJoinedCommunitiesByPage(user.getId(),
				PageRequest.of(setPage, setPageSize, Sort.by(Sort.Direction.DESC, "creation_date")));
		
		Pager postsPager = new Pager(posts.getTotalPages(),
				posts.getNumber(), NUM_OF_BUTTONS);
		
		data.put("posts", posts);
		data.put("postsPager", postsPager);
		data.put("pageSizes", PAGE_SIZES);
		data.put("selectedPageSize", setPageSize);
		data.put("user", user);
//		model.addAttribute("posts", posts);
//		model.addAttribute("postsPager", postsPager);
//		model.addAttribute("pageSizes", PAGE_SIZES);
//		model.addAttribute("selectedPageSize", setPageSize);
//		model.addAttribute("user", user);
		return data;
//		return new ResponseEntity<>(model, 
//				   HttpStatus.OK);
	}
}
