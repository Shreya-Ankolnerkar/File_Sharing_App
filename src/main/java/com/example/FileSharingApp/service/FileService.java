package com.example.FileSharingApp.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.example.FileSharingApp.model.FileModel;

public interface FileService {
     public List<FileModel> getAll();
     public ResponseEntity<?> uploadFile(MultipartFile file, String uploadedBy) throws IOException;
     public ResponseEntity<?> shareFile(int id) throws FileNotFoundException;
     public ResponseEntity<?> deletedFile(int id) throws FileNotFoundException;
     public ResponseEntity<?> getFile(int id) throws FileNotFoundException;
     public void deleteExpiredFiles();
} 
