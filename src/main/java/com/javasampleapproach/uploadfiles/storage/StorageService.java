package com.javasampleapproach.uploadfiles.storage;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService {

	Logger log = LoggerFactory.getLogger(this.getClass().getName());
	private final Path rootLocation = Paths.get("upload-dir");

	public void store(MultipartFile file) {
		try {
			Files.copy(file.getInputStream(), this.rootLocation.resolve(file.getOriginalFilename()));
		} catch (Exception e) {
			throw new RuntimeException("FAIL!");
		}
	}

	public Resource loadFile(String filename) {
		try {
			Path file = rootLocation.resolve(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new RuntimeException("FAIL!");
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("FAIL!");
		}
	}

	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}

	public void init() {
		try {
			Files.createDirectory(rootLocation);
		} catch (IOException e) {
			throw new RuntimeException("Could not initialize storage!");
		}
	}
  public String readFilePath(String file) throws IOException {
    BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(new FileInputStream(new File(file)), "UTF-8"));
    String f;
    int count=0;
    StringBuffer strBuffer = new StringBuffer();
    Map<String ,Integer> map = new HashMap<String,Integer>();
    while ((f=bufferedReader.readLine())!=null){
      StringTokenizer tokenizer = new StringTokenizer(f, " \t\n\r,:-.");
      while(tokenizer.hasMoreTokens()) {
        String s = tokenizer.nextToken();
        if(s.length()>2) {
          strBuffer.append(s);
          int cnt = map.get(s) != null ? map.get(s) : 0;
          cnt++;
          map.put(s, cnt);
        }
      }
    }
    return String.valueOf(map);
  }
}
