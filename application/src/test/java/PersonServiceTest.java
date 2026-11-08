import com.kodat.complexhotelsystem.dtos.PersonDto;
import com.kodat.complexhotelsystem.dtos.RegisterDto;
import com.kodat.complexhotelsystem.entities.Person;
import com.kodat.complexhotelsystem.exceptions.EmailAlreadyExistsException;
import com.kodat.complexhotelsystem.interfaces.Irepository.IPersonRepository;
import com.kodat.complexhotelsystem.interfaces.Iservices.Imapper.IPersonMapper;
import com.kodat.complexhotelsystem.services.PersonService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;


@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {
    @Mock
    private IPersonRepository repository;
    @Mock
    private IPersonMapper personMapper;

    @InjectMocks
    private PersonService service;

    @Test
    void testReturnPersonByEmail(){
        String emailExample = "test@email.com";
        Person person = new Person();

        Mockito.when(repository.findPersonByEmail(emailExample)).thenReturn(person);

        UserDetails result = service.loadUserByUsername(emailExample);

        Assertions.assertSame(person, result);
        Mockito.verify(repository).findPersonByEmail(emailExample);
    }
    @Test
    void testReturnPersonById(){
        long idForTest = 1;
        Person person = new Person();
        PersonDto dto = new PersonDto();
        person.setUsername("testPerson");
        dto.setUsername("testPerson");

        Mockito.when(repository.findPersonById(idForTest)).thenReturn(person);
        Mockito.when(personMapper.toDto(person)).thenReturn(dto);

        PersonDto result = service.findPersonDtoById(idForTest);

        Assertions.assertEquals(person.getUsername(), result.getUsername());
        Mockito.verify(repository).findPersonById(idForTest);
    }

    @Test
    void createPersonTest(){
        Person personForCreating = new Person();
        personForCreating.setUsername("Ondra");
        personForCreating.setLastname("Kodat");
        personForCreating.setEmail("test@email.com");

        Mockito.when(repository.createPerson(personForCreating)).thenReturn(personForCreating);

        Person result = repository.createPerson(personForCreating);

        Assertions.assertEquals(personForCreating, result);
        Assertions.assertEquals("Ondra", result.getUsername());

        Mockito.verify(repository).createPerson(personForCreating);
    }

    @Test
    void createPersonTestException(){

        Person personForCreating = new Person();
        RegisterDto dto = new RegisterDto();

        Mockito.when(repository.createPerson(personForCreating)).thenThrow(EmailAlreadyExistsException.class);

        Assertions.assertThrows(EmailAlreadyExistsException.class , () -> repository.createPerson(personForCreating));

        Mockito.verify(repository).createPerson(personForCreating);
    }
}
