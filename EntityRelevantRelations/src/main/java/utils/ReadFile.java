package main.java.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import main.java.containers.Container;
import main.java.containers.EntityContainer;
import main.java.containers.RankingJSONTemplate;
import main.java.searcher.DocumentEntityIndexSearcher;
import main.java.searcher.WATEntityIndexSearcher;
import org.apache.lucene.document.Document;

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

    public Map<String, Map<String, Container>> convertParaAggrCsvtoBM25Ranking(List<String> csv_data, String entityLinkerLoc){
        Map<String, Map<String, Container>> final_map = new LinkedHashMap<>();
        try {
            DocumentEntityIndexSearcher documentEntityIndexSearcher = new DocumentEntityIndexSearcher(entityLinkerLoc);
            Map<String, Document> para_details = new LinkedHashMap<>();

            for (String s : csv_data) {
                String[] splited_text = s.split("\t");

                Document document = null;
                if (para_details.containsKey(splited_text[1])) {
                    document = para_details.get(splited_text[1]);
                } else {
                    List<Document> wat_mentions = documentEntityIndexSearcher.getLuceneDocuments(splited_text[1]);
                    if (wat_mentions.size() > 0) {
                        if (wat_mentions.get(0) != null) {
                            document = wat_mentions.get(0);
                        }
                    }
                }
                if(document != null) {
                    Container c = new Container(Double.parseDouble(splited_text[3]));
                    c.setRank(Integer.parseInt(splited_text[2]));
                    c.setScoreVal(Double.parseDouble(splited_text[3]));
                    c.setText(document.getField("Text").stringValue());
                    c.addEntityContainer(new EntityContainer(document.getField("EntityLinks").stringValue(), document.getField("OutlinkIds").stringValue()));

                    para_details.put(splited_text[1], document);

                    String outer_key = splited_text[0];
                    String inner_key = splited_text[1];

                    if (final_map.containsKey(outer_key)) {
                        Map<String, Container> extract = final_map.get(outer_key);
                        extract.put(inner_key, c);
                    } else {
                        Map<String, Container> temp = new LinkedHashMap<>();
                        temp.put(inner_key, c);
                        final_map.put(outer_key, temp);
                    }
                }
            }
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
        return final_map;
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
