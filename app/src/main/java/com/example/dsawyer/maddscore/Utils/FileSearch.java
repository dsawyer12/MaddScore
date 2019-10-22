package com.example.dsawyer.maddscore.Utils;

import java.io.File;
import java.util.ArrayList;

public class FileSearch {

    public static ArrayList<String> getDirectoryPaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listFiles = file.listFiles();
        for (File listFile : listFiles) {
            if (listFile.isDirectory()) {
                pathArray.add(listFile.getAbsolutePath());
            }
        }
        return pathArray;
    }

    public static ArrayList<String> getFilePath(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listFiles = file.listFiles();
        for (File listFile : listFiles) {
            if (listFile.isFile()) {
                pathArray.add(listFile.getAbsolutePath());
            }
        }
        return pathArray;
    }
}
