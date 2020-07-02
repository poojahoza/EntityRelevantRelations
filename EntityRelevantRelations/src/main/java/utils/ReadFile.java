package main.java.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import main.java.containers.RankingJSONTemplate;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ReadFile {

    public List<String> readFile(String filelocation){
        List<String> lines = Collections.emptyList();
        try {
            lines = Files.readAllLines(Paths.get(filelocation), StandardCharsets.UTF_8);
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        return lines;
    }

    public Map<String, List<String>> getFilesfromFolder(String Folderlocation){
        Map<String, List<String>> converted_entity_ids = new HashMap<>();
        File folder = new File(Folderlocation);
        File[] listOfFiles = folder.listFiles();
        //System.out.println(listOfFiles.length);

        for (File file : listOfFiles) {
            if (file.isFile()) {
                //System.out.println(JSONlocation+file.getName());
                //System.out.println(file.getAbsolutePath());
                List<String> answer= readFile(file.getAbsolutePath());
                //System.out.println(answer.size());
                for(String s:answer){
                    String[] splited_text = s.split("\t");
                    //System.out.println(s.split("\t")[1]);
                    //System.out.println("******************");
                    List<String> entity_ids;
                    if(!(splited_text[0].trim().equals("enwiki:"))) {
                        if (converted_entity_ids.containsKey(splited_text[1])) {
                            entity_ids = converted_entity_ids.get(splited_text[1]);
                        } else {
                            entity_ids = new ArrayList<>();
                        }
                        entity_ids.add(splited_text[0]);
                        converted_entity_ids.put(splited_text[1], entity_ids);
                    }
                }

            }
        }
        return converted_entity_ids;
    }
}
