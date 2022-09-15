package org.wyeworks.service;

import lombok.extern.slf4j.Slf4j;
import org.wyeworks.dto.FileAlbumDTO;
import org.wyeworks.exception.ReadingFileException;
import org.wyeworks.mapper.FileAlbumMapper;
import org.wyeworks.model.Album;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class FileAlbumService implements AlbumService {

    private final String pathToFile;

    public FileAlbumService(String pathToFile) {
        this.pathToFile = pathToFile;
    }

    @Override
    public List<Album> getAllAlbums() {
        List<FileAlbumDTO> fileAlbums = readAlbumsFromFile();
        return FileAlbumMapper.INSTANCE.convert(fileAlbums);
    }

    private List<FileAlbumDTO> readAlbumsFromFile() {
        List<String> albumsStringList;
        try {
            albumsStringList = Files.readAllLines(Paths.get(pathToFile));
        } catch (IOException e) {
            String message = "Couldn't read file :" + pathToFile;
            log.error(message);
            throw new ReadingFileException(message, e);
        }

        /*
        This algorithm parses the information file
         */
        return albumsStringList.stream()
                .map(line -> new FileAlbumDTO(Integer.valueOf(line.substring(0, 4)), line.substring(5)))
                .collect(Collectors.toList());
    }

}
