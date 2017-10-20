package it.eric.micro.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.eric.mico.service.model.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {

}
