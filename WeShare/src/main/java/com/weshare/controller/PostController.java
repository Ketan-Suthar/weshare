package com.weshare.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

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
import com.weshare.service.impl.CommunityServiceImpl;
import com.weshare.service.impl.PostServiceImpl;
//import com.weshare.service.impl.CommunityServiceImpl;
import com.weshare.service.impl.UserServiceImpl;

//for azure
//package blobQuickstart.blobAzureApp;


//import com.microsoft.azure.storage.*;
//import com.microsoft.azure.storage.blob.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Scanner;


@Controller
public class PostController
{
	private final UserServiceImpl userServiceImpl;
	private final PostServiceImpl postServiceImpl;
	private final CommunityServiceImpl communityServiceImp;
//	@Autowired
//    private CommunityServiceImpl communityService;
	@Autowired
    public PostController(UserServiceImpl userServiceImpl,
    			PostServiceImpl postServiceImpl,
    			CommunityServiceImpl communityServiceImp) 
	{
        this.userServiceImpl = userServiceImpl;
        this.postServiceImpl = postServiceImpl;
        this.communityServiceImp = communityServiceImp;
    }
	
	@GetMapping("/createpost")
    public String getCreatePost(Model model)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userServiceImpl.findUserByUserName(auth.getName());
		model.addAttribute("use", user); 
        return "user/createPost";
    }
	
	@PostMapping("/createpost")
    public RedirectView addPost(Model model, HttpServletRequest request)
	{
//        System.out.println("\n\n\n create post\n\n\n");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userServiceImpl.findUserByUserName(auth.getName());
		model.addAttribute("use", user);
        String title = request.getParameter("postTitle");
        int communityId = Integer.parseInt(request.getParameter("communityId"));
        String postType = request.getParameter("postType");
        System.out.println("\n\n\npost tpye: "+postType);
        Post newPost = new Post();
        Community community = communityServiceImp.getCommunityById(communityId);
        
        newPost.setTitle(title);
        
        if(postType.equals("normal"))
        {
            String content = request.getParameter("postDesciption");
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
    	userServiceImpl.saveUser(user);
    	postServiceImpl.savePost(newPost);
    	communityServiceImp.saveCommunity(community);
    	user.addPost(newPost);
    	community.addPost(newPost);
        return new RedirectView("createpost");
    }
	
	@PostMapping("/createpostwithmedia")
    public RedirectView addMediaPost(Model model, HttpServletRequest request,
    		@RequestParam("postImage") MultipartFile postImage)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userServiceImpl.findUserByUserName(auth.getName());
		model.addAttribute("use", user);
        String title = request.getParameter("postTitle");
        int communityId = Integer.parseInt(request.getParameter("communityId"));
        String postType = request.getParameter("postType");
        System.out.println("\n\n\npost tpye: "+postType);
        Post newPost = new Post();
        Community community = communityServiceImp.getCommunityById(communityId);
        
        newPost.setTitle(title);
		newPost.setUser(user);
    	newPost.setCommunity(community);
    	userServiceImpl.saveUser(user);
    	postServiceImpl.savePost(newPost);
    	communityServiceImp.saveCommunity(community);
    	user.addPost(newPost);
    	community.addPost(newPost);
    	
    	String imageName = newPost.getPostId() + ".png";
    	
//    	MultipartFile postImage = request.getParameter("postImage");
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
    	return new RedirectView("createpost");
	}
	
}
