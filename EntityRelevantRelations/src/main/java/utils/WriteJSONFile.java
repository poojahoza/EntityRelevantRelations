package main.java.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.java.containers.RankingJSONTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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

    public void writeFile(String output_file_name, List<RankingJSONTemplate> rankingJSON){
        String result_dir = "JSON";
        File directory = new File(result_dir);
        if (! directory.exists()){
            directory.mkdir();
        }
        Path file = Paths.get(System.getProperty("user.dir")+"/"+result_dir, output_file_name);
        checkFileExistence(System.getProperty("user.dir")+"/"+result_dir+"/"+output_file_name);

        try(Writer writer = new FileWriter(file.toFile())){
            Gson gson = new GsonBuilder().create();
            gson.toJson(rankingJSON, writer);
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }
}
