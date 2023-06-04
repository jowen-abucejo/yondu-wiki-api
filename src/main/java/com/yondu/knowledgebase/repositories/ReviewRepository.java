package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {


    Page<Review> findAllByStatus (String status, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE lower(r.pageVersion.title) like %:title%")
    Page<Review> findAllByPageVersionTitle(String title, Pageable pageable);

    Page<Review> findAll (Pageable pageable);
}
