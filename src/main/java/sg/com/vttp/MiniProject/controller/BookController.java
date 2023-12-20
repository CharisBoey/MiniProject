package sg.com.vttp.MiniProject.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import sg.com.vttp.MiniProject.model.Book;
import sg.com.vttp.MiniProject.service.BookService;

@Controller
@RequestMapping("/Home")
public class BookController {

    @Autowired
    BookService bookSvc;
    

    // @GetMapping("/Info")
    // public String displayBookInfo(Model model){
    //     List<Book> bookList = bookSvc.getBookList();

    //     model.addAttribute("bookList", bookList);
    //     return "bookinfo";
    // }
}
