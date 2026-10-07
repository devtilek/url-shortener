package com.example.shortener.repository;

import com.example.shortener.entity.Click;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ClickRepository extends JpaRepository<Click, Long> {

    long countByLinkId(Long linkId);

    @Query("SELECT MAX(c.clickedAt) FROM Click c WHERE c.link.id = :linkId")
    Instant findLastClickAt(@Param("linkId") Long linkId);

    @Query("SELECT COUNT(DISTINCT c.ipAddress) FROM Click c WHERE c.link.id = :linkId")
    long countUniqueIps(@Param("linkId") Long linkId);

    /** Топ рефереров (кто больше всего редиректит) */
    @Query("""
            SELECT COALESCE(c.referer, 'direct') AS ref, COUNT(c) AS cnt
            FROM Click c
            WHERE c.link.id = :linkId
            GROUP BY c.referer
            ORDER BY cnt DESC
            """)
    List<Object[]> topReferers(@Param("linkId") Long linkId, Pageable pageable);

    /** Клики по дням за период */
    @Query("""
            SELECT CAST(c.clickedAt AS date) AS d, COUNT(c) AS cnt
            FROM Click c
            WHERE c.link.id = :linkId AND c.clickedAt >= :since
            GROUP BY CAST(c.clickedAt AS date)
            ORDER BY d ASC
            """)
    List<Object[]> clicksByDay(@Param("linkId") Long linkId, @Param("since") Instant since);
}