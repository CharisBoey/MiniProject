package sg.com.vttp.MiniProject.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RatingAndComments {
    
    @NotNull(message="Must fill in rating")
    @Min(value=0, message="Rating must be between 1-5")
    @Max(value=5, message="Rating must be between 1-5")
    private Double rating;
    private String comments;
    @NotNull(message="Must indicate completion status")
    private String completed;
}
