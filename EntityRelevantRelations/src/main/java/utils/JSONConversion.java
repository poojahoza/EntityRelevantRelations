package main.java.utils;

import main.java.containers.Container;
import main.java.containers.RankingJSONTemplate;
import main.java.searcher.BaseSearcher;
import main.java.searcher.WATEntityIndexSearcher;
import org.apache.lucene.document.Document;

import java.io.IOException;
import java.util.*;

public class JSONConversion {

    public static class BM25RankingConversion extends BaseSearcher{

        public BM25RankingConversion(String indexLoc) throws IOException {
            super(indexLoc);
        }

        public List<RankingJSONTemplate> convertBM25RankingToRankingJSON(Map<String, Map<String, Container>> BM25Ranking){
            List<RankingJSONTemplate> rankingJSONTemplateList = new ArrayList<>() ;
            try {
                for (Map.Entry<String, Map<String, Container>> queryid : BM25Ranking.entrySet()) {
                    for (Map.Entry<String, Container> para : queryid.getValue().entrySet()) {
                        RankingJSONTemplate jsonTemplate = new RankingJSONTemplate();
                        jsonTemplate.setQueryid(queryid.getKey());
                        jsonTemplate.setContextid(para.getKey());
                        Document doc = searcher.doc(para.getValue().getDocID());
                        jsonTemplate.setContexttext(doc.get("Text"));
                        jsonTemplate.setContextrank(String.valueOf(para.getValue().getRank()));
                        jsonTemplate.setContextscore(String.valueOf(para.getValue().getScore()));
                        rankingJSONTemplateList.add(jsonTemplate);
                    }
                }
            }catch (IOException ioe){
                ioe.printStackTrace();
            }
            return rankingJSONTemplateList;
        }

        public List<RankingJSONTemplate> convertBM25RankingToWikiEntityJSON(Map<String, Map<String, Container>> BM25Ranking){
            List<RankingJSONTemplate> rankingJSONTemplateList = new ArrayList<>() ;
            try {
                for (Map.Entry<String, Map<String, Container>> queryid : BM25Ranking.entrySet()) {
                    for (Map.Entry<String, Container> para : queryid.getValue().entrySet()) {
                        RankingJSONTemplate jsonTemplate = new RankingJSONTemplate();
                        jsonTemplate.setQueryid(queryid.getKey());
                        jsonTemplate.setContextid(para.getKey());
                        Document doc = searcher.doc(para.getValue().getDocID());
                        jsonTemplate.setContexttext(doc.get("Text"));
                        jsonTemplate.setContextrank(String.valueOf(para.getValue().getRank()));
                        jsonTemplate.setContextscore(String.valueOf(para.getValue().getScore()));
                        jsonTemplate.setWikiEntitiesId(Arrays.asList(para.getValue().getEntity().getEntityId().split("[\r\n]+")));
                        rankingJSONTemplateList.add(jsonTemplate);
                    }
                }
            }catch (IOException ioe){
                ioe.printStackTrace();
            }
            return rankingJSONTemplateList;
        }

        public List<RankingJSONTemplate> convertBM25RankingToWATEntityJSON(Map<String, Map<String, Container>> BM25Ranking,
                                                                           String WATEntityIndexLoc){
            List<RankingJSONTemplate> rankingJSONTemplateList = new ArrayList<>() ;
            Map<String, List<String>> para_entities = new LinkedHashMap<>();
            try {
                WATEntityIndexSearcher watEntityIndexSearcher = new WATEntityIndexSearcher(WATEntityIndexLoc);
                for (Map.Entry<String, Map<String, Container>> queryid : BM25Ranking.entrySet()) {
                    for (Map.Entry<String, Container> para : queryid.getValue().entrySet()) {
                        List<String> wat_entities;
                        RankingJSONTemplate jsonTemplate = new RankingJSONTemplate();
                        jsonTemplate.setQueryid(queryid.getKey());
                        jsonTemplate.setContextid(para.getKey());
                        Document doc = searcher.doc(para.getValue().getDocID());
                        jsonTemplate.setContexttext(doc.get("Text"));
                        jsonTemplate.setContextrank(String.valueOf(para.getValue().getRank()));
                        jsonTemplate.setContextscore(String.valueOf(para.getValue().getScore()));
                        if(para_entities.containsKey(para.getKey())){
                            wat_entities = para_entities.get(para.getKey());
                        }else{
                            wat_entities = watEntityIndexSearcher.createWATAnnotations(para.getKey());
                            if(wat_entities.size()>0){
                                System.out.println(wat_entities.get(0));
                                String[] temp = wat_entities.get(0).split("\n");
                                wat_entities.clear();
                                for(String t:temp){
                                    wat_entities.add(t.split("_")[0]);
                                }
                            }
                            para_entities.put(para.getKey(), wat_entities);
                        }
                        jsonTemplate.setWATEntitiesTitle(wat_entities);
                        rankingJSONTemplateList.add(jsonTemplate);
                    }
                }
            }catch (IOException ioe){
                ioe.printStackTrace();
            }
            return rankingJSONTemplateList;
        }
    }

    public static class RankingJSONTemplateConversion{

        public Map<String, Map<String, Map<Integer, List<String>>>> ConvertRankingJSONtoMap(List<RankingJSONTemplate> rankingJSONTemplate){
            Map<String, Map<String, Map<Integer, List<String>>>> convertedMap = new HashMap<>();
            rankingJSONTemplate.forEach((jsonTemplate)->{
                //System.out.println(jsonTemplate);
                if(convertedMap.containsKey(jsonTemplate.getQueryid())){
                    Map<Integer, List<String>> temp = new HashMap<>();
                    temp.put(Integer.valueOf(jsonTemplate.getContextrank()), jsonTemplate.getWATEntitiesTitle());
                    Map<String, Map<Integer, List<String>>> para_temp = convertedMap.get(jsonTemplate.getQueryid());
                    para_temp.put(jsonTemplate.getContextid(), temp);
                }else{
                    Map<Integer, List<String>> temp = new HashMap<>();
                    temp.put(Integer.valueOf(jsonTemplate.getContextrank()), jsonTemplate.getWATEntitiesTitle());
                    Map<String, Map<Integer, List<String>>> para_temp = new HashMap<>();
                    para_temp.put(jsonTemplate.getContextid(), temp);
                    convertedMap.put(jsonTemplate.getQueryid(),para_temp);
                }
            });
            System.out.println(convertedMap.size());
            return convertedMap;
        }
    }
}
