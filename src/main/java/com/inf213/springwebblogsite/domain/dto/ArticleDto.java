package com.inf213.springwebblogsite.domain.dto;

import com.inf213.springwebblogsite.domain.Article;
import com.inf213.springwebblogsite.domain.User;
import com.inf213.springwebblogsite.domain.util.ArticleHelper;

public class ArticleDto {
    private Long id;
    private String content;
    private String tag;
    private User author;
    private String filename;

    public ArticleDto(Article article) {
        this.id = article.getId();
        this.content = article.getContent();
        this.tag = article.getTag();
        this.author = article.getAuthor();
        this.filename = article.getFilename();

    }

    public String getAuthorName() {
        return ArticleHelper.getAuthorName(author);
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getTag() {
        return tag;
    }

    public User getAuthor() {
        return author;
    }

    public String getFilename() {
        return filename;
    }


    @Override
    public String toString() {
        return "MessageDto{" +
                "id=" + id +
                ", author=" + author +
                '}';
    }
}
