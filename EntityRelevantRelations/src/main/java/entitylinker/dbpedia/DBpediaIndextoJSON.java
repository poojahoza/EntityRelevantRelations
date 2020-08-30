package main.java.entitylinker.dbpedia;

/**
 * @author poojaoza
 **/

import main.java.containers.EntityLinkerAnnotationsJSONTemplate;
import main.java.containers.EntityLinkerAnnotationsWrapperJSONTemplate;
import main.java.containers.RankingJSONTemplate;
import main.java.searcher.WATEntityIndexSearcher;
import main.java.utils.ReadJSONFile;
import main.java.utils.WriteJSONFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DBpediaIndextoJSON {

    WATEntityIndexSearcher watEntityIndexSearcher;

    private List<EntityLinkerAnnotationsJSONTemplate> getAnnotations(String paraid){
        List<EntityLinkerAnnotationsJSONTemplate> entityLinkerAnnotationsJSONTemplateList = new ArrayList<>();
        List<String> annotations = watEntityIndexSearcher.createWATAnnotations(paraid);
        System.out.println(paraid+" "+String.valueOf(annotations.size()));
        if(annotations.size()>0) {
            if (annotations.get(0) != null) {
                String[] splitted_text = annotations.get(0).split("\n");
                for (int i = 0; i < splitted_text.length; i++) {
                    EntityLinkerAnnotationsJSONTemplate entityLinkerAnnotationsJSONTemplate = new EntityLinkerAnnotationsJSONTemplate();
                    String[] inner_splitted_text = splitted_text[i].split("\t");
                    entityLinkerAnnotationsJSONTemplate.setSpot(inner_splitted_text[0]);
                    entityLinkerAnnotationsJSONTemplate.setStart(Integer.parseInt(inner_splitted_text[1]));
                    entityLinkerAnnotationsJSONTemplate.setEnd(Integer.parseInt(inner_splitted_text[2]));
                    entityLinkerAnnotationsJSONTemplate.setRho(Double.parseDouble(inner_splitted_text[3]));
                    entityLinkerAnnotationsJSONTemplate.setWiki_title(inner_splitted_text[4]);
                    entityLinkerAnnotationsJSONTemplate.setWiki_id(Integer.parseInt("0"));
                    entityLinkerAnnotationsJSONTemplateList.add(entityLinkerAnnotationsJSONTemplate);
                }
            }
        }
        return entityLinkerAnnotationsJSONTemplateList;
    }

    private void fetchDBpediaAnnotationsFromIndex(String index, String inputFileLoc, String outputFileLoc){

        try {
            ReadJSONFile readJSONFile = new ReadJSONFile();
            List<RankingJSONTemplate> rankingJSONTemplateList = readJSONFile.readRankingJSONTemplate(inputFileLoc);
            watEntityIndexSearcher = new WATEntityIndexSearcher(index, "EntityLinks");

            List<EntityLinkerAnnotationsWrapperJSONTemplate> entityLinkerAnnotationsWrapperJSONTemplates = new ArrayList<>();
            rankingJSONTemplateList.forEach((jsonTemplate) -> {
                EntityLinkerAnnotationsWrapperJSONTemplate entityLinkerAnnotationsWrapperJSONTemplate = new EntityLinkerAnnotationsWrapperJSONTemplate();
                entityLinkerAnnotationsWrapperJSONTemplate.setQueryid(jsonTemplate.getQueryid());
                entityLinkerAnnotationsWrapperJSONTemplate.setContextid(jsonTemplate.getContextid());
                entityLinkerAnnotationsWrapperJSONTemplate.setContexttext(jsonTemplate.getContexttext());
                entityLinkerAnnotationsWrapperJSONTemplate.setContextrank(jsonTemplate.getContextrank());
                entityLinkerAnnotationsWrapperJSONTemplate.setContextscore(jsonTemplate.getContextscore());
                entityLinkerAnnotationsWrapperJSONTemplate.setWATAnnotations(getAnnotations(jsonTemplate.getContextid()));
                entityLinkerAnnotationsWrapperJSONTemplates.add(entityLinkerAnnotationsWrapperJSONTemplate);
            });

            WriteJSONFile writeJSONFile = new WriteJSONFile();
            writeJSONFile.writeDBpediaAnnotationsFile(outputFileLoc, entityLinkerAnnotationsWrapperJSONTemplates);

        }catch (IOException ioe){
            ioe.printStackTrace();
        }

    }

    public void getDBpediaAnnotations(String dbpediaIndex, String inputFileLoc, String outputFileLoc){
        fetchDBpediaAnnotationsFromIndex(dbpediaIndex, inputFileLoc, outputFileLoc);
    }

}
