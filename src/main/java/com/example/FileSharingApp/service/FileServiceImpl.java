package com.example.FileSharingApp.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;

import com.example.FileSharingApp.entity.FileEntity;
import com.example.FileSharingApp.model.FileModel;
import com.example.FileSharingApp.repository.FileRepository;



@Service
public class FileServiceImpl implements FileService {
        

    @Autowired
    private FileRepository fileRepository;

    private FileModel convertToModel(FileEntity entity){
        FileModel model = new FileModel();
        BeanUtils.copyProperties(entity, model);
        return model;
    }

    @Override
    public List<FileModel> getAll() {
        List<FileEntity> entities = fileRepository.findAll();
        return entities.stream().map(this::convertToModel).collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<?> uploadFile(MultipartFile file, String uploadedBy) throws IOException {
        FileEntity entity = new FileEntity();
         entity.setFileName(file.getOriginalFilename());
         entity.setUploadedBy(uploadedBy);
         entity.setExpiryTime(LocalDateTime.now().plusDays(1));
         entity.setUploadTime(LocalDateTime.now());
         entity.setFileData(file.getBytes());
         fileRepository.save(entity);
         return ResponseEntity.ok().body(convertToModel(entity));
    }

    @Override
    public ResponseEntity<?> shareFile(int id) throws java.io.FileNotFoundException {
        Optional<FileEntity> entity = fileRepository.findById(id);
        if(entity.isPresent()){
            return ResponseEntity.ok().body(convertToModel(entity.get()));
        }
        else{
            throw new FileNotFoundException("File not Found");
        }
    }

    @Override
    public ResponseEntity<?> deletedFile(int id) throws java.io.FileNotFoundException {
        Optional<FileEntity> entity = fileRepository.findById(id);
        if(entity.isPresent()){
            fileRepository.delete(entity.get());
            return ResponseEntity.ok().body("File deleted successfully");
        }
        else{
            throw new FileNotFoundException("File not Found");
        }
    }

    @Override
    public ResponseEntity<?> getFile(int id) throws FileNotFoundException{
        Optional<FileEntity> entity = fileRepository.findById(id);
        if(entity.isPresent()){
            FileEntity fileEntity = entity.get();
            FileModel fileModel = new FileModel();
            BeanUtils.copyProperties(fileEntity, fileModel);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+fileModel.getFileName()+"\"" ).body(fileModel.getFileData());
        }
        else{
            throw new FileNotFoundException("File not Found");
        }
    }

    @Override
    @Scheduled(cron ="0 0 * * * *")
    public void deleteExpiredFiles() {
        List<FileEntity> entities = fileRepository.findByExpiryTimeBefore(LocalDateTime.now());
        entities.forEach(fileRepository::delete);
        System.out.println("Files deleted successfully "+LocalDateTime.now());
    }
    
}
