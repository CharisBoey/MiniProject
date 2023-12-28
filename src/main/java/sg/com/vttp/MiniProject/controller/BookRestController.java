package sg.com.vttp.MiniProject.controller;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.servlet.http.HttpSession;
import sg.com.vttp.MiniProject.model.Book;
import sg.com.vttp.MiniProject.model.ReadingListBook;
import sg.com.vttp.MiniProject.repository.BookRepository;
import sg.com.vttp.MiniProject.service.BookService;

@RestController
@RequestMapping(path="/Home", produces ="application/json")
public class BookRestController {

    @Autowired
    BookRepository bookRepo;

    @GetMapping("/MyReadingListJson/{email}")
    public ResponseEntity<String> getUserReadingList(@PathVariable(name = "email") String email){
        
        List<ReadingListBook> readingList = bookRepo.getSavedReadingListBooks(email);

        JsonObjectBuilder userBuilder = Json.createObjectBuilder();
        userBuilder.add("email", email);

        JsonObjectBuilder indivBookBuilder = Json.createObjectBuilder();
        JsonArrayBuilder readingListBuilder = Json.createArrayBuilder();

        for (ReadingListBook rlb: readingList){
            indivBookBuilder.add("title", rlb.getTitle());
            indivBookBuilder.add("isbn", rlb.getIsbn());
            indivBookBuilder.add("authors", rlb.getAuthors());
            indivBookBuilder.add("description", rlb.getDescription());
            indivBookBuilder.add("thumbnail", rlb.getThumbnail());
            indivBookBuilder.add("category", rlb.getCategory());
            indivBookBuilder.add("language", rlb.getLanguage());
            indivBookBuilder.add("rating", rlb.getRating());
            indivBookBuilder.add("comments", rlb.getComments());
            indivBookBuilder.add("completed", rlb.getCompleted());
            
            readingListBuilder.add(indivBookBuilder);
        } 
        

        JsonObjectBuilder userDataBuilder = Json.createObjectBuilder();
        userDataBuilder.add("myReadingList", readingListBuilder);

        userBuilder.add("data", userDataBuilder);

        JsonObject resp = userBuilder.build();
        
        return ResponseEntity.ok(resp.toString());

    }
    
}