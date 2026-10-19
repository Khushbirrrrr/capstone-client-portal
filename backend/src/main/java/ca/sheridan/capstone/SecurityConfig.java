package ca.sheridan.capstone;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
@Configuration
public class SecurityConfig {
 @Bean PasswordEncoder encoder(){return new BCryptPasswordEncoder();}
 @Bean UserDetailsService users(PasswordEncoder encoder){return new InMemoryUserDetailsManager(
  User.withUsername("student").password(encoder.encode("student123")).roles("STUDENT").build(),
  User.withUsername("faculty").password(encoder.encode("faculty123")).roles("FACULTY").build());}
 @Bean SecurityFilterChain filter(HttpSecurity http) throws Exception {
  return http.csrf(csrf->csrf.disable())
   .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .authorizeHttpRequests(auth->auth
    .requestMatchers("/api/health").permitAll()
    .requestMatchers("/api/me").authenticated()
    .requestMatchers("/api/faculty/**").hasRole("FACULTY")
    .requestMatchers(HttpMethod.GET,"/api/projects","/api/projects/**").authenticated()
    .anyRequest().denyAll())
   .httpBasic(Customizer.withDefaults()).build();
 }
}
