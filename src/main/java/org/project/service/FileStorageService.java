package org.project.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
	//save image and get name
	String saveImage(MultipartFile file); 
}
