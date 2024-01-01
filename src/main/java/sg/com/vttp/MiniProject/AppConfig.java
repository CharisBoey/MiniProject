package sg.com.vttp.MiniProject;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class AppConfig {

   private Logger logger = Logger.getLogger(AppConfig.class.getName());

   @Value("${spring.redis.host}")
   private String redisHost;

   @Value("${spring.redis.port}")
   private Integer redisPort;

   @Value("${spring.redis.username}")
   private String redisUser;

   @Value("${spring.redis.password}")
   private String redisPassword;

   @Value("${spring.redis.database}")
   private Integer redisDatabase;

   @Bean("stringRedis")
   public RedisTemplate<String, String> createStringRedisConnection() {

      RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
      config.setHostName(redisHost);
      config.setPort(redisPort);
      config.setDatabase(redisDatabase);

      if (!"NOT_SET".equals(redisUser.trim())) {
         config.setUsername(redisUser);
         config.setPassword(redisPassword);
      }

      logger.log(Level.INFO, "Using Redis database %d".formatted(redisPort));
      logger.log(Level.INFO
         , "Using Redis password is set: %b".formatted(redisPassword != "NOT_SET"));

      JedisClientConfiguration jedisClient = JedisClientConfiguration
            .builder().build();
      JedisConnectionFactory jedisFac = new JedisConnectionFactory(config, jedisClient);
      jedisFac.afterPropertiesSet();

      RedisTemplate<String, String> template = new RedisTemplate<>();
      template.setConnectionFactory(jedisFac);

      template.setKeySerializer(new StringRedisSerializer());
      template.setValueSerializer(new StringRedisSerializer());
      template.setHashKeySerializer(new StringRedisSerializer());
      template.setHashValueSerializer(new StringRedisSerializer());

      return template;
   }
   @Bean("objectRedis")
   //CHANGE TO STRING/OBJECT
   public RedisTemplate<String, Object> createObjectRedisConnection() {

      RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
      config.setHostName(redisHost);
      config.setPort(redisPort);
      config.setDatabase(redisDatabase);

      if (!"NOT_SET".equals(redisUser.trim())) {
         config.setUsername(redisUser);
         config.setPassword(redisPassword);
      }

      logger.log(Level.INFO, "Using Redis database %d".formatted(redisPort));
      logger.log(Level.INFO
         , "Using Redis password is set: %b".formatted(redisPassword != "NOT_SET"));

      JedisClientConfiguration jedisClient = JedisClientConfiguration
            .builder().build();
      JedisConnectionFactory jedisFac = new JedisConnectionFactory(config, jedisClient);
      jedisFac.afterPropertiesSet();

      //CHANGE TO STRING/OBJECT
      RedisTemplate<String, Object> template = new RedisTemplate<>();
      template.setConnectionFactory(jedisFac);
      
      //if using <String, Object>, setKeySerializer & set HashKeySerializer is still stringredisserializer
      template.setKeySerializer(new StringRedisSerializer());
      template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
      template.setHashKeySerializer(new StringRedisSerializer());
      template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
      
      return template;
   }
   
}
