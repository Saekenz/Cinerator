package at.saekenz.cinerator.model.person;

import at.saekenz.cinerator.util.EntityMapper;
import org.springframework.stereotype.Component;

@Component
public class PersonMapper implements EntityMapper<Person, PersonDTO> {

    @Override
    public PersonDTO toDTO(Person person) {
        return new PersonDTO(person.getId(),
                person.getName(),
                person.getBirthDate(),
                person.getDeathDate(),
                person.getBirthCountry().getName());
    }

    public Person toPerson(PersonDTO personDTO) {
        return new Person(personDTO.getName(),
                personDTO.getBirthDate(),
                personDTO.getDeathDate()
                );
    }
}
