package com.management.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.management.dto.CategoryDto;
import com.management.entity.ImageCategory;
import com.management.exception.CustomeException;
import com.management.mapperDto.RegistrationDtomapper;
import com.management.repository.CategoryRepository;


@Service
@Transactional(rollbackOn = CustomeException.class)
public class CategoryServices {

	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private RegistrationDtomapper registrationDtomapper;
	
	public ImageCategory saveImageCategory(ImageCategory imageCategory) {
		ImageCategory imageCategory2=categoryRepository.findByCategoryName(imageCategory.getCategoryName());
		if(imageCategory2!=null) {
			throw new com.management.exception.CustomeException("Check Categoty Name", "Allready used",
					"Enter different Name", "then click Submit", "if and issued contact us");
		}
		return categoryRepository.save(imageCategory);	
	}
	
	
	
	@SuppressWarnings("rawtypes")
	public ResponseEntity uploadToLocalFile(MultipartFile file,Long id) {
		 ImageCategory category=categoryRepository.findById(id).get();
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		 File folder=new File("E:\\temp\\"+category.getCategoryName()+"\\");
         System.out.println(folder);
         folder.mkdirs();
	     System.out.println(fileName);
		 Path path = Paths.get("E:\\temp\\"+category.getCategoryName()+"\\"+ file.getOriginalFilename());
		
		    category.setCategoryImage(fileName);
		    categoryRepository.save(category);
		try {
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/files/download/")
				.path(fileName)
				.toUriString();
		return ResponseEntity.ok(fileDownloadUri);
	}	
	
	
	
	
	
	public List<CategoryDto> getImageCategory() {
		return categoryRepository.findAll().stream().map(registrationDtomapper::categoryDto).collect(Collectors.toList());
	}
}