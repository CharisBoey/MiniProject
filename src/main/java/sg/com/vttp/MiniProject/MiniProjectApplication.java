package sg.com.vttp.MiniProject;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;

import sg.com.vttp.MiniProject.model.ReadingListBook;
import sg.com.vttp.MiniProject.repository.BookRepository;

@SpringBootApplication
public class MiniProjectApplication implements CommandLineRunner{

	@Autowired @Qualifier("objectRedis")
    private RedisTemplate<String, Object> readingListTemplate;
	@Autowired
	BookRepository bookRepo;

	public static void main(String[] args) {
		SpringApplication.run(MiniProjectApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println(";;;;;;;;;;;;;;;;;;;;;;;;"+ readingListTemplate.keys("*"));
		Set<String> keys = readingListTemplate.keys("*");
		for (String key: keys){
		List<ReadingListBook> readingList = bookRepo.getSavedReadingListBooks(key);
			for(ReadingListBook rlb: readingList){
				System.out.println(rlb.getAuthors());
				System.out.println(rlb.getTitle());
			}
		}
	}

}
