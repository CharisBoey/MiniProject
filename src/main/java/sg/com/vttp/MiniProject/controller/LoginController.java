package sg.com.vttp.MiniProject.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import sg.com.vttp.MiniProject.model.Book;
import sg.com.vttp.MiniProject.model.Login;
import sg.com.vttp.MiniProject.repository.BookRepository;
import sg.com.vttp.MiniProject.service.BookService;

@Controller
@RequestMapping("/Home")
public class LoginController {

    @Autowired
    BookRepository bookRepo;

    @Autowired
    BookService bookSvc;

    @GetMapping("/Login")
    public String login(Model model){
        Login login = new Login();
        model.addAttribute("login", login);
        return "login";
    }    

    @PostMapping("/Login")
    public String loginProcessing(@Valid @ModelAttribute Login login, BindingResult result, Model model, HttpSession sess){

        if(result.hasErrors()){
            return "login";
        }
        sess.setAttribute("login", login);
        return "redirect:/Home/Validation";
    }
    
    //PROBLEM!!! DOES NOT GET FROM REDIS KEEP "CREATING" EVEN THOUGH IT's IN REDIS ALRDY
    @GetMapping("/Validation")
    public String userValidation(HttpSession sess, Model model){
        Login login = (Login) sess.getAttribute("login");
        String email = login.getEmail();
        String password = login.getPassword();
        Boolean successfullyRetrieved = bookRepo.saveOrRetrieveUser(email, password);
        model.addAttribute("login", login);
        sess.setAttribute("email", email);

        // List<Book> bookList = bookSvc.getBookList();
        // model.addAttribute("bookList", bookList);
        Book book = new Book();
        model.addAttribute("book", book);
        if (successfullyRetrieved){
            return "success";
        }
        return "creating";
    }

    @GetMapping("/Search")
    public String displayResult(Model model, @RequestParam MultiValueMap<String, String> params, HttpSession sess){
        String input = params.getFirst("input");
        List<Book> bookList = bookSvc.getBookListTitle(input);
        model.addAttribute("bookList", bookList);

        String email = (String) sess.getAttribute("email");
        model.addAttribute("email", email);

        return "success";
    }




    
}
