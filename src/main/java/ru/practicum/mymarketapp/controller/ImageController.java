package ru.practicum.mymarketapp.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class ImageController {

    @GetMapping("/download/{itemId}")
    public ResponseEntity<Resource> download(@PathVariable int itemId , @RequestParam String fileName) throws MalformedURLException {
        String line =  "src\\main\\resources\\images\\"+itemId + "\\"+ fileName;
        Path path = Paths.get(line);
        Resource file = new UrlResource(path.toUri());

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);

    }
}
