package org.project.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.project.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageServiceImpl implements FileStorageService {

	//Set upload directory 
	private static final String UPLOAD_DIR = "src/main/resources/static/uploads"; 
	@Override
	public String saveImage(MultipartFile file) {
		// TODO Auto-generated method stub
		//check if file is empty 
		if (file.isEmpty()) {
			//throw new runtime exception 
			throw new RuntimeException("File is empty"); 
		}
		//get original file name  
		String originalFilename = StringUtils.cleanPath(file.getOriginalFilename()); 
		//Get extension (e.g: .jpeg, .jpg, .png,....)
		String extension = originalFilename.substring(originalFilename.lastIndexOf(".")); 
		//Create new filename by UUID + extension 
		String filename = UUID.randomUUID().toString() + extension; 
		//try-catch IOException 
		try {
			//Create uploadPath 
			Path uploadPath = Paths.get(UPLOAD_DIR); 
			//Check if the destination file to upload is not exist 
			if (!Files.exists(uploadPath)) {
				//create dir 
				Files.createDirectories(uploadPath); 
			}
			
			//get file path 
			Path filePath = uploadPath.resolve(filename); 
			//Copy file to filePath 
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING); 
			
			//return path 
			return "/uploads/" + filename; 
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage()); 
		}
	}
	
}
