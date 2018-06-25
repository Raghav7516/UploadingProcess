package com.file.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.file.dto.UserDto;
import com.file.dto.UserRegister;
import com.file.model.User;
import com.file.repository.UserRepo;
import com.file.response.Response;

@Service
public class FileUploadService {

	@Autowired
	private UserRepo userRepo;

	@Value("${image}")
	private String imageLocation;

	@Transactional
	public Response<UserRegister> createUser(UserDto userDto) {
		Response<UserRegister> response = new Response<>();
		UserRegister userReg = new UserRegister();
		User user = new User();
		user.setFirstName(userDto.getFirstName());
		user.setLastName(userDto.getLastName());
		user.setAddress(userDto.getAddress());
		userRepo.save(user);
		userReg.setUserId(user.getUserId());
		userReg.setUserName(user.getFirstName() + " " + user.getLastName());
		response.setData(userReg);
		response.setMessage("user created successfully");
		return response;
	}

	public Response<String> imageUpload(MultipartFile file, Integer userId) throws IOException {
		Response<String> response = new Response<>();
		if (userId != null) {
			Path saveLocation = Paths.get(imageLocation);
			if (file.getContentType().contains("image") || file.getContentType().contains("video")) {
				String fileName = (file.getContentType().contains("image"))
						? (file.getName() + LocalTime.now() + ".jpg")
						: (file.getName() + LocalTime.now() + ".mp4");
				Files.copy(file.getInputStream(), saveLocation.resolve(fileName));
				User user = userRepo.findById(userId).get();
				if (file.getContentType().contains("image")) {
					user.setUserProfileName(fileName);
				} else {
					user.setUserVideoName(fileName);
				}
				userRepo.save(user);
				response.setData(user.getFirstName()+" "+user.getLastName());
				response.setMessage("user profile uploaded successfully");
			} else {
				response.setData(file.getContentType().replace("application/", "") + " file can't upload");
				response.setMessage("file upload un-success");
			}
		} else {
			response.setMessage(userId == null ? "userId not found" : null);
		}
		return response;
	}

	public ResponseEntity<?> getUserProfile(String file, Integer userId) throws FileNotFoundException {
		Response<String> response=new Response<>();
		Optional<User> user = userRepo.findById(userId);
		if (user.isPresent()) {
			return file != null ? downloadUserFile(file)
					: downloadUserFile(userRepo.findById(userId).get().getUserProfileName());
		}else {
			response.setMessage("user not found");
			return ResponseEntity.ok().body(response);
		}
	}

	private ResponseEntity<InputStreamResource> downloadUserFile(String file) throws FileNotFoundException {
		File image = new File(imageLocation + "/" + file);
		InputStreamResource resource = new InputStreamResource(new FileInputStream(image));
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + image.getName())
				.contentType(MediaType.parseMediaType("application/octet-stream")).contentLength(image.length())
				.body(resource);
	}

	public Response<List<String>> getUserFiles(Integer userId) {
		Response<List<String>> response = new Response<>();
		Optional<User> users = userRepo.findById(userId);
		if (users.isPresent()) {
			response.setData(Arrays.asList("UserProfile : "+users.get().getUserProfileName(), "UserVideo : "+users.get().getUserVideoName()));
			response.setMessage("user list of files");
		} else {
			response.setMessage("user id  not present in database");
		}
		return response;
	}

	public ResponseEntity<?> getUserVideoName(Integer userId) throws FileNotFoundException {
		Response<String> response=new Response<>();
		Optional<User> user = userRepo.findById(userId);
		if (user.isPresent())
			return downloadUserFile(user.get().getUserVideoName());
		response.setMessage("user not found");
		return ResponseEntity.ok().body(response);
	}

	public ResponseEntity<?> getZipFiles(Integer userId) throws IOException {
		Response<List<String>> response=new Response<>();
		Optional<User> users = userRepo.findById(userId);
		if (users.isPresent()) {
			List<String> files=Arrays.asList(users.get().getUserProfileName(),users.get().getUserVideoName());
			ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream("myfile.zip"));
			for (String file : files) {
				File image = new File(imageLocation + "/" + file);
		        zipOutputStream.putNextEntry(new ZipEntry(image.getName()));
		        FileInputStream inputStream = new FileInputStream(image);
		        final byte[] buffer = new byte[1024];
		        int length;
		        while((length = inputStream.read(buffer)) >= 0) {
		            zipOutputStream.write(buffer, 0, length);
		        }
		        inputStream.close();
			}
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION)
					.contentType(MediaType.parseMediaType("application/zip")).body(zipOutputStream);
		} else {
			response.setMessage("user id  not present in database");
			return ResponseEntity.ok().body(response);
		}
	}

}
