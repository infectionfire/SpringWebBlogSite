package com.inf213.springwebblogsite.controllers;

import com.inf213.springwebblogsite.domain.Article;
import com.inf213.springwebblogsite.domain.User;
import com.inf213.springwebblogsite.domain.dto.ArticleDto;
import com.inf213.springwebblogsite.repos.ArticleRepo;
import com.inf213.springwebblogsite.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Controller
public class BlogController {

    @Autowired
    private ArticleRepo articleRepo;

    @Autowired
    private ArticleService articleService;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/main")//добавить вью
    public String main(
            @RequestParam(required = false, defaultValue = "") String filter,
            Model model,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal User user
    ) {
        Page<ArticleDto> page = articleService.articleList(pageable, filter, user);

        model.addAttribute("page", page);
        model.addAttribute("url", "/main");
        model.addAttribute("filter", filter);

        return "main";
    }


    @PostMapping("/main")
    public String add(
            @AuthenticationPrincipal User user,
            @Valid Article article,
            BindingResult bindingResult,
            Model model,
            @RequestParam(required = false, defaultValue = "") String filter,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam("file") MultipartFile file
                                    ) throws IOException {
        article.setAuthor(user);

        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);

            model.mergeAttributes(errorsMap);
            model.addAttribute("article", article);
        } else {
            saveFile(article, file);

            model.addAttribute("article", null);

            articleRepo.save(article);
        }

        Page<ArticleDto> page = articleService.articleList(pageable, filter, user);
        model.addAttribute("page", page);

        return "main";
    }


    @GetMapping("/blog")
    public String blog(Model model) {
        model.addAttribute("title", "Блог");
        return "blog";
    }


    @GetMapping("/user-messages/{author}")
    public String userArticles(
            @AuthenticationPrincipal User currentUser,
            @PathVariable User author,
            Model model,
            @RequestParam(required = false) Article article,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ArticleDto> page = articleService.articleListForUser(pageable, currentUser, author);

        model.addAttribute("userChannel", author);
        model.addAttribute("page", page);
        model.addAttribute("article", article);
        model.addAttribute("isCurrentUser", currentUser.equals(author));
        model.addAttribute("url", "/user-messages/" + author.getId());

        return "userMessages";
    }

    @PostMapping("/user-messages/{user}")
    public String updateMessage(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long user,
            @RequestParam("id") Article article,
            @RequestParam("content") String content,
            @RequestParam("tag") String tag,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        if (article.getAuthor().equals(currentUser)) {
            if (!StringUtils.isEmpty(content)) {
                article.setContent(content);
            }

            if (!StringUtils.isEmpty(tag)) {
                article.setTag(tag);
            }

            saveFile(article, file);

            articleRepo.save(article);
        }

        return "redirect:/user-article/" + user;
    }



    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "О нас");
        return "about";
    }

    @GetMapping("/faqs")
    public String faqs(Model model) {
        model.addAttribute("title", "Вопросы-ответы");
        return "faqs";
    }

    private void saveFile(@Valid Article article, @RequestParam("file") MultipartFile file) throws IOException {
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resultFilename));

            article.setFilename(resultFilename);
        }
    }
}
