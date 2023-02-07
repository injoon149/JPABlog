package com.example.jblog.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jblog.domain.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Integer>{

}
