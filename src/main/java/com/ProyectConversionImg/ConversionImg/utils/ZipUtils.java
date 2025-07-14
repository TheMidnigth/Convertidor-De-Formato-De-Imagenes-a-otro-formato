package com.ProyectConversionImg.ConversionImg.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    /**
     * Crea un archivo ZIP en memoria con los archivos recibidos.
     *
     * @param files Mapa donde la clave es el nombre del archivo y el valor es su contenido en bytes.
     * @return Un arreglo de bytes que representa el archivo ZIP.
     * @throws IOException Si ocurre un error al escribir el ZIP.
     */

    public static byte[] createZip(Map<String, byte[]> files) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zipOut = new ZipOutputStream(baos)) {
            for (Map.Entry<String, byte[]> fileEntry : files.entrySet()) {
                ZipEntry zipEntry = new ZipEntry(fileEntry.getKey());
                zipOut.putNextEntry(zipEntry);
                zipOut.write(fileEntry.getValue());
                zipOut.closeEntry();
            }
        }
        return baos.toByteArray();
    }
}
