package com.file.controller;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.file.dto.UserDto;
import com.file.dto.UserRegister;
import com.file.response.Response;
import com.file.service.FileUploadService;

@RestController
public class FileUploadController {

	@Autowired
	private FileUploadService fileUploadService;

	@Value("${image}")
	private String imageLocation;
	
	@PostMapping("reg")
	public Response<UserRegister> createUser(@Valid @RequestBody UserDto user) {
		return fileUploadService.createUser(user);
	}

	@PostMapping("/user")
	public Response<String> imageUpload(@RequestParam("file") MultipartFile file,
			@RequestParam(required = true) Integer userId) throws IOException {
		return fileUploadService.imageUpload(file, userId);
	}

	@GetMapping(value = { "/file/{file:.+}", "/user/file/{userId}" })
	public ResponseEntity<?> getUserProfile(@PathVariable(required = false) String file,
			@PathVariable(required = false) Integer userId) throws IOException {
		return fileUploadService.getUserProfile(file, userId);
	}

	@GetMapping("/user/video/{userId}")
	public ResponseEntity<?> getUserVideoName(@PathVariable Integer userId) throws IOException {
		return fileUploadService.getUserVideoName(userId);
	}

	@GetMapping("/user/{userId}")
	public Response<List<String>> getUserFiles(@PathVariable Integer userId) throws IOException {
		return fileUploadService.getUserFiles(userId);
	}
	@GetMapping("/file/user")
	public ResponseEntity<?> getZipFile(@RequestParam Integer userId) throws IOException{
		return fileUploadService.getZipFiles(userId);
		
	}
}
