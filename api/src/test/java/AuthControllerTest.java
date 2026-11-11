import com.fasterxml.jackson.databind.ObjectMapper;
import com.kodat.complexhotelsystem.ComplexHotelSystemApplication;
import com.kodat.complexhotelsystem.dtos.RegisterDto;
import jakarta.transaction.Transactional;
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
@Transactional
public class AuthControllerTest {
    @Autowired
    private MockMvc mock;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void createPersonWithountValidEmailEndPointTest(){
        RegisterDto dto = new RegisterDto();
        dto.setEmail("NotValidEmail");
        dto.setFirstname("Ondra");
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
        dto.setFirstname("Ondra");
        dto.setLastname("Kodat");
        dto.setPhone("759542364");
        dto.setHashedSecret("notvalidpassword");
        try {
            String json = mapper.writeValueAsString(dto);

            mock.perform(post("/api/auth/register")
                            .with(csrf())
                    .header("X-Forwarded-For", "192.168.1.10")
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
        dto.setFirstname("O");
        dto.setLastname("Kodat");
        dto.setPhone("759542364");
        dto.setHashedSecret("SecretPassword1@");
        try {
            String json = mapper.writeValueAsString(dto);

            mock.perform(post("/api/auth/register")
                    .with(csrf())
                    .header("X-Forwarded-For", "192.168.1.11")
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
        dto.setFirstname("Ondra");
        dto.setLastname("K");
        dto.setPhone("759542364");
        dto.setHashedSecret("SecretPassword1@");
        try {
            String json = mapper.writeValueAsString(dto);

            mock.perform(post("/api/auth/register")
                    .with(csrf())
                    .header("X-Forwarded-For", "192.168.1.12")
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
        dto.setFirstname("Ondra");
        dto.setLastname("Kodat");
        dto.setPhone("number");
        dto.setHashedSecret("SecretPassword1@");
        try {
            String json = mapper.writeValueAsString(dto);

            mock.perform(post("/api/auth/register")
                    .with(csrf())
                    .header("X-Forwarded-For", "192.168.1.14")
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
        dto.setFirstname("Ondra");
        dto.setLastname("Kodat");
        dto.setPhone("number");
        dto.setHashedSecret("SecretPassword1@");
        try {
            String json = mapper.writeValueAsString(dto);

            mock.perform(post("/api/auth/register")
                    .header("X-Forwarded-For", "192.168.1.15")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
            ).andExpect(status().isForbidden());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void registerUserAndCheckDuplicateEmail() throws Exception {

        RegisterDto dto = new RegisterDto();
        dto.setEmail("valid@email.cz");
        dto.setFirstname("Ondra");
        dto.setLastname("Kodat");
        dto.setPhone("759542364");
        dto.setHashedSecret("StrongPassword123!");

        RegisterDto dto2 = new RegisterDto();
        dto2.setEmail("valid@email.cz");
        dto2.setFirstname("Ondra");
        dto2.setLastname("Kodat");
        dto2.setPhone("111111222");
        dto2.setHashedSecret("StrongPassword123!");

        String json = mapper.writeValueAsString(dto);
        String json2 = mapper.writeValueAsString(dto2);


        mock.perform(post("/api/auth/register")
                        .with(csrf())
                        .header("X-Forwarded-For", "192.168.1.16")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mock.perform(post("/api/auth/register")
                        .header("X-Forwarded-For", "192.168.1.17")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json2))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUserAndCheckDuplicatePhone() throws Exception {
        RegisterDto dto = new RegisterDto();
        dto.setEmail("valid@email.cz");
        dto.setFirstname("Ondra");
        dto.setLastname("Kodat");
        dto.setPhone("759542364");
        dto.setHashedSecret("StrongPassword123!");

        RegisterDto dto2 = new RegisterDto();
        dto2.setEmail("valid2@email.cz");
        dto2.setFirstname("Ondra");
        dto2.setLastname("Kodat");
        dto2.setPhone("759542364");
        dto2.setHashedSecret("StrongPassword123!");

        String json = mapper.writeValueAsString(dto);
        String json2 = mapper.writeValueAsString(dto2);

        mock.perform(post("/api/auth/register")
                        .with(csrf())
                        .header("X-Forwarded-For", "192.168.1.18")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mock.perform(post("/api/auth/register")
                        .with(csrf())
                        .header("X-Forwarded-For", "192.168.1.19")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json2))
                .andExpect(status().isBadRequest());
    }



}


