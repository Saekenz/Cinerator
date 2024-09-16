package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.person.Person;
import at.saekenz.cinerator.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonServiceImpl implements IPersonService {

    @Autowired
    private PersonRepository personRepository;

    @Override
    public List<Person> findAll() {
        return personRepository.findAll();
    }

    @Override
    public Optional<Person> findById(Long id) {
        return personRepository.findById(id);
    }

    @Override
    public Person getReferenceById(Long id) {
        return personRepository.getReferenceById(id);
    }

    @Override
    public Person save(Person person) {
        return personRepository.save(person);
    }

    @Override
    public void deleteById(Long id) {
        personRepository.deleteById(id);
    }
}
