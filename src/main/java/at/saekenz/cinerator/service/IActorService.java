package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.actor.Actor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IActorService {

    List<Actor> findAll();

    Optional<Actor> findById(Long id);

    List<Actor> findByName(String name);

    List<Actor> findByBirthDate(LocalDate birthDate);

    List<Actor> findByBirthCountry(String country);

    List<Actor> findByAge(int age);

    Actor save(Actor actor);

    void deleteById(Long id);
}
