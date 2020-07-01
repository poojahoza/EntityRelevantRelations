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

        public List<RankingJSONTemplate> convertParaIndexToRankingJSON(Map<String, String> paraIndex){
            List<RankingJSONTemplate> rankingJSONTemplateList = new ArrayList<>() ;
            try {
                for (Map.Entry<String, String> paraid : paraIndex.entrySet()) {
                    RankingJSONTemplate jsonTemplate = new RankingJSONTemplate();
                    jsonTemplate.setContextid(paraid.getKey());
                    jsonTemplate.setContexttext(paraid.getValue());
                    rankingJSONTemplateList.add(jsonTemplate);
                }
            }catch (Exception ioe){
                ioe.printStackTrace();
            }
            return rankingJSONTemplateList;
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
                        jsonTemplate.setWATEntitiesTitle(Arrays.asList(para.getValue().getEntity().getEntityId().split("[\r\n]+")));
                        rankingJSONTemplateList.add(jsonTemplate);
                    }
                }
            }catch (IOException ioe){
                ioe.printStackTrace();
            }
            return rankingJSONTemplateList;
        }

        public List<RankingJSONTemplate> convertBM25RankingToWATEntityJSON(Map<String, Map<String, Container>> BM25Ranking,
                                                                           String WATEntityIndexLoc,
                                                                           Map<String, List<String>> converted_entity_ids){
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
                            wat_entities = new ArrayList<>();
                            List<String> wat_mentions = watEntityIndexSearcher.createWATAnnotations(para.getKey());
                            if(wat_mentions.size()>0){
                                System.out.println(wat_mentions.get(0));
                                String[] temp = wat_mentions.get(0).split("\n");
                                wat_mentions.clear();
                                for(String t:temp){
                                    wat_mentions.add(t.split("_")[0]);
                                }
                                for(String m:wat_mentions){
                                    if(converted_entity_ids.containsKey(m)){
                                        wat_entities.addAll(converted_entity_ids.get(m));
                                    }
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

        public HashSet<String> ConvertRankingJSONtoTitleSet(List<RankingJSONTemplate> rankingJSONTemplate){
            HashSet<String> titleSet = new HashSet<>();
            rankingJSONTemplate.forEach((jsonTemplate)->{
                //System.out.println(jsonTemplate);
                String text = jsonTemplate.getContexttext();
                String[] entities = text.split("\n");
                for(String e:entities){
                    titleSet.add(e.split("_")[0]);
                    if(e.split("_")[0].equals("How's It Going%3F")){
                        System.out.println("How's It going is present in the read json file");
                    }
                }
            });
            System.out.println(titleSet.size());
            return titleSet;
        }

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

        public HashSet<String> ConvertRankingJSONtoWATEntitiesSet(List<RankingJSONTemplate> rankingJSONTemplate){
            HashSet<String> WATEntitiesSet = new HashSet<>();
            rankingJSONTemplate.forEach((jsonTemplate)->{
                //System.out.println(jsonTemplate);
                List<String> Entities = jsonTemplate.getWATEntitiesTitle();
                WATEntitiesSet.addAll(Entities);

            });
            System.out.println(WATEntitiesSet.size());
            return WATEntitiesSet;
        }
    }
}
