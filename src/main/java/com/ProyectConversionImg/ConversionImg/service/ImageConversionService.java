package com.ProyectConversionImg.ConversionImg.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ImageConversionService {

    public byte[] convertImage(MultipartFile file,String format) throws IOException{
        BufferedImage image = ImageIO.read(file.getInputStream());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image,format,outputStream);
        return outputStream.toByteArray();
    }
}
