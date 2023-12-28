/* package sg.com.vttp.MiniProject.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class Login {

    @Email(message="Must follow email format")
    @NotEmpty(message="Email must be filled in")
    private String email;

    // @Pattern(regexp="^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$", message="Minimum eight characters, at least one letter and one number")
    @Size(min=8, message="Password must be at least 8 characters")
    @NotEmpty(message="Email must be filled in")
    private String password;

    private Boolean setPassword;

    //Constructors
    public Login (){

    }
    public Login(String email, String password, Boolean setPassword) {
        this.email = email;
        this.password = password;
        this.setPassword = setPassword;
    }



    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getPassword() {return password;}
    public void setPassword(String password) {
        if (!getSetPassword()){
            this.password = password;
            setSetPassword(true);
        } else {
            throw new IllegalArgumentException("password only set once");
        }
    }

    public Boolean getSetPassword() {return setPassword;}
    public void setSetPassword(Boolean setPassword) {this.setPassword = setPassword;}





}
 */

package sg.com.vttp.MiniProject.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
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

    // @Pattern(regexp="^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$", message="Minimum eight characters, at least one letter and one number")
    @Size(min=8, message="Password must be at least 8 characters")
    @NotEmpty(message="Email must be filled in")
    private String password;

    //private String username;
}