package com.example;


import lombok.Data;
import lombok.Getter;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


@Data public class FileInfo implements Serializable {
   @Getter public enum FileType {
        FILE("F"), DIRECTORY("D");
        private String name;

        FileType(String type) {
            this.name = type;
        }
    }

    private String fileName;
    private FileType type;
    private long fileSize;
    private LocalDateTime lastModified;

    public FileInfo (Path path){
        try {
            this.fileName = path.getFileName().toString();
            this.type = Files.isDirectory(path) ? FileType.DIRECTORY : FileType.FILE;
            this.fileSize = Files.size(path);
            if (this.type == FileType.DIRECTORY){
                this.fileSize = -1;
            }
            this.lastModified = LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneOffset.ofHours(7));

        } catch (IOException e) {
            throw  new RuntimeException("ALERT ");
        }

    }
}
