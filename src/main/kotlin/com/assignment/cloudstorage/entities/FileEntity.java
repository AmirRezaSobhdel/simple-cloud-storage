package com.assignment.cloudstorage.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "Files")
@Getter
@Setter
@ToString
public class FileEntity implements Serializable {

    @Id
    public String fileId;
    public String fileName;
    public String filePath;
    public String contentType;
    public String username;
    public Long fileSize = 0L;


    public FileEntity(String fileName, String filePath,long fileSize, String contentType, String userName) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.filePath = filePath;
        this.username = userName;
        this.fileId = userName + "-" + fileName;
        this.fileSize = fileSize;
    }

    public FileEntity(String id, String fileName, String filePath, String contentType, String userName) {
        this.fileId = id;
        this.fileName = fileName;
        this.filePath = filePath;
        this.contentType = contentType;
        this.username = userName;
    }

    public FileEntity() {
    }

    public String getProperSize() {

        final double actualSize = fileSize;
        if (actualSize == 0) {
            return "1B";
        }

        final double MB = (actualSize / (1024 * 1024));
        if (MB > 1) {
            return String.format("%.0f MB", MB);
        }

        final double KB = (actualSize / (1024));
        if (KB > 1) {
            return String.format("%.0f KB", KB);
        }

        return String.format("%.0f B", actualSize);

    }
}
