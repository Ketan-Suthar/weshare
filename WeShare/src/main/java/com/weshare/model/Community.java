package com.weshare.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Data
public class Community {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int communityId;
	
	@Column(unique=true)
	private String communityName;
	
	@Column(length=3000)
	private String communityDescription;
	
    @CreationTimestamp
    private Date creationDate;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
	@ManyToOne
	@JoinColumn(name="category_id")
	private Category category;
	
	@JsonIgnore
	@OneToMany(mappedBy="community")
    private List<Post> posts;
	
	private int membersCount;
}
