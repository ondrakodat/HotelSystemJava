import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kodat.complexhotelsystem.ComplexHotelSystemApplication;
import com.kodat.complexhotelsystem.dtos.RegisterDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ComplexHotelSystemApplication.class)
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    private MockMvc mock;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void createPersonWithountValidEmailEndPointTest(){
        RegisterDto dto = new RegisterDto();
        dto.setEmail("NotValidEmail");
        dto.setUsername("Ondra");
        dto.setLastname("Kodat");
        dto.setPhone("759542364");
        dto.setHashedSecret("SecretPassword1@");

        try {
            String json = mapper.writeValueAsString(dto);

            mock.perform(post("/api/auth/register")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
            ).andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createPersonWithountValidPassword(){
        RegisterDto dto = new RegisterDto();
        dto.setEmail("valid@email.cz");
        dto.setUsername("Ondra");
        dto.setLastname("Kodat");
        dto.setPhone("759542364");
        dto.setHashedSecret("notvalidpassword");
        try {
            String json = mapper.writeValueAsString(dto);

            mock.perform(post("/api/auth/register")
                            .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
            ).andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createPersonWithountValidUsername(){
        RegisterDto dto = new RegisterDto();
        dto.setEmail("valid@email.cz");
        dto.setUsername("O");
        dto.setLastname("Kodat");
        dto.setPhone("759542364");
        dto.setHashedSecret("SecretPassword1@");
        try {
            String json = mapper.writeValueAsString(dto);

            mock.perform(post("/api/auth/register")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
            ).andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createPersonWithountValidLastName(){
        RegisterDto dto = new RegisterDto();
        dto.setEmail("valid@email.cz");
        dto.setUsername("Ondra");
        dto.setLastname("K");
        dto.setPhone("759542364");
        dto.setHashedSecret("SecretPassword1@");
        try {
            String json = mapper.writeValueAsString(dto);

            mock.perform(post("/api/auth/register")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
            ).andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createPersonWithountValidPhoneNumber(){
        RegisterDto dto = new RegisterDto();
        dto.setEmail("valid@email.cz");
        dto.setUsername("Ondra");
        dto.setLastname("Kodat");
        dto.setPhone("number");
        dto.setHashedSecret("SecretPassword1@");
        try {
            String json = mapper.writeValueAsString(dto);

            mock.perform(post("/api/auth/register")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
            ).andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createPersonWithountValidCsrf(){
        RegisterDto dto = new RegisterDto();
        dto.setEmail("valid@email.cz");
        dto.setUsername("Ondra");
        dto.setLastname("Kodat");
        dto.setPhone("number");
        dto.setHashedSecret("SecretPassword1@");
        try {
            String json = mapper.writeValueAsString(dto);

            mock.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
            ).andExpect(status().isForbidden());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}


