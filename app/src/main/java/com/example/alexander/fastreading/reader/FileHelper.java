package com.example.alexander.fastreading.reader;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Alexander on 04.08.2016.
 */
public class FileHelper {

    public static final String EPUB = "epub";
    public static final String TXT = "txt";

    private static final List<String> supportedExtensions;

    static {
        supportedExtensions = new ArrayList<String>();

        supportedExtensions.add(EPUB);
        supportedExtensions.add(TXT);
    }

    public static File[] readerFileFilter(File[] files){
        //Пропускает только доступные форматы
        if (files == null){
            return new File[0];
        }

        List<File> acceptedFile = new ArrayList<>(files.length);

        for (File file : files) {
            if (file.isHidden()){
                continue;
            }

            if (file.isDirectory()){
                acceptedFile.add(file);
            } else {
                String fileExtension = getFileExtension(file);
                if (supportedExtensions.contains(fileExtension)){
                    acceptedFile.add(file);
                }
            }
        }

        return acceptedFile.toArray(new File[acceptedFile.size()]);
    }

    public static String getFileExtension(File file) {
        String extension = null;
        String fileName = file.getName();

        int i = fileName.lastIndexOf('.');

        if (i > 0 && i < fileName.length() - 1) {
            extension = fileName.substring(i + 1).toLowerCase();
        }
        return extension;
    }

    public static String getTextFromFile(File file) throws IOException {
        StringBuilder text = new StringBuilder();

        InputStreamReader streamReader = new InputStreamReader(new FileInputStream(file), "utf8");
        BufferedReader bufferedReader = new BufferedReader(streamReader);

        String line;

        while ((line = bufferedReader.readLine()) != null) {
            text.append(line);
            text.append('\n');
        }
        bufferedReader.close();

        return text.toString();
    }

    public static String getTextFromFile(InputStream inputStream) throws IOException {
        StringBuilder text = new StringBuilder();

        InputStreamReader streamReader = new InputStreamReader(inputStream, "utf8");
        BufferedReader br = new BufferedReader(streamReader);
        String line;

        while ((line = br.readLine()) != null) {
            text.append(line);
            text.append('\n');
        }
        br.close();

        return text.toString();
    }

    public static void unZip(String zipFilePath, String targetDirectory) throws IOException {
        ZipFile zipFile =  new ZipFile(zipFilePath);

        int BUFFER = 4096;

        File rootDirectory = new File(targetDirectory);
        if(!rootDirectory.exists()){
            rootDirectory.mkdir();
        } else {
            rootDirectory.delete();
            rootDirectory.mkdir();
        }

        InputStream inputStream;

        List<? extends ZipEntry> zipEntries = Collections.list(zipFile.entries());

        for (ZipEntry zipEntry: zipEntries) {
            if (!zipEntry.isDirectory()){
                //Создаем в памяти файл с именем /Папка_куда_сохраняем/Имя_файла_в_архиве
                File file = new File(targetDirectory, zipEntry.getName());

                //Создаем все(!) родительские папки для данного фала
                String parentPath = file.getParent();
                File parentDirectories = new File(parentPath);
                parentDirectories.mkdirs();

                //Переписываем файл из архива на диск
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, BUFFER);

                int currentByte;
                byte data[] = new byte[BUFFER];

                inputStream = zipFile.getInputStream(zipEntry);

                while ((currentByte = inputStream.read(data, 0, BUFFER)) != -1) {
                    bufferedOutputStream.write(data, 0, currentByte);
                }

                bufferedOutputStream.flush();
                bufferedOutputStream.close();
            }
        }
        zipFile.close();
    }
}
