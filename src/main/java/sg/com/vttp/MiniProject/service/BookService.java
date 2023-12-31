package sg.com.vttp.MiniProject.service;

import java.io.StringReader;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import sg.com.vttp.MiniProject.model.Book;
import sg.com.vttp.MiniProject.model.ReadingListBook;

@Service
public class BookService {

    @Value("${books.apikey}")
    private String BOOKS_APIKEY;

    public String base_url="https://www.googleapis.com/books/v1/volumes?";

    public ResponseEntity<String> getRelevantBookData(String input){
        input = input.trim().replaceAll(" ","+");

        String url = UriComponentsBuilder
                    .fromUriString(base_url)
                    .queryParam("q", input)
                    .queryParam("printType", "books")
                    .queryParam("key", BOOKS_APIKEY)
                    .queryParam("maxResults", 40)
                    .build(false)
                    .toUriString();
                    
        //System.out.println("URL!!!!!!!!!!!"+url);
        RequestEntity<Void> request = RequestEntity.get(url).build();

        RestTemplate template = new RestTemplate();
        ResponseEntity<String> response = null;
        try{
            response = template.exchange(request, String.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return response;
    }

    public List<Book> getBookListTitle(String input){

        List<Book> bookList = new LinkedList<>();
        Book book = new Book();
        ResponseEntity<String> result = getRelevantBookData(input);
        String jsonString = result.getBody();
        
        //~Read data
        JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
        JsonObject jsonMainObject = jsonReader.readObject();
        JsonArray jsonArrayItemsInMain = jsonMainObject.getJsonArray("items");

        for(JsonValue item: jsonArrayItemsInMain){
            JsonObject jsonObjectIndivBook = (JsonObject) item;

            //~Go into the Object
            JsonObject jsonObjectVolumeInfo = jsonObjectIndivBook.getJsonObject("volumeInfo");

            //~Get Book Title
            String title = jsonObjectVolumeInfo.getString("title", "No Title");

            //~Get Book Authors List
            JsonArray authors = jsonObjectVolumeInfo.getJsonArray("authors");  
            String authorListOfNames="";
            if (authors != null) {
                for (JsonValue author:authors){
                    authorListOfNames += author.toString().replaceAll("\"", "");
                    if(author != authors.get(authors.size() - 1)){
                        authorListOfNames += ", ";
                    }
                }
            } else {
                authorListOfNames="Anonymous";
            }

            //~Get Book Description
            String description = jsonObjectVolumeInfo.getString("description", "No Description");

            //~Get Book ISBN
            JsonArray isbnVersions = jsonObjectVolumeInfo.getJsonArray("industryIdentifiers"); 
            String isbn10 = "";
            String isbn13 = "";
            if (isbnVersions != null) {
                for (JsonValue i :isbnVersions){
                    JsonObject isbn = i.asJsonObject();
                    String type = isbn.getString("type");
                    if (type.equals("ISBN_10")){
                        isbn10 = isbn.getString("identifier");
                    } else if (type.equals("ISBN_13")){
                        isbn13 = isbn.getString("identifier"); 
                    }
                }
            } else {
                isbn10 = null;
                isbn13 = null;
            }
            String isbn = "";
            if (isbn10 != null){
                isbn = isbn10;
            } else if (isbn13 !=null){
                isbn = isbn13;
            } else {
                isbn = null;
            }

            
            //~Get Book Image
            JsonObject imageLinkObject = jsonObjectVolumeInfo.getJsonObject("imageLinks");
            String thumbnail="";

            if (imageLinkObject != null) {
                thumbnail = imageLinkObject.getString("thumbnail");
            } else {
                thumbnail = "https://i.pinimg.com/originals/ed/d9/65/edd96521e8ca8975196b224a9c1cea6c.jpg";
            }

            //~Get Book Category List (Normal list is also used if you wanna display one way)
            JsonArray categories = jsonObjectVolumeInfo.getJsonArray("categories");
            List<String> categoryList = new LinkedList<>();
            String categoryListInString = "";

            if (categories != null) {
                for (JsonValue category:categories){
                    categoryListInString += category.toString().replaceAll("\"", "");
                    categoryList.add(category.toString());
                    if(category != categories.get(categories.size() - 1)){
                        categoryListInString += ", ";
                    }
                }
            } else {
                categoryListInString="No Category";
            }

            //~Get Book Language
            String language = jsonObjectVolumeInfo.getString("language", "No Language Specified");

            String infoLink = jsonObjectVolumeInfo.getString("infoLink","https://play.google.com/store/books");
            book = new Book(title, isbn, authorListOfNames, description, thumbnail, categoryListInString, language, infoLink);
            
            if(isbn !=null && !isbn.isEmpty()){
                bookList.add(book);
            }
        }
        return bookList;
    }

    public ReadingListBook readingListBook(String isbn){
        String input = "isbn:"+isbn;
        Book originalToSaveBook = getBookListTitle(input).get(0);
        ReadingListBook readingListBook = new ReadingListBook(
            originalToSaveBook.getTitle(),
            originalToSaveBook.getIsbn(), 
            originalToSaveBook.getAuthors(), 
            originalToSaveBook.getDescription(), 
            originalToSaveBook.getThumbnail(), 
            originalToSaveBook.getCategory(), 
            originalToSaveBook.getLanguage(),
            originalToSaveBook.getInfoLink(), 
            " ? "+"/5", 
            "...", false);

        return readingListBook;
    }

    public Book getSurpriseBook(){
        //~Get random index
        ResponseEntity<String> randomResult = getRelevantBookData("*");
        String randomJsonString = randomResult.getBody();
        JsonReader randomJsonReader = Json.createReader(new StringReader(randomJsonString));
        JsonObject randomJsonMainObject = randomJsonReader.readObject();
        Integer totalItems = randomJsonMainObject.getInt("totalItems");

        SecureRandom random = new SecureRandom();
        Integer randomIndex = random.nextInt(totalItems);

        //----------------------------------- get start index ---------------------------------
        ResponseEntity<String> result = getRelevantBookData("startIndex:"+randomIndex);
        String jsonString = result.getBody();
        JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
        JsonObject jsonMainObject = jsonReader.readObject();

        Boolean notfound = true;
        String isbn = "";

        while(notfound){
    
            //~Get isbn related to random index
            JsonArray jsonArrayItemsInMain = jsonMainObject.getJsonArray("items");
            JsonObject surpriseBook = (JsonObject) jsonArrayItemsInMain.get(0);
            JsonObject jsonObjectVolumeInfo = surpriseBook.getJsonObject("volumeInfo");
            JsonArray isbnVersions = jsonObjectVolumeInfo.getJsonArray("industryIdentifiers"); 
            String isbn10 = "";
            String isbn13 = "";
            if (isbnVersions != null) {
                for (JsonValue i :isbnVersions){
                    JsonObject isbnFound = i.asJsonObject();
                    String type = isbnFound.getString("type");
                    if (type.equals("ISBN_10")){
                        isbn10 = isbnFound.getString("identifier");
                    } else if (type.equals("ISBN_13")){
                        isbn13 = isbnFound.getString("identifier"); 
                    }
                }
            } else {
                isbn10 = null;
                isbn13 = null;
            }
            
            if (isbn10 != null){
                isbn = isbn10;
            } else if (isbn13 !=null){
                isbn = isbn13;
            } else {
                isbn = null;
            }
                
            if(!isbn.isEmpty()){
                notfound=false;
            }
        }
        
        String input = "isbn:"+isbn;
        Book surpriseBook = getBookListTitle(input).get(0);
        
        return surpriseBook;
    }
}