package sg.com.vttp.MiniProject.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
public class BookRepository {

    @Autowired @Qualifier("redis")
   private RedisTemplate<String, String> template;

    
    public void saveUser(String email, String password){
        ValueOperations<String, String> user = template.opsForValue();
        user.set(email, password);
    }

    public boolean hasUser(String email){
        return template.hasKey(email);
    }

    public boolean saveOrRetrieveUser(String email, String password){
        ValueOperations<String, String> user = template.opsForValue();
        
        if(hasUser(email)){
            String savedPassword = user.get(email);
            if(savedPassword.equals(password)){
                return true;
            }
        }

        saveUser(email, password);
        return false;
    }
}
