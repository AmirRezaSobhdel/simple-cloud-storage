package com.assignment.cloudstorage.services;

import com.assignment.cloudstorage.entities.FileEntity;
import com.assignment.cloudstorage.repos.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    public FileEntity uploadFile(MultipartFile multipartFile, String username) {
        FileEntity dbFileEntity = null;
        try {

            File file = new File("src/main/resources/files/" + username + "-" + multipartFile.getOriginalFilename());
            file.createNewFile();

            multipartFile.transferTo(Paths.get(file.getAbsolutePath()));

            dbFileEntity = new FileEntity(multipartFile.getOriginalFilename(), file.getAbsolutePath(),
                    multipartFile.getSize(),
                    multipartFile.getContentType(),
                    username);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Problem occured while saving data into FileEntity table");
        }
        if (dbFileEntity != null)
            return fileRepository.save(dbFileEntity);

        return null;

    }

    public List<FileEntity> provideFiles(String username) {
        List<FileEntity> l = fileRepository.getAllByUsername(username);
        List<FileEntity> files = new ArrayList();
        for (FileEntity f : l) {
            f.setFileId(f.getUsername()+"-"+f.getFileName());
            files.add(f);
        }
        return files;
    }

    public FileEntity provideFile(String fileId, String username) {
        return fileRepository.getUserFileByFileIdAndUsername(username, fileId);
    }

    public void removeFile(String fileId) {
        fileRepository.deleteByFileId(fileId);
    }
}
