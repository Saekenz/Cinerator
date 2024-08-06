package at.saekenz.cinerator.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Review {

    private @Id @GeneratedValue long id;
}
