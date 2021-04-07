package com.management.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.management.dto.CategoryDto;
import com.management.entity.ImageCategory;
import com.management.service.CategoryServices;

@RestController
@RequestMapping(path = "/web/category")
public class CategoryController {

	@Autowired
	private CategoryServices categoryService;
	
	@PostMapping(path = "/addCategory")
	public ImageCategory category(@RequestBody ImageCategory imageCategory) {
		return categoryService.saveImageCategory(imageCategory);
	}
	
	@GetMapping(path = "/getcategory")
	public List<CategoryDto> imageCategories(){
		return categoryService.getImageCategory();
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(path = "/categoryImgUpload/{id}",consumes = { "multipart/form-data" })
	public  ResponseEntity uploadImg(@RequestParam("file") MultipartFile file,@PathVariable("id")Long id) {
		return categoryService.uploadToLocalFile(file,id);
	}
}

