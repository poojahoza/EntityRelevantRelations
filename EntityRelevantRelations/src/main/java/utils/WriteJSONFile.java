package main.java.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.java.containers.RankingJSONTemplate;
import main.java.containers.RelationWrapperJSONTemplate;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.lang.Math;

public class WriteJSONFile {

    private void checkFileExistence(String output_file_name)
    {
        File e = new File(output_file_name);
        System.out.println(output_file_name);
        if(e.exists())
        {
            e.delete();
            //System.out.println(output_file_name+" deleted");
        }
    }

    public void writeFile(String output_file_name,
                          List<RankingJSONTemplate> rankingJSON,
                          String folder_name){
        //String result_dir = "JSON";
        File directory = new File(folder_name);
        if (! directory.exists()){
            directory.mkdir();
        }
        Path file = Paths.get(System.getProperty("user.dir")+"/"+folder_name, output_file_name);
        checkFileExistence(System.getProperty("user.dir")+"/"+folder_name+"/"+output_file_name);

        try(Writer writer = new OutputStreamWriter(new FileOutputStream(file.toFile()), StandardCharsets.UTF_8)){
            Gson gson = new GsonBuilder().create();
            gson.toJson(rankingJSON, writer);
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    public void writeRelationTripleFile(String output_file_name,
                          List<RelationWrapperJSONTemplate> rankingJSON,
                          String folder_name){
        //String result_dir = "JSON";
        File directory = new File(folder_name);
        if (! directory.exists()){
            directory.mkdir();
        }
        Path file = Paths.get(System.getProperty("user.dir")+"/"+folder_name, output_file_name);
        checkFileExistence(System.getProperty("user.dir")+"/"+folder_name+"/"+output_file_name);

        try(Writer writer = new OutputStreamWriter(new FileOutputStream(file.toFile()), StandardCharsets.UTF_8)){
            Gson gson = new GsonBuilder().create();
            gson.toJson(rankingJSON, writer);
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    public void writeMultipleFiles(String output_file_name,
                                   List<RankingJSONTemplate> rankingJSON,
                                   String folder_name){
        int total_num_files = (int)Math.ceil((double)rankingJSON.size()/50000);
        List<RankingJSONTemplate> tempJSONList = new ArrayList<>();
        int counter = -1;
        for(int i = 1; i <= total_num_files; i++) {
            tempJSONList.clear();
            for (int y = (counter + 1); y < (50000 * i); y++) {
                counter++;
                if(rankingJSON.size() >= y) {
                    try {
                        tempJSONList.add(rankingJSON.get(y));
                    }catch (IndexOutOfBoundsException outOfBoundsException){
                        System.out.println(String.valueOf(y)+"----"+rankingJSON.size());
                    }
                }
            }
            if(tempJSONList.size()>0) {
                writeFile(output_file_name + "_" + String.valueOf(i) + ".json",
                        tempJSONList,
                        folder_name);
            }
        }
    }
}
