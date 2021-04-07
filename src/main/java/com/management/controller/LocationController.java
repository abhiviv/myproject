package com.management.controller;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.management.entity.City;
import com.management.entity.Country;
import com.management.entity.State;
//import com.management.repository.CityRepository;
import com.management.repository.CountryRepsitory;
import com.management.repository.StateRepository;

@RestController
@RequestMapping(path = "/web/location")
public class LocationController {

	
	@Autowired
	private CountryRepsitory  countryRepsitory;
	
//	@Autowired
//	private CityRepository cityRepository;
	
	@Autowired
	private StateRepository stateRepository;
	
	
	
	@GetMapping(path = "/getCountry")
	public List<Country> countries(){
		return countryRepsitory.findAll() ;
	}
	
	@GetMapping(path = "/getState/{id}")
	public Set<State> states(@PathVariable("id")Integer id){
		Country country=countryRepsitory.findById(id).get();
		return country.getState();
	}
	
	@GetMapping(path = "/getCity/{id}")
	public Set<City> cities(@PathVariable("id")Integer id){
		Optional<State> state=stateRepository.findById(id);
		if(state.isPresent()){
			State state2=state.get();
			return state2.getCity();
		}
		else {
			throw new com.management.exception.CustomeException("Value not present", "check value",
					"", "", "");
		}
	}
}
