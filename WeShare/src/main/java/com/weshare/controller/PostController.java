package com.weshare.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import com.weshare.model.Community;
import com.weshare.model.Post;
import com.weshare.model.User;
import com.weshare.service.AzureBlobAdapter;
import com.weshare.service.CommunityService;
import com.weshare.service.PostService;
import com.weshare.service.UserService;

@Controller
public class PostController
{
	@Autowired
	private UserService userService;
	@Autowired
	private PostService postService;
	@Autowired
	private CommunityService communityService;
	
	@Autowired
    private AzureBlobAdapter azureBlobAdapter;
//	@Autowired
//    private CommunityServiceImpl communityService;
	
	
	@GetMapping("/createpost")
    public String getCreatePost(Model model)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		model.addAttribute("use", user); 
        return "user/createPost";
    }
	
	@PostMapping("/createpost")
    public String addPost(Model model, HttpServletRequest request,Principal principal)
	{
//        System.out.println("\n\n\n create post\n\n\n");
		User user=this.userService.findUserByUserName(principal.getName());
		model.addAttribute("use", user);
        String title = request.getParameter("postTitle");
        int communityId = Integer.parseInt(request.getParameter("communityId"));
        String postType = request.getParameter("postType");
        System.out.println("\n\n\npost tpye: "+postType);
        Post newPost = new Post();
        Community community = communityService.getCommunityById(communityId);
        
        newPost.setTitle(title);
        
        if(postType.equals("normal"))
        {
            String content = request.getParameter("content");
            System.out.println("\n\n\n in if normal \n\n\n: "+content);
            newPost.setContent(content);
            System.out.println("\n\n\n leaving if normal \n\n\n: "+content);
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
        return "redirect:/createpost";
    }
	
	@PostMapping("/createpostwithmedia")
    public RedirectView addMediaPost(Model model, HttpServletRequest request,
    		@RequestParam("postImage") MultipartFile postImage, Principal principal)
	{
		User user=this.userService.findUserByUserName(principal.getName());
		model.addAttribute("use", user);
        String title = request.getParameter("postTitle");
        int communityId = Integer.parseInt(request.getParameter("communityId"));
        String postType = request.getParameter("postType");
        System.out.println("\n\n\npost tpye: "+postType);
        Post newPost = new Post();
        Community community = communityService.getCommunityById(communityId);
        
        newPost.setTitle(title);
		newPost.setUser(user);
    	newPost.setCommunity(community);
    	userService.saveUser(user);
    	postService.savePost(newPost);
    	communityService.saveCommunity(community);
//    	user.addPost(newPost);
//    	community.addPost(newPost);
    	
    	String imageName = newPost.getPostId() + ".png";
    	
//    	MultipartFile postImage = request.getParameter("postImage");
    	try
    	{
    		byte[] bytes = postImage.getBytes();
			
			BufferedOutputStream stream = new BufferedOutputStream(
					new FileOutputStream(new File("src/main/resources/static/postImages/" + imageName)));
			stream.write(bytes);
			stream.close();
			
			boolean temp = azureBlobAdapter.createContainer("postimages");
			System.out.println("\n\n\nblob created? "+temp+"\n\n\n\n");
			URI url = azureBlobAdapter.upload(postImage);
			
    	}
    	catch(Exception e)
    	{
    		System.out.println("\n\n\n\n\nerror while saving:file "+e+" \n\n\n\n");
    	}
    	return new RedirectView("createpost");
	}
	
}
