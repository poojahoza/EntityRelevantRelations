package main.java.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import main.java.containers.RankingJSONTemplate;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ReadJSONFile {

    private String JSONFilelocation;

    public ReadJSONFile(String JSONFileloc){
        JSONFilelocation = JSONFileloc;
    }

    public List<RankingJSONTemplate> readRankingJSONTemplate(){
        List<RankingJSONTemplate> rankedParas = null;
        try {
            Reader reader = Files.newBufferedReader(Paths.get(JSONFilelocation));
            rankedParas = new Gson().fromJson(reader, new TypeToken<List<RankingJSONTemplate>>() {
            }.getType());
            //rankedParas.forEach(System.out::println);
            reader.close();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        return rankedParas;
    }
}
