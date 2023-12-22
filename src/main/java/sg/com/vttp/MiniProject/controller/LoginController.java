package sg.com.vttp.MiniProject.controller;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import sg.com.vttp.MiniProject.model.Book;
import sg.com.vttp.MiniProject.model.Login;
import sg.com.vttp.MiniProject.model.ReadingListBook;
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
        return "redirect:/Home/MyReadingList";
    }
    
    //PROBLEM!!! DOES NOT GET FROM REDIS KEEP "CREATING" EVEN THOUGH IT's IN REDIS ALRDY
    @GetMapping("/MyReadingList")
    public String userValidation(HttpSession sess, Model model){
        //Validate User (If have existing account, get data, if not create new account)
        Login login = (Login) sess.getAttribute("login");
        String email = login.getEmail();
        String password = login.getPassword();
        Boolean successfullyRetrieved = bookRepo.saveOrRetrieveUser(email, password);
        model.addAttribute("login", login);
        model.addAttribute("email", email);
        sess.setAttribute("email", email);

        // List<Book> bookList = bookSvc.getBookList();
        // model.addAttribute("bookList", bookList);
        // Book book = new Book();
        // model.addAttribute("book", book);
        List<ReadingListBook> readingList = bookRepo.getSavedReadingListBooks(email);
        model.addAttribute("readingList", readingList);
        if (successfullyRetrieved){
            return "readinglist";
        }
        return "creating";
    }

    @GetMapping("/Search")
    public String displayResult(Model model, @RequestParam MultiValueMap<String, String> params, HttpSession sess){

        String input = params.getFirst("input");

        //Check if input is not null & not empty, if so, use api to retrieve books, else, get from existing book list, else, return empty booklist
        List<Book> bookList = new LinkedList<>();
        if(input != null && !input.isEmpty()){
            bookList = bookSvc.getBookListTitle(input);
            model.addAttribute("bookList", bookList);
        } else if(sess.getAttribute("bookList") !=null) {
            bookList = (List<Book>) sess.getAttribute("bookList");
            model.addAttribute("bookList", bookList);
        } else {
            model.addAttribute("bookList", bookList);
        }

        String email = (String) sess.getAttribute("email");
        model.addAttribute("email", email);
        sess.setAttribute("bookList", bookList);

        // Book readingAdd = new Book();
        // model.addAttribute("readingAdd", readingAdd);
        /* List<ReadingListBook> readingList = bookRepo.getSavedReadingListBooks(email);
        model.addAttribute("readingList", readingList); */
        return "booksearchlist";
    }

    /* @PostMapping("/DisplayAndSearch")
    public String displayResult(Model model, @ModelAttribute ("readingAdd") Book readingAdd, HttpSession sess){

        List<Book> bookList = (List<Book>) sess.getAttribute("bookList");
        model.addAttribute("bookList", bookList);

        String email = (String) sess.getAttribute("email");
        model.addAttribute("email", email);

        
        List<Book> readingList = new LinkedList<>();
        ////System.out.println("*******************************" + readingAdd.getTitle());
        readingList.add(readingAdd);
        model.addAttribute("readingList", readingList);
        return "success";
    } */

    @PostMapping("/DisplayAndSearch")
    public String displayResult(Model model, HttpSession sess){

        List<Book> bookList = (List<Book>) sess.getAttribute("bookList");
        model.addAttribute("bookList", bookList);

        String email = (String) sess.getAttribute("email");
        model.addAttribute("email", email);

        
        // List<Book> readingList = sess.getAttribute("readingList")
        // ////System.out.println("*******************************" + readingAdd.getTitle());
        // readingList.add(readingAdd);
        // model.addAttribute("readingList", readingList);
        return "success";
    }
   
    @GetMapping("/Save/{isbn}")
    public String updateEmployee(@PathVariable("isbn") String isbn, Model model, HttpSession sess) {
        //PROBLEM: DEAL WITH BOOKS WITH NO ISBN

        //get book related to isbn + model.addattribute;
        //model.addAttribute("", );
        String input = "isbn:"+isbn;
        /* List<Book> readingList = new LinkedList<>();
        
        for(Book book: bookSvc.getBookListTitle(input)){
            readingList.add(book);
        }

        sess.setAttribute("readingList", readingList);
        model.addAttribute("readingList", readingList); */
        Book originalToSaveBook = bookSvc.getBookListTitle(input).get(0);
        ReadingListBook readingListBook = new ReadingListBook(
            originalToSaveBook.getTitle(),
            originalToSaveBook.getIsbn(), 
            originalToSaveBook.getAuthors(), 
            originalToSaveBook.getDescription(), 
            originalToSaveBook.getThumbnail(), 
            originalToSaveBook.getCategory(), 
            originalToSaveBook.getLanguage(), 
            "5/5", 
            "commentsss");
           
        String email = (String) sess.getAttribute("email");
        bookRepo.saveChosenBook(email, readingListBook);
        model.addAttribute("readingListBook", readingListBook);
        // return "redirect:/Home/DisplayAndSearch"; 
        return "redirect:/Home/DisplayAndSearch"; 
    }
}
