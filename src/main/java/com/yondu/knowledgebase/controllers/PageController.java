//package com.yondu.knowledgebase.controllers;
//
//import java.util.List;
//
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.yondu.knowledgebase.entities.Page;
//import com.yondu.knowledgebase.services.PageService;
//
//@RestController
//@RequestMapping(path = "pages")
//public class PageController {
//
//    private final PageService pageService;
//
//    /**
//     * @param pageService
//     */
//    public PageController(PageService pageService) {
//        this.pageService = pageService;
//    }
//
//    @GetMapping(path = "{id}")
//    public Page getPage(@PathVariable Long id) {
//        return pageService.findById(id);
//    }
//
//    @GetMapping
//    public List<Page> getAllPages() {
//        return pageService.findAll();
//    }
//}
