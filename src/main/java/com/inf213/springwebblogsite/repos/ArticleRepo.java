package com.inf213.springwebblogsite.repos;

import com.inf213.springwebblogsite.domain.Article;
import com.inf213.springwebblogsite.domain.User;
import com.inf213.springwebblogsite.domain.dto.ArticleDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ArticleRepo extends CrudRepository<Article, Long> {

    @Query("select new ArticleDto(" +
            "   m, " +
            "   count(ml), " +
            "   sum(case when ml = :user then 1 else 0 end) > 0" +
            ") " +
            "from article m left join m.likes ml " +
            "group by m")
    Page<ArticleDto> findAll(Pageable pageable, @Param("user") User user);

    @Query("select new ArticleDto(" +
            "   m, " +
            "   count(ml), " +
            "   sum(case when ml = :user then 1 else 0 end) > 0" +
            ") " +
            "from article m left join m.likes ml " +
            "where m.tag = :tag " +
            "group by m")
    Page<ArticleDto> findByTag(@Param("tag") String tag, Pageable pageable, @Param("user") User user);

    @Query("select new ArticleDto(" +
            "   m, " +
            "   count(ml), " +
            "   sum(case when ml = :user then 1 else 0 end) > 0" +
            ") " +
            "from article m left join m.likes ml " +
            "where m.author = :author " +
            "group by m")
    Page<ArticleDto> findByUser(Pageable pageable, @Param("author") User author, @Param("user") User user);
}
