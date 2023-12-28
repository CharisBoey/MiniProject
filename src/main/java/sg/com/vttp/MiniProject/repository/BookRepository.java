package sg.com.vttp.MiniProject.repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import sg.com.vttp.MiniProject.model.ReadingListBook;

@Repository
public class BookRepository {

    @Autowired @Qualifier("stringRedis")
    private RedisTemplate<String, String> loginTemplate;

    @Autowired @Qualifier("objectRedis")
    private RedisTemplate<String, Object> readingListTemplate;

    //REDIS USER 
    public void saveUser(String email, String password){
        ValueOperations<String, String> user = loginTemplate.opsForValue();
        user.set(email, password);
    }

    public boolean hasUser(String email){
        return loginTemplate.hasKey(email);
    }

    public boolean RetrieveUser(String email, String password){
        //Only if HAVE USER, check if password correct 
        ValueOperations<String, String> user = loginTemplate.opsForValue();
        
        if(hasUser(email)){
            String savedPassword = user.get(email);
            if(savedPassword.equals(password)){
                return true;
            }
        }
        
        return false;
    }


    //REDIS READINGLIST FOR USER 
    public void saveChosenBook(String email, ReadingListBook ReadingListBook){
        HashOperations<String, String, Object> chosenBookList = readingListTemplate.opsForHash();
        chosenBookList.put(email+"book", ReadingListBook.getIsbn(), ReadingListBook);
    }

    // public void deleteReadingList(String email, ReadingListBook ReadingListBook){
    //     readingListTemplate.delete(email);
    // }

    public boolean hasReadingList(String email){
        return readingListTemplate.hasKey(email+"book");
    }

    public void deleteChosenBook(String email, ReadingListBook ReadingListBook){
        readingListTemplate.opsForHash().delete(email+"book", ReadingListBook.getIsbn());
    }

    public List<ReadingListBook> getSavedReadingListBooks(String email){
        HashOperations<String,String,Object> readingList = readingListTemplate.opsForHash();
        List<ReadingListBook> readingListBooks = new LinkedList<>();
        if(hasReadingList(email)){
            //System.out.println("TRUUUUUUUUUUUUUUUUUUUUUUUUUUEEEEEEEEEEEEEEEEEEe");
            Map<String, Object> hashReadingListEntries = readingList.entries(email+"book");
            for (String i : hashReadingListEntries.keySet()){
                ReadingListBook book = (ReadingListBook) hashReadingListEntries.get(i);
                //System.out.println("*******************"+book.getTitle());
                readingListBooks.add(book);
            }
        }
        return readingListBooks;
    }
}