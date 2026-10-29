import com.kodat.complexhotelsystem.entities.Person;
import com.kodat.complexhotelsystem.enums.PersonCategory;
import com.kodat.complexhotelsystem.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class PersonTest {

    @Test
    void testCreatingPersonWithDefaultValues(){
        Person personForTEst = new Person();
        assertNull(personForTEst.getId());
        assertNull(personForTEst.getEmail());
        assertNull(personForTEst.getUsername());
        assertNull(personForTEst.getLastname());
        assertNull(personForTEst.getPhone());

        assertEquals((Role.USER), personForTEst.getRole());
        assertEquals(PersonCategory.ADULT, personForTEst.getCategory());
    }
    @Test
    void testDefaultSpringAuthority(){
        Person personForTest = new Person();
        assertEquals(List.of(new SimpleGrantedAuthority("ROLE_USER")), personForTest.getAuthorities());
    }
    @Test
    void testGetPasswordMethodFromUserDetail(){
        Person personForTest = new Person();
        String password = "PasswordForTest";
        personForTest.setSecret("PasswordForTest");
        assertEquals(personForTest.getPassword(), password);

    }
}
