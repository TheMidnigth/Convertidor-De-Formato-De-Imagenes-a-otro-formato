package com.ProyectConversionImg.ConversionImg.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class ImageConversionService {

    public byte[] convertImage(MultipartFile file, String format) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());

        if (image == null) {
            System.out.println("❌ No se pudo leer la imagen. Archivo corrupto o formato no soportado.");
            throw new IOException("No se pudo leer la imagen.");
        } else {
            System.out.println("✅ Imagen leída correctamente.");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        boolean written = ImageIO.write(image, format, outputStream);

        if (!written) {
            System.out.println("❌ No se pudo convertir la imagen al formato: " + format);
            throw new IOException("No se pudo convertir la imagen al formato: " + format);
        } else {
            System.out.println("✅ Imagen convertida correctamente a: " + format);
        }

        return outputStream.toByteArray();
    }

    private static final String CWEBP_PATH = "C:\\Users\\Keyner Reyes\\Downloads\\libwebp-1.4.0-windows-x64\\bin\\cwebp.exe";

    public byte[] convertToWebP(MultipartFile file) throws IOException {
        // Guardar el archivo temporalmente
        File tempInput = File.createTempFile("input-", "-" + file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(tempInput)) {
            fos.write(file.getBytes());
        }

        // Definir archivo de salida
        File tempOutput = File.createTempFile("output-", ".webp");

        // Ejecutar el comando cwebp
        ProcessBuilder pb = new ProcessBuilder(
                CWEBP_PATH,
                tempInput.getAbsolutePath(),
                "-o",
                tempOutput.getAbsolutePath()
        );

        Process process = pb.start();

        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Error al convertir la imagen con cwebp, código: " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Conversión interrumpida", e);
        }

        // Leer el archivo convertido
        byte[] convertedBytes = java.nio.file.Files.readAllBytes(tempOutput.toPath());

        // Limpiar archivos temporales
        tempInput.delete();
        tempOutput.delete();

        return convertedBytes;
    }
}
