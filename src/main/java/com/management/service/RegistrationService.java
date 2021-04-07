package com.management.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.Deflater;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.management.common.Role;
import com.management.dto.RegistrationDto;
import com.management.entity.City;
import com.management.entity.Country;
import com.management.entity.Registration;
import com.management.entity.State;
import com.management.mapperDto.RegistrationDtomapper;
import com.management.repository.CityRepository;
import com.management.repository.CountryRepsitory;
import com.management.repository.RegistrationRepository;
import com.management.repository.StateRepository;

@Service
@Transactional
public class RegistrationService {

	@Autowired
	private RegistrationRepository registrationRepository;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private RegistrationDtomapper registrationDtomapper;

	@Autowired
	private MailService mailService;
	
	@Autowired
	private CountryRepsitory  countryRepsitory;
	
	@Autowired
	private CityRepository cityRepository;
	
	@Autowired
	private StateRepository stateRepository;

	public RegistrationDto getuserdetails() {
		Object authentication = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String loggedInUserEmailId = (String) authentication;
		Registration loginusers = registrationRepository.findByEmail(loggedInUserEmailId);
		return registrationDtomapper.registrationDto(loginusers);
	}

	public Boolean registration(Registration registration) {
		String EmailId = registration.getEmail();
		Registration checkDublicate = registrationRepository.findByEmail(EmailId);
		if (checkDublicate != null) {
			throw new com.management.exception.CustomeException("Check Emaild", "Emaiid Allready used",
					"Enter different EmailId", "then click Submit", "if and issued contact us");
		} else {
			registration.setPassword(securityService.encodePassword(registration.getPassword()));
			registrationRepository.save(registration);
			return true;
		}

	}

	public Boolean UserRegistartion(Registration registration) {
		String EmailId = registration.getEmail();
		Registration checkDublicate = registrationRepository.findByEmail(EmailId);
		if (checkDublicate != null) {
			throw new com.management.exception.CustomeException("Check Emaild", "Emaiid Allready used",
					"Enter different EmailId", "then click Submit", "if and issued contact us");
		} else {
			registration.setPassword(securityService.encodePassword(registration.getPassword()));
			mailService.sendSimpleMessage(registration.getEmail(), "Register Sucessfully",
					"Welcome " + registration.getName());
			registration.setRole(Role.ROLE_USER.toString());
			registrationRepository.save(registration);
			return true;
		}
	}

	public Boolean CheckDublicate(String EmailId) {
		Registration checkDublicate = registrationRepository.findByEmail(EmailId);
		if (checkDublicate != null) {
			return false;
		}
		return true;
	}

	public Registration updateData(Registration registration) {
		Object authentication = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String loggedInUserEmailId = (String) authentication;
		Registration checkDublicate = registrationRepository.findByEmail(loggedInUserEmailId);
		
		if (checkDublicate != null) {
			if(registration.getEmail().isEmpty() || registration.getName().isEmpty()) {
				throw new com.management.exception.CustomeException("Some Fields Are Misssing", "please Fill all * mark fields",
						"", "then click Submit", "if and issued contact us");
			}
			
			if(registration.getCity().getCityId()==0 || registration.getCountry().getCountryId()==0 || registration.getState().getStateId()==0) {
				copy(registration, checkDublicate);
				return registrationRepository.save(checkDublicate);	
			}
			else {
			copydata(registration, checkDublicate);
			System.out.println("=========================="+registration.getEmail());
			Country country=countryRepsitory.findById(registration.getCountry().getCountryId()).get();
			checkDublicate.setCountry(country);
			City city=cityRepository.findById(registration.getCity().getCityId()).get();
			checkDublicate.setCity(city);
			State state=stateRepository.findById(registration.getState().getStateId()).get();
			checkDublicate.setState(state);
			registrationRepository.save(checkDublicate);
			return checkDublicate;
			}
		}
		return checkDublicate;

	}

	private void copy(Registration newregistartion, Registration existing) {
		existing.setName(newregistartion.getName());
		existing.setPhoneno(newregistartion.getPhoneno());
		existing.setEmail(newregistartion.getEmail());
		existing.setAbout(newregistartion.getAbout());
		existing.setZipCode(newregistartion.getZipCode());
	}

	public void copydata(Registration newregistartion, Registration existing) {
		existing.setName(newregistartion.getName());
		existing.setPhoneno(newregistartion.getPhoneno());
		existing.setEmail(newregistartion.getEmail());
		existing.setAbout(newregistartion.getAbout());
		existing.setCountry(newregistartion.getCountry());
		existing.setCity(newregistartion.getCity());
		existing.setState(newregistartion.getState());
		existing.setZipCode(newregistartion.getZipCode());
	}

//	@Cacheable("registration")
	public List<RegistrationDto> filter() {
		return registrationRepository.findAll().stream().map(registrationDtomapper::registrationDto).collect(Collectors.toList());
	}


	
	public org.springframework.http.ResponseEntity.BodyBuilder uploadImage(MultipartFile file) throws IOException {
		Object authentication = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String loggedInUserEmailId = (String) authentication;
		Registration loginusers = registrationRepository.findByEmail(loggedInUserEmailId);
		if (loginusers != null) {
			System.out.println("Original Image Byte Size - " + file.getBytes().length);
			loginusers.setPicByte(compressBytes(file.getBytes()));
			registrationRepository.save(loginusers);	
		} 
		return ResponseEntity.status(HttpStatus.OK);
	}

// compress the image bytes before storing it in the database
	public static byte[] compressBytes(byte[] data) {
		Deflater deflater = new Deflater();
		deflater.setInput(data);
		deflater.finish();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
		byte[] buffer = new byte[1024];
		while (!deflater.finished()) {
			int count = deflater.deflate(buffer);
			outputStream.write(buffer, 0, count);
		}
		try {
			outputStream.close();
		} catch (IOException e) {
		}
		System.out.println("Compressed Image Byte Size - " + outputStream.toByteArray().length);

		return outputStream.toByteArray();

	}

	

}
