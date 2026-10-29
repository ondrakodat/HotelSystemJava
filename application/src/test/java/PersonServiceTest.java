import com.kodat.complexhotelsystem.dtos.PersonDto;
import com.kodat.complexhotelsystem.entities.Person;
import com.kodat.complexhotelsystem.interfaces.Irepository.IPersonRepository;
import com.kodat.complexhotelsystem.interfaces.Iservices.IPersonService;
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

import java.util.Optional;

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
}
