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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("api/images")
public class ImageConversionController {

    private final ImageConversionService imageConversionService;

    //Todo:inyeccion por constructor
    public  ImageConversionController(ImageConversionService imageConversionService){
        this.imageConversionService = imageConversionService;
    }

    //Todo: Para convertir una sola imagen
    @PostMapping("/convert")
    public ResponseEntity<byte[]> convertImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("format") String format
    ) {
        try {
            byte[] converted = imageConversionService.convertImage(file, format);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(getMediaType(format));
            headers.setContentDispositionFormData("attachment", "imagen_convertida." + format);

            return new ResponseEntity<>(converted, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //Todo: Para convertir varias imagenes
    @PostMapping("/convert-Multiple")
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

    @PostMapping("/convert-to-webp")
    public ResponseEntity<byte[]> convertToWebP(@RequestParam("file") MultipartFile file) {
        try {
            byte[] converted = imageConversionService.convertToWebP(file);

            String originalName = file.getOriginalFilename();
            String baseName = (originalName != null)
                    ? originalName.replaceFirst("[.][^.]+$", "")
                    : "converted";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf("image/webp"));
            headers.setContentDispositionFormData("attachment", baseName + ".webp");
            headers.setContentLength(converted.length);

            return new ResponseEntity<>(converted, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace(); // Para debug en consola
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error al convertir la imagen: " + e.getMessage()).getBytes());
        }
    }

    @PostMapping("/convert-multiple-webp")
    public ResponseEntity<byte[]> convertMultipleToWebP(@RequestParam("files") MultipartFile[] files) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ZipOutputStream zipOut = new ZipOutputStream(byteArrayOutputStream)) {

            for (MultipartFile file : files) {
                byte[] converted = imageConversionService.convertToWebP(file);

                String originalName = file.getOriginalFilename();
                String baseName = (originalName != null)
                        ? originalName.replaceFirst("[.][^.]+$", "")
                        : "converted";

                ZipEntry zipEntry = new ZipEntry(baseName + ".webp");
                zipOut.putNextEntry(zipEntry);
                zipOut.write(converted);
                zipOut.closeEntry();
            }

            zipOut.finish();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "converted-images.zip");

            return new ResponseEntity<>(byteArrayOutputStream.toByteArray(), headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error al convertir múltiples imágenes: " + e.getMessage()).getBytes());
        }
    }

    public MediaType getMediaType(String format) {
        return switch (format.toLowerCase()) {
            case "png" -> MediaType.IMAGE_PNG;
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "gif" -> MediaType.IMAGE_GIF;
            case "bmp" -> MediaType.valueOf("image/bmp");
            case "tiff", "tif" -> MediaType.valueOf("image/tiff");
            case "webp" -> MediaType.valueOf("image/webp");
            case "avif" -> MediaType.valueOf("image/avif");  // 👈 agregamos AVIF
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }
}
