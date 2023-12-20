package sg.com.vttp.MiniProject.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    private String title;
    private String id;
    private String authors;
    private String description;
    private String thumbnail;
    private String category;
    private String language;
}
