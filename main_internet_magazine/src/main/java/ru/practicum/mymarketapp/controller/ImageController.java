package ru.practicum.mymarketapp.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Controller
public class ImageController {
    @Value("${path.to.image}")
    String pathToImage;
    private static final byte[] PNG_PLACEHOLDER =
            Base64.getDecoder().decode(
                    "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMB/axu2kQAAAAASUVORK5CYII=");


    @GetMapping("/download/{itemId}")
    public Mono<ResponseEntity<Resource>> download(@PathVariable int itemId , @RequestParam String fileName) throws MalformedURLException {
        String line = pathToImage+"\\" +itemId + "\\"+ fileName;
        Path path = Paths.get(line);
        Resource file = new UrlResource(path.toUri());
        if (!file.exists()) {
            line = pathToImage+"\\notFound.png";
            path = Paths.get(line);
            file = new UrlResource(path.toUri());
            return Mono.just( ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + file.getFilename() + "\"").body(new ByteArrayResource(PNG_PLACEHOLDER)));
        }

        return Mono.just( ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file));

    }
}
