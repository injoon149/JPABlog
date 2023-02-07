package com.example.jblog.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jblog.domain.Post;

public interface PostRepository extends JpaRepository<Post, Integer>{

}
