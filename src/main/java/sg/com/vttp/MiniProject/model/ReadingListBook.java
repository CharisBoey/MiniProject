package sg.com.vttp.MiniProject.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReadingListBook {
    private String title;
    private String isbn;
    private String authors;
    private String description;
    private String thumbnail;
    private String category;
    private String language;
    private String rating;
    private String comments;
}
