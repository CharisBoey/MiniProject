package sg.com.vttp.MiniProject.controller;

import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import sg.com.vttp.MiniProject.model.ReadingListBook;
import sg.com.vttp.MiniProject.repository.BookRepository;

@RestController
@RequestMapping(path="/Home")
public class BookRestController {

    @Autowired
    BookRepository bookRepo;

    //#RestAPI for individual data
    @GetMapping(path="/InkItAPI/{email}", produces ="application/json")
    public ResponseEntity<String> getUserReadingList(@PathVariable(name = "email") String email){
        
        List<ReadingListBook> readingList = bookRepo.getSavedReadingListBooks(email);

        JsonObjectBuilder userBuilder = Json.createObjectBuilder();
        userBuilder.add("email", email);
        userBuilder.add("items", readingList.size());

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
        

        userBuilder.add("myReadingList", readingListBuilder);

        JsonObject resp = userBuilder.build();
        
        return ResponseEntity.ok(resp.toString());

    }

    //#RestAPI for all data
    @GetMapping(path = "/InkItAPI", produces = "application/json")
    public ResponseEntity<String> getAll(@RequestParam Map<String, String> params) {
        String queryString = params.getOrDefault("q", "*");

        Set<String> allEmailKeys = bookRepo.getAllEmailKeys(queryString);
        
        JsonArrayBuilder allArrayBuilder = Json.createArrayBuilder();

        for (String key: allEmailKeys){


            List<ReadingListBook> readingList = bookRepo.getSavedReadingListBooks(key);

            JsonObjectBuilder userBuilder = Json.createObjectBuilder();
            userBuilder.add("email", key);
            userBuilder.add("items", readingList.size());

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
            

            userBuilder.add("myReadingList", readingListBuilder);
            
            JsonObject user = userBuilder.build();
            allArrayBuilder.add(user);
        }

        JsonArray allUsersResp = allArrayBuilder.build();
    
        return ResponseEntity.ok(allUsersResp.toString());

    }

    //#RestAPI new total no. of items
    @PostMapping(consumes = "application/json")
    public ResponseEntity<String> postAsJson(@RequestBody String payload) {
        JsonReader jsonReader = Json.createReader(new StringReader(payload));
        JsonObject mainData = jsonReader.readObject();
        System.out.printf(">>> data = %s\n", mainData.toString());

        String email = mainData.getString("email");

        JsonArray myReadingListArray = mainData.getJsonArray("myReadingList");

        JsonObject resp = Json.createObjectBuilder()
            .add("status", "modified")
            .add("items", myReadingListArray.size())
            .build();

        return ResponseEntity.status(201)
            .header("my header", email)
            .body(resp.toString());
    }
    
}