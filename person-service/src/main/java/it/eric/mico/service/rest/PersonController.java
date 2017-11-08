package it.eric.mico.service.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import it.eric.mico.service.model.Person;
import it.eric.micro.service.exceptions.CustomErrorType;
import it.eric.micro.service.repository.PersonRepository;

@RestController
@RequestMapping("/myApi")
public class PersonController {
	public static final Logger logger = LoggerFactory.getLogger(PersonController.class);
	
	@Autowired
	private PersonRepository personRepository;
	
	@RequestMapping(value = "/persons", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	 public ResponseEntity<List<Person>> allPersons() {
		List<Person> persons =personRepository.findAll();
		if(persons.isEmpty()) {
			 return new ResponseEntity<List<Person>>(HttpStatus.NO_CONTENT);
		}
		
		return new ResponseEntity<List<Person>>(persons, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/person/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	 public ResponseEntity<?> getPerson(@PathVariable("id") int id) {
		Person person = personRepository.getOne(id);
		if(person == null) {
			logger.error("Person with id {} not found.", id);
            return new ResponseEntity<Object>(new CustomErrorType("Person with   id " + id 
                    + " not found"), HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<Person>(person, HttpStatus.OK);
	}
	
	
	 @RequestMapping(value = "/person", method = RequestMethod.POST,  produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	    public ResponseEntity<?> createUser(@RequestBody Person person, UriComponentsBuilder ucBuilder) {
	        logger.info("Creating person : {}", person);

	        Person p = personRepository.saveAndFlush(person);
	 
	        HttpHeaders headers = new HttpHeaders();
	        headers.setLocation(ucBuilder.path("/api/user/{id}").buildAndExpand(p.getId()).toUri());
	        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	    }
	 
	 @RequestMapping(value = "/person/{id}", method = RequestMethod.PUT, produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE } )
	    public ResponseEntity<?> updateUser(@PathVariable("id") int id, @RequestBody Person person) {
	        logger.info("Updating Person  with id {}", id);
	 
	        Person currentPerson = personRepository.getOne(id);
	 
	        if (!personRepository.exists(id)) {
	            logger.error("Unable to update. person with id {} not found.", id);
	            return new ResponseEntity(new CustomErrorType("Unable to upate. User with id " + id + " not found."),
	                    HttpStatus.NOT_FOUND);
	        }
	 
	        currentPerson.setFirstName(person.getFirstName());
	        currentPerson.setLastName(person.getLastName());
	        currentPerson.setCity(person.getCity());
	        personRepository.save(currentPerson);
	        return new ResponseEntity<Person>(currentPerson, HttpStatus.OK);
	    }
	 
	 @RequestMapping(value = "/person/{id}", method = RequestMethod.DELETE, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	    public ResponseEntity<?> deleteUser(@PathVariable("id") int id) {
	        logger.info("Fetching & Deleting person with id {}", id);
	        if (!personRepository.exists(id)) {
	            logger.error("Unable to delete. User with id {} not found.", id);
	            return new ResponseEntity(new CustomErrorType("Unable to delete. Person with id " + id + " not found."),
	                    HttpStatus.NOT_FOUND);
	        }
	        personRepository.delete(id);
	        return new ResponseEntity<Person>(HttpStatus.NO_CONTENT);
	    }
	 

}
