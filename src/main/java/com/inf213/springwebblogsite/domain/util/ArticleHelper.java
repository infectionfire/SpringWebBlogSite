package com.inf213.springwebblogsite.domain.util;

import com.inf213.springwebblogsite.domain.User;

public abstract class ArticleHelper {
    public static String getAuthorName(User author) {
        return author != null ? author.getUsername() : "<none>";
    }
}
