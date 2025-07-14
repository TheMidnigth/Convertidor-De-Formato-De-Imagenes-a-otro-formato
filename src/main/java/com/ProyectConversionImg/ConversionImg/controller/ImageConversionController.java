package com.ProyectConversionImg.ConversionImg.controller;

import com.ProyectConversionImg.ConversionImg.service.ImageConversionService;
import com.ProyectConversionImg.ConversionImg.utils.ZipUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.print.attribute.standard.Media;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/images")
public class ImageConversionController {

    private final ImageConversionService imageConversionService;

    //Todo:inyeccion por constructor
    public  ImageConversionController(ImageConversionService imageConversionService){
        this.imageConversionService = imageConversionService;
    }

    @PostMapping("/convert")
    public ResponseEntity<byte[]> convertMultipleImages(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("format") String format
    ) {
        try {
            Map<String, byte[]> convertedImages = new HashMap<>();

            for (MultipartFile file : files) {
                byte[] converted = imageConversionService.convertImage(file, format);

                String fileName = file.getOriginalFilename();
                String baseName = fileName != null ? fileName.replaceFirst("[.][^.]+$", "") : "converted";

                convertedImages.put(baseName + "." + format, converted);
            }

            byte[] zipBytes = ZipUtils.createZip(convertedImages);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "imagenes_convertidas.zip");

            return new ResponseEntity<>(zipBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    public MediaType getMediaType(String format){
        return switch (format.toLowerCase()){
            case "png" -> MediaType.IMAGE_PNG;
            case "jpg","jpeg" -> MediaType.IMAGE_JPEG;
            case "gif" -> MediaType.IMAGE_GIF;
            case "bmp" -> MediaType.valueOf("image/bmp");
            case "tiff", "tif" -> MediaType.valueOf("image/tiff");
            case  "webp" -> MediaType.valueOf("image/webp");
            default ->  MediaType.APPLICATION_OCTET_STREAM;
        };
    }
}
