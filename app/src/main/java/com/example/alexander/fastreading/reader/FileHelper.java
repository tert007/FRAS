package com.example.alexander.fastreading.reader;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
    public static final String FB2 = "fb2";
    public static final String FB2_ZIP = "fb2.zip";

    private static final List<String> supportedExtensions;

    static {
        supportedExtensions = new ArrayList<>();

        supportedExtensions.add(EPUB);
        supportedExtensions.add(TXT);
        supportedExtensions.add(FB2);
        supportedExtensions.add(FB2_ZIP);
    }

    public static List<File> readerFileFilter(File[] files){
        //Пропускает только доступные форматы
        if (files == null){
            return Collections.emptyList();
        }

        List<File> acceptedFiles = new ArrayList<>(files.length);

        for (File file : files) {
            if (file.isHidden()){
                continue;
            }

            if (file.isDirectory()){
                acceptedFiles.add(file);
            } else {
                String fileExtension = getFileExtension(file);
                if (supportedExtensions.contains(fileExtension)){
                    acceptedFiles.add(file);
                }
            }
        }

        return acceptedFiles;
    }

    public static String getFileExtension(File file) {
        return getFileExtension(file.getName());
    }

    public static String getFileName(File file) {
        //using when we need to get name from BookTitle.txt -> BookTitle
        String fileName = file.getName();
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    public static String getFileName(String filePath) {
        //using when we need to get name from BookTitle.txt -> BookTitle
        String fileName = new File(filePath).getName();
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    public static String getFileExtension(String filePath) {
        String fileName = new File(filePath).getName();
        String extension = null;

        int i = fileName.lastIndexOf('.');

        if (i > 0 && i < fileName.length() - 1) {
            extension = fileName.substring(i + 1).toLowerCase();

            if (extension.equals("zip")) {
                int zipIndex = fileName.lastIndexOf(FB2_ZIP);

                if (zipIndex > 1 && zipIndex == fileName.length() - FB2_ZIP.length()) {
                    if (fileName.charAt(zipIndex - 1) == '.') {
                        return FB2_ZIP;
                    }
                }
            }
        }

        return extension;
    }

    public static String getEncoding(File file) throws IOException {
        FileInputStream fis = null;
        UniversalDetector detector = new UniversalDetector(null);
        String encoding = null;
        byte[] buf = new byte[4096];
        fis = new FileInputStream(file);
        int nread;
        while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread);
        }
        detector.dataEnd();
        encoding = detector.getDetectedCharset();

        return encoding;
    }

    public static String getTextFromFile(File file, String charset) throws IOException {
        InputStreamReader streamReader = new InputStreamReader(new FileInputStream(file), charset);

        return getTextFromFile(streamReader);
    }

    public static String getTextFromFile(String filePath, String charset) throws IOException {
        InputStreamReader streamReader = new InputStreamReader(new FileInputStream(filePath), charset);

        return getTextFromFile(streamReader);
    }

    public static String getTextFromFile(InputStream inputStream, String charset) throws IOException {
        InputStreamReader streamReader = new InputStreamReader(inputStream, charset);

        return getTextFromFile(streamReader);
    }

    public static String getTextFromFile(File file) throws IOException {
        InputStreamReader streamReader = new InputStreamReader(new FileInputStream(file));

        return getTextFromFile(streamReader);
    }

    public static String getTextFromFile(String filePath) throws IOException {
        InputStreamReader streamReader = new InputStreamReader(new FileInputStream(filePath));

        return getTextFromFile(streamReader);
    }

    public static String getTextFromFile(InputStream inputStream) throws IOException {
        InputStreamReader streamReader = new InputStreamReader(inputStream);

        return getTextFromFile(streamReader);
    }


    private static String getTextFromFile(InputStreamReader streamReader) throws IOException {
        StringBuilder text = new StringBuilder();

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
            removeDirectory(rootDirectory);
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

    public static void copyFile(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            if (is != null){
                is.close();
            }
            if (os != null){
                os.close();
            }
        }
    }


    public static void copyFile(InputStream inputStreamSource, File dest) throws IOException {
        OutputStream os = null;
        try {
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStreamSource.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            if (inputStreamSource != null){
                inputStreamSource.close();
            }
            if (os != null){
                os.close();
            }
        }
    }

    public static boolean removeDirectory(File directory) {
        if(directory.exists()){
            File[] files = directory.listFiles();
            if(files != null){
                for (File file: files) {
                    if(file.isDirectory()) {
                        removeDirectory(file);
                    }
                    else {
                        file.delete();
                    }
                }
            }
        }
        return(directory.delete());
    }

    public static List<File> getFilesCollection(String filePath) {
        File root = new File(filePath);
        File[] tempFiles =  root.listFiles();
        if (tempFiles == null)
            return Collections.emptyList();

        List<File> files = new ArrayList<>();

        for (File file : tempFiles) {
            if (file.isFile()) {
                files.add(file);
            } else {
                getFilesRecursive(file, files);
            }
        }

        return files;
    }

    private static List<File> getFilesRecursive(File file, List<File> outFilesList) {
        if (file.isFile()){
            outFilesList.add(file);

            return outFilesList;
        }

        File[] tempFiles =  file.listFiles();
        if (tempFiles == null)
            return outFilesList;

        for(File file1 : tempFiles) {
            getFilesRecursive(file1, outFilesList);
        }

        return outFilesList;
    }

}
