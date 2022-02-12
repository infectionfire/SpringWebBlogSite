package com.inf213.springwebblogsite.service;

import com.inf213.springwebblogsite.domain.User;
import com.inf213.springwebblogsite.domain.dto.ArticleDto;
import com.inf213.springwebblogsite.repos.ArticleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ArticleService {
    @Autowired
    private ArticleRepo articleRepo;

    public Page<ArticleDto> articleList(Pageable pageable, String filter, User user) {
        if (filter != null && !filter.isEmpty()) {
            return articleRepo.findByTag(filter, pageable, user);
        } else {
            return articleRepo.findAll(pageable, user);
        }
    }

    public Page<ArticleDto> articleListForUser(Pageable pageable, User currentUser, User author) {
        return articleRepo.findByUser(pageable, currentUser, author);
    }
}
