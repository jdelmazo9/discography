package org.wyeworks.services.album;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.wyeworks.dtos.FileAlbumDTO;
import org.wyeworks.exceptions.ReadingFileException;
import org.wyeworks.mappers.FileAlbumMapper;
import org.wyeworks.model.Album;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FileAlbumService implements AlbumService {

    @Value("${albums.file.path}")
    private Resource resourceFile;

    @Autowired
    private FileAlbumMapper fileAlbumMapper;

    @Override
    public List<Album> getAllAlbums() {
        List<FileAlbumDTO> fileAlbums = readAlbumsFromFile();
        return fileAlbumMapper.convert(fileAlbums);
    }

    private List<FileAlbumDTO> readAlbumsFromFile() {
        List<String> albumsStringList;
        try {
            if(resourceFile == null || !resourceFile.exists()) {
                throw new ReadingFileException("The resource is bad referenced or not exists");
            }
            InputStream inputStream = resourceFile.getInputStream();
            albumsStringList = IOUtils.readLines(inputStream, Charset.defaultCharset());
        } catch (IOException e) {
            String message = "Couldn't read file: " + resourceFile;
            log.error(message);
            throw new ReadingFileException(message, e);
        }

        //This algorithm parses the information file
        return albumsStringList.stream()
                .map(line -> {
                    Integer yearValue;
                    String albumName;
                    try {
                        yearValue = Integer.valueOf(line.substring(0, 4));
                        albumName = line.substring(5);
                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                        String message = "There is a problem with the file format";
                        log.error(message);
                        throw new ReadingFileException(message, e);
                    }

                    return FileAlbumDTO.builder()
                            .year(yearValue)
                            .name(albumName)
                            .build();
                })
                .collect(Collectors.toList());
    }

}
