package com.weshare.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.weshare.model.Community;
import com.weshare.model.Post;
import com.weshare.model.User;
import com.weshare.service.CommunityService;
import com.weshare.service.PostService;
import com.weshare.service.UserService;

@Controller
@RequestMapping("/user/community")
public class PostController
{
	@Autowired
	private UserService userService;
	@Autowired
	private PostService postService;
	@Autowired
	private CommunityService communityService;
	
	
//	@Autowired
//    private CommunityServiceImpl communityService;
	
	@GetMapping("{communityName}/createPost")
    public String getCreatePost(Model model, Principal principal,
    		@PathVariable String communityName )
	{
		User user=this.userService.findUserByUserName(principal.getName());
		model.addAttribute("use", user);
//		System.out.println("\n\n\n"+communityName+"\n\n\n");
		Community community = communityService.getCommunityByName(communityName);
		if(community==null)
	    {
			return "404";
		}
//		System.out.println("\n\n\n"+community+"\n\n\n");
		model.addAttribute("community", community);
		
		return "user/createPost";
    }
	
	@PostMapping("{communityName}/createPost")
    public String addPost(Model model, HttpServletRequest request,
    		Principal principal, @PathVariable String communityName)
	{
//        System.out.println("\n\n\n create post\n\n\n");
		User user=this.userService.findUserByUserName(principal.getName());
		model.addAttribute("use", user);
        String title = request.getParameter("postTitle");
        String postType = request.getParameter("postType");
        System.out.println("\n\n\npost tpye: "+postType);
        Post newPost = new Post();
        
        Community community = communityService.getCommunityByName(communityName);
		if(community==null)
	    {
			return "404";
		}
        newPost.setTitle(title);
        
        if(postType.equals("normal"))
        {
            String content = request.getParameter("postDescription");
            System.out.println("\n\n\nin if content is: "+content+"\n\n\n");
        	newPost.setContent(content);
        }
        else if(postType.equals("link"))
        {
        	String link = request.getParameter("postLink");
        	newPost.setLink(link);
        }
        newPost.setTitle(title);
		newPost.setUser(user);
    	newPost.setCommunity(community);
//    	userService.saveUser(user);
    	postService.savePost(newPost);
//    	communityService.saveCommunity(community);
//    	user.addPost(newPost);
//    	community.addPost(newPost);
        return "redirect:/user/community/"+communityName;
    }
	
	@PostMapping("{communityName}/createPostWithMedia")
    public String addMediaPost(Model model, HttpServletRequest request,
    		@RequestParam("postImage") MultipartFile postImage, Principal principal
    		, @PathVariable String communityName)
	{
		System.out.println("\n\n\n createPostWithMedia "+communityName+"\n\n\n");
		User user=this.userService.findUserByUserName(principal.getName());
		model.addAttribute("use", user);
        String title = request.getParameter("postTitle");
        String postType = request.getParameter("postType");
        System.out.println("\n\n\npost tpye: "+postType);
        Post newPost = new Post();
        Community community = communityService.getCommunityByName(communityName);
		if(community==null)
	    {
			return "404";
		}
        newPost.setTitle(title);
		newPost.setUser(user);
    	newPost.setCommunity(community);
    	postService.savePost(newPost);
    	
    	newPost.setImageUrl(newPost.getPostId()+"");
    	postService.savePost(newPost);
    	
    	String imageName = newPost.getPostId() + ".png";
    	
    	try
    	{
    		byte[] bytes = postImage.getBytes();
			
			BufferedOutputStream stream = new BufferedOutputStream(
					new FileOutputStream(new File("src/main/resources/static/postImages/" + imageName)));
			stream.write(bytes);
			stream.close();
    	}
    	catch(Exception e)
    	{
    		System.out.println("\n\n\n\n\nerror while saving:file "+e+" \n\n\n\n");
    	}
    	return "redirect:/user/community/"+communityName;
	}

}
