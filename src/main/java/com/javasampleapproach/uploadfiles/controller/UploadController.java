package com.javasampleapproach.uploadfiles.controller;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.javasampleapproach.uploadfiles.storage.StorageService;
@CrossOrigin
@RestController
public class UploadController {

	@Autowired
	StorageService storageService;

	List<String> files = new ArrayList<String>();

	@PostMapping("/post")
	public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
		String message = "";
		try {
			storageService.store(file);
			files.add(file.getOriginalFilename());

			message = "You successfully uploaded " + file.getOriginalFilename() + "!";
			return ResponseEntity.status(HttpStatus.OK).body(message);
		} catch (Exception e) {
			message = "FAIL to upload " + file.getOriginalFilename() + "!";
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
		}
	}

	@GetMapping("/getallfiles")
	public ResponseEntity<List<String>> getListFiles() {
	  List<String> dirlist = new ArrayList<>();
    Path dir =  Paths.get("upload-dir");
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
      for (Path file: stream) {
        dirlist.add(file.getFileName().toString());
        System.out.println(file.getFileName());
      }
    } catch (IOException | DirectoryIteratorException x) {
      // IOException can never be thrown by the iteration.
      // In this snippet, it can only be thrown by newDirectoryStream.
      System.err.println(x);
    }
//    try {
//    Stream<Path> files =  Files.list(Paths.get("\\upload-dir"));
//      files.collect(Collectors.toList());
////        .forEach(System.out::println);
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//    List<String> fileNames = files
//				.stream().map(fileName -> MvcUriComponentsBuilder
//						.fromMethodName(UploadController.class, "getFile", fileName).build().toString())
//				.collect(Collectors.toList());
		return ResponseEntity.ok().body(dirlist);

	}


	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<String> getFile(@PathVariable String filename){
		Resource file = storageService.loadFile(filename);

    String s = null;
    try {
      s = storageService.readFilePath("upload\\"+file.getFilename() +".txt");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file+" "+s);
	}

	@GetMapping("/words/{filename}")
  @ResponseBody
  public ResponseEntity<String> getWorksCount(@PathVariable String filename) throws IOException {

    return ResponseEntity.ok().body(storageService.readFilePath("upload\\"+filename));
  }
}
//  Resource file = storageService.loadFile(filename);
