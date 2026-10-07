package com.example.shortener.repository;

import com.example.shortener.entity.Link;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link, Long> {

    Optional<Link> findByCode(String code);

    boolean existsByCode(String code);

    Page<Link> findAllByUserId(Long userId, Pageable pageable);

    Optional<Link> findByIdAndUserId(Long id, Long userId);

    @Modifying
    @Query("UPDATE Link l SET l.clicksCount = l.clicksCount + 1 WHERE l.id = :id")
    void incrementClicks(@Param("id") Long id);
}