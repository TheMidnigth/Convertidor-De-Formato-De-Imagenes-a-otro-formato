package com.ProyectConversionImg.ConversionImg.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

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

    private static final String CWEBP_PATH = "C:\\Users\\Keyner Reyes\\Downloads\\libwebp-1.4.0-windows-x64\\libwebp-1.4.0-windows-x64\\bin\\cwebp";

    public byte[] convertToWebP(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = (originalFilename != null && originalFilename.contains("."))
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".png"; // Por defecto PNG si no hay extensión

        File tempInput = null;
        File tempOutput = null;

        try {
            tempInput = File.createTempFile("input-", extension);
            file.transferTo(tempInput);

            tempOutput = File.createTempFile("output-", ".webp");

            ProcessBuilder pb = new ProcessBuilder(
                    CWEBP_PATH,
                    tempInput.getAbsolutePath(),
                    "-q", "80",
                    "-o", tempOutput.getAbsolutePath()
            );

            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Error al convertir la imagen con cwebp. Código de salida: " + exitCode);
            }

            return java.nio.file.Files.readAllBytes(tempOutput.toPath());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Conversión interrumpida", e);
        } finally {
            if (tempInput != null && tempInput.exists()) {
                tempInput.delete();
            }
            if (tempOutput != null && tempOutput.exists()) {
                tempOutput.delete();
            }
        }
    }
}
