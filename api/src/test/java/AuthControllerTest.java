import com.fasterxml.jackson.databind.ObjectMapper;
import com.kodat.complexhotelsystem.ComplexHotelSystemApplication;
import com.kodat.complexhotelsystem.dtos.RegisterDto;
import com.kodat.complexhotelsystem.interfaces.IEmail.IEmailSender;
import com.kodat.complexhotelsystem.services.RateLimitService;
import com.kodat.complexhotelsystem.services.jwt.RsaKeysGeneration;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(
        classes = ComplexHotelSystemApplication.class,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:auth-controller-tests;DB_CLOSE_DELAY=-1",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "app.jwt.private-key-path=/tmp/test-private.key",
                "app.jwt.public-key-path=/tmp/test-public.key",
                "spring.mail.username=test",
                "spring.mail.password=test"
        }
)
@AutoConfigureMockMvc
@Transactional
public class AuthControllerTest {
    @Autowired
    private MockMvc mock;

    private final ObjectMapper mapper = new ObjectMapper();

    @MockitoBean
    private IEmailSender emailSender;

    @MockitoBean
    private RateLimitService rateLimitService;

    @MockitoBean
    private RsaKeysGeneration rsaKeysGeneration;

    @BeforeEach
    void allowRequestsThroughTheRateLimitInterceptor() {
        when(rateLimitService.checkLimit(anyString(), anyInt(), anyLong()))
                .thenReturn(new RateLimitService.Decision(true, 0));
    }

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
                .andExpect(status().isAccepted());

        mock.perform(post("/api/auth/register")
                        .header("X-Forwarded-For", "192.168.1.17")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json2))
                .andExpect(status().isAccepted());
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
                .andExpect(status().isAccepted());

        mock.perform(post("/api/auth/register")
                        .with(csrf())
                        .header("X-Forwarded-For", "192.168.1.19")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json2))
                .andExpect(status().isAccepted());
    }



}


