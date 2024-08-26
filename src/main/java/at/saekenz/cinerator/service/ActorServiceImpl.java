package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.actor.Actor;
import at.saekenz.cinerator.repository.ActorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ActorServiceImpl implements IActorService{

    @Autowired
    public ActorRepository actorRepository;

    @Override
    public List<Actor> findAll() {
        return actorRepository.findAll();
    }

    @Override
    public Optional<Actor> findById(Long id) {
        return actorRepository.findById(id);
    }

    @Override
    public List<Actor> findByName(String name) {
        return actorRepository.findByName(name);
    }

    @Override
    public List<Actor> findByBirthDate(LocalDate birthDate) {
        return actorRepository.findByBirthDate(birthDate);
    }

    @Override
    public List<Actor> findByBirthCountry(String country) {
        return actorRepository.findByBirthCountry(country);
    }

    @Override
    public List<Actor> findByAge(int age) {
        return actorRepository.findByAge(age);
    }

    @Override
    public Actor save(Actor actor) {
        return actorRepository.save(actor);
    }

    @Override
    public void deleteById(Long id) {
        actorRepository.deleteById(id);
    }
}
