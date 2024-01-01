package sg.com.vttp.MiniProject.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Login {

    @Email(message="Must follow email format")
    @NotEmpty(message="Email must be filled in")
    private String email;

    @Pattern(regexp="^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$", message="Minimum eight characters, at least one uppercase letter, one lowercase letter and one digit")
    @Size(min=8, message="Password must be at least 8 characters")
    @NotEmpty(message="Email must be filled in")
    private String password;
}