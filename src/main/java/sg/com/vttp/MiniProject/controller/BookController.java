package sg.com.vttp.MiniProject.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
import sg.com.vttp.MiniProject.model.RatingAndComments;
import sg.com.vttp.MiniProject.repository.BookRepository;
import sg.com.vttp.MiniProject.service.BookService;

@Controller
@RequestMapping("/Home")
public class BookController {

    @Autowired
    BookRepository bookRepo;

    @Autowired
    BookService bookSvc;

    //#Display Login Page
    @GetMapping("/Login")
    public String login(Model model){
        Login login = new Login();
        model.addAttribute("login", login);
        return "login";
    }    

    //#Validate Login Page
    @PostMapping("/Login")
    public String loginProcessing(@Valid @ModelAttribute Login login, BindingResult result, Model model, HttpSession sess){

        String email = login.getEmail().toLowerCase();
        String password = login.getPassword();
        
        //~Typical validation
        if(result.hasErrors()){
            return "login";
        }

        //~If new user, auto create account
        if(!bookRepo.hasUser(email)){
            model.addAttribute("email", email);
            sess.setAttribute("email", email);
            bookRepo.saveUser(email,  password);
            return "creating";
        }

        //~If old user, check password
        Boolean successfullyRetrieved = bookRepo.RetrieveUser(email, password);
        
        //~Validation that password is correct
        if(!successfullyRetrieved){
            FieldError err = new FieldError("login", "password", "Wrong Password, please try again");
            result.addError(err);
            return "login";
        }

        sess.setAttribute("email", email);
        return "redirect:/Home/MyReadingList";
    }
    
    //#Display existing reading list from redis
    @GetMapping("/MyReadingList")
    public String userValidation(HttpSession sess, Model model){
        String email = (String) sess.getAttribute("email");
        model.addAttribute("email", email);
        
        //~Retrieve from redis
        List<ReadingListBook> readingList = bookRepo.getSavedReadingListBooks(email);
        model.addAttribute("readingList", readingList);
        return "readinglist";
    }

    //#Display results during book search
    @GetMapping("/Search")
    public String displayResult(Model model, @RequestParam MultiValueMap<String, String> params, HttpSession sess){

        String input = params.getFirst("input");

        //~Check if input is not null & not empty, if so, use api to retrieve books via ISBN, else, get from existing book list, else, return empty booklist
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
        return "booksearchlist";
    }
   
    //#Save book from Book Search and stay on same page 
    @GetMapping("/Save/Search/{isbn}")
    public String saveBookFromBookSearch(@PathVariable("isbn") String isbn, Model model, HttpSession sess) {

        ReadingListBook readingListBook = bookSvc.readingListBook(isbn);
           
        String email = (String) sess.getAttribute("email");
        bookRepo.saveChosenBook(email, readingListBook);
        
        return "redirect:/Home/Search"; 
    }

    //#Display Surprise Book
    @GetMapping("/SurpriseBook")
    public String surpriseBook(Model model, HttpSession sess){
        String email = (String) sess.getAttribute("email");
        model.addAttribute("email", email);
        
        Book surpriseBook = bookSvc.getSurpriseBook();
        model.addAttribute("surpriseBook", surpriseBook);
        return "surprisebook";
    }

    //#Save book from Surprise Book and stay on same page 
    @GetMapping("/Save/SurpriseBook/{isbn}")
    public String saveBookFromSurpriseBook(@PathVariable("isbn") String isbn, Model model, HttpSession sess) {

        ReadingListBook readingListBook = bookSvc.readingListBook(isbn);
           
        String email = (String) sess.getAttribute("email");
        bookRepo.saveChosenBook(email, readingListBook);
        
        return "redirect:/Home/SurpriseBook"; 
    }

    //#Get existing book data and display update page
    @GetMapping("/Update/{isbn}")
    public String updateBook(@PathVariable("isbn") String isbn, Model model, HttpSession sess){
        
        String email = (String) sess.getAttribute("email");
        ReadingListBook rlb = bookRepo.getIndivSavReadingListBook(email, isbn);
        RatingAndComments ratingAndComments = new RatingAndComments();
        String retrievedRatingString = rlb.getRating();
        String[] retrievedRating = retrievedRatingString.trim().split(" ");
        Double rating;
        if (retrievedRating[0].equals("?")){
            rating = 0.00;
        } else {
            rating = Double.parseDouble(retrievedRating[0]);
        }

        ratingAndComments.setRating(rating);
        ratingAndComments.setComments(rlb.getComments());
        model.addAttribute("ratingAndComments", ratingAndComments);
        
        model.addAttribute("email", email);
        model.addAttribute("isbn", isbn);
        return "update";
    }

    //#Validate Update response & save new data to redis
    @PostMapping("/Update/{isbn}")
    public String updateBookValidation(@PathVariable("isbn") String isbn, @Valid @ModelAttribute RatingAndComments ratingAndComments, BindingResult result, Model model, HttpSession sess){
        model.addAttribute("isbn", isbn);
        ReadingListBook readingListBook = bookSvc.readingListBook(isbn);

         if(result.hasErrors()){
            return "update";
        }
        
        readingListBook.setRating(ratingAndComments.getRating().toString()+" /5");
        readingListBook.setComments(ratingAndComments.getComments());
        if (ratingAndComments.getCompleted().equals("true")){
            readingListBook.setCompleted(true);
        } else{
            readingListBook.setCompleted(false);
        }

        String email = (String) sess.getAttribute("email");
        
        bookRepo.saveChosenBook(email, readingListBook);

        return "redirect:/Home/MyReadingList";
    }

    //#Delete Books
    @GetMapping("/Delete/{isbn}")
    public String deleteBook(@PathVariable("isbn") String isbn, HttpSession sess){
        String email = (String) sess.getAttribute("email");
        ReadingListBook readingListBook = bookSvc.readingListBook(isbn);
        bookRepo.deleteChosenBook(email, readingListBook);

        return "redirect:/Home/MyReadingList";
    }

    //#Logout
    @GetMapping("/Logout")
    public String logout(HttpSession sess){
        sess.invalidate();
        return "redirect:/Home/Login";
    }

    //#Display image credits
    @GetMapping("/Credits")
    public String credits(){

        return "credits";
    }

    //Display RestAPI instructions
    @GetMapping("/RestAPI")
    public String restAPI(){

        return "restapi";
    }
}