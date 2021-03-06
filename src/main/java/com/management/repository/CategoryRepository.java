package com.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.management.entity.ImageCategory;

@Repository
public interface CategoryRepository  extends JpaRepository<ImageCategory, Long>{

	ImageCategory findByCategoryName(String categoryName);
	
}
