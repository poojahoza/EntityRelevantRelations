package main.java.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import main.java.containers.RankingJSONTemplate;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ReadJSONFile {

    public List<RankingJSONTemplate> readRankingJSONTemplate(String JSONlocation){
        List<RankingJSONTemplate> rankedParas = null;
        try {
            Reader reader = Files.newBufferedReader(Paths.get(JSONlocation));
            rankedParas = new Gson().fromJson(reader, new TypeToken<List<RankingJSONTemplate>>() {
            }.getType());
            //rankedParas.forEach(System.out::println);
            reader.close();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        return rankedParas;
    }

    public  List<RankingJSONTemplate> getRankingJSONfromFolder(String JSONlocation){
        List<RankingJSONTemplate> finalRankedParas = null;
        File folder = new File(JSONlocation);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                finalRankedParas.addAll(readRankingJSONTemplate(file.getName()));
            }
        }
        return finalRankedParas;
    }
}
