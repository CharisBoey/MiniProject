package sg.com.vttp.MiniProject;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;

import sg.com.vttp.MiniProject.model.Book;
import sg.com.vttp.MiniProject.service.BookService;

@SpringBootApplication
public class MiniProjectApplication implements CommandLineRunner{

	@Autowired
	private BookService bookSvc;
	public static void main(String[] args) {
		SpringApplication.run(MiniProjectApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		/* List<Book> bookList = bookSvc.getBookList();

		for(Book book : bookList){
			System.out.println("!!!!!!!!!!"+ book.getTitle()+"___"+book.getId());
		} */

		//System.out.println(base_url);

		// ResponseEntity<String> result = bookSvc.getAllBookData();
		// String jsonString = result.getBody();
		// System.out.println(jsonString);
	}

}
