package com.management.mapperDto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.management.dto.CategoryDto;
import com.management.dto.RegistrationDto;
import com.management.entity.ImageCategory;
import com.management.entity.Registration;

@Component
public class RegistrationDtomapper {
	
	public RegistrationDto registrationDto(Registration registration) {
		RegistrationDto registrationDto =new RegistrationDto();
		BeanUtils.copyProperties(registration, registrationDto);
		return registrationDto;
	}

	
	// uncompress the image bytes before returning it to the angular application
		public static byte[] decompressBytes(byte[] data) {
			Inflater inflater = new Inflater();
			inflater.setInput(data);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
			byte[] buffer = new byte[1024];
			try {
				while (!inflater.finished()) {
					int count = inflater.inflate(buffer);
					outputStream.write(buffer, 0, count);
				}
				outputStream.close();
			} catch (IOException ioe) {
			} catch (DataFormatException e) {
			}
			return outputStream.toByteArray();
		}
		
		
		public CategoryDto categoryDto(ImageCategory b) {
			CategoryDto categoryDto=new CategoryDto();
			BeanUtils.copyProperties(b, categoryDto);
			return categoryDto;
		}
}
