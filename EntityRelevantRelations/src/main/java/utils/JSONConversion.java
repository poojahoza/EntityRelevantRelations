package main.java.utils;

import main.java.containers.Container;
import main.java.containers.RankingJSONTemplate;
import main.java.containers.RelationWrapperJSONTemplate;
import main.java.relationextractor.stanfordcorenlp.StanfordRelationExtractor;
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

        public List<RankingJSONTemplate> convertParaAggrBM25RankingToRankingJSON(Map<String, Map<String, Container>> BM25Ranking){
            List<RankingJSONTemplate> rankingJSONTemplateList = new ArrayList<>() ;
                for (Map.Entry<String, Map<String, Container>> queryid : BM25Ranking.entrySet()) {
                    for (Map.Entry<String, Container> para : queryid.getValue().entrySet()) {
                        RankingJSONTemplate jsonTemplate = new RankingJSONTemplate();
                        jsonTemplate.setQueryid(queryid.getKey());
                        jsonTemplate.setContextid(para.getKey());
                        jsonTemplate.setContexttext(para.getValue().getText());
                        jsonTemplate.setContextrank(String.valueOf(para.getValue().getRank()));
                        jsonTemplate.setContextscore(String.valueOf(para.getValue().getScore()));
                        rankingJSONTemplateList.add(jsonTemplate);
                    }
                }
            return rankingJSONTemplateList;
        }

        public List<RankingJSONTemplate> convertParaAggrBM25RankingToWikiEntityJSON(Map<String, Map<String, Container>> BM25Ranking){
            List<RankingJSONTemplate> rankingJSONTemplateList = new ArrayList<>() ;
            for (Map.Entry<String, Map<String, Container>> queryid : BM25Ranking.entrySet()) {
                for (Map.Entry<String, Container> para : queryid.getValue().entrySet()) {
                    RankingJSONTemplate jsonTemplate = new RankingJSONTemplate();
                    jsonTemplate.setQueryid(queryid.getKey());
                    jsonTemplate.setContextid(para.getKey());
                    jsonTemplate.setContexttext(para.getValue().getText());
                    jsonTemplate.setContextrank(String.valueOf(para.getValue().getRank()));
                    jsonTemplate.setContextscore(String.valueOf(para.getValue().getScore()));
                    jsonTemplate.setWATEntitiesTitle(Arrays.asList(para.getValue().getEntity().getEntityId().split("[\r\n]+")));
                    rankingJSONTemplateList.add(jsonTemplate);
                }
            }
            return rankingJSONTemplateList;
        }

        public List<RankingJSONTemplate> convertParaAggrBM25RankingToWATEntityJSON(Map<String, Map<String, Container>> BM25Ranking,
                                                                           String WATEntityIndexLoc,
                                                                           Map<String, List<String>> converted_entity_ids){
            List<RankingJSONTemplate> rankingJSONTemplateList = new ArrayList<>() ;
            Map<String, List<String>> para_entities = new LinkedHashMap<>();
            try {
                WATEntityIndexSearcher watEntityIndexSearcher = new WATEntityIndexSearcher(WATEntityIndexLoc, "EntityLinks");
                for (Map.Entry<String, Map<String, Container>> queryid : BM25Ranking.entrySet()) {
                    for (Map.Entry<String, Container> para : queryid.getValue().entrySet()) {
                        List<String> wat_entities;
                        RankingJSONTemplate jsonTemplate = new RankingJSONTemplate();
                        jsonTemplate.setQueryid(queryid.getKey());
                        jsonTemplate.setContextid(para.getKey());
                        jsonTemplate.setContexttext(para.getValue().getText());
                        jsonTemplate.setContextrank(String.valueOf(para.getValue().getRank()));
                        jsonTemplate.setContextscore(String.valueOf(para.getValue().getScore()));
                        if(para_entities.containsKey(para.getKey())){
                            wat_entities = para_entities.get(para.getKey());
                        }else{
                            wat_entities = new ArrayList<>();
                            List<String> wat_mentions = watEntityIndexSearcher.createWATAnnotations(para.getKey());
                            if(wat_mentions.size()>0){
                                //System.out.println(wat_mentions.get(0));
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
                WATEntityIndexSearcher watEntityIndexSearcher = new WATEntityIndexSearcher(WATEntityIndexLoc, "EntityLinks");
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
                                //System.out.println(wat_mentions.get(0));
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

        public Map<String, Map<String, String>> ConvertRankingJSONtoContextText(List<RankingJSONTemplate> rankingJSONTemplate){
            Map<String, Map<String, String>> BM25ContextText = new LinkedHashMap<>();
            rankingJSONTemplate.forEach((jsonTemplate)->{
                //System.out.println(jsonTemplate);
                if(BM25ContextText.containsKey(jsonTemplate.getQueryid())){
                    Map<String, String> context_details = BM25ContextText.get(jsonTemplate.getQueryid());
                    context_details.put(jsonTemplate.getContextid(), jsonTemplate.getContexttext());
                }else{
                    Map<String, String> context_details = new LinkedHashMap<>();
                    context_details.put(jsonTemplate.getContextid(), jsonTemplate.getContexttext());
                    BM25ContextText.put(jsonTemplate.getQueryid(), context_details);
                }

            });
            return BM25ContextText;
        }

        public List<RelationWrapperJSONTemplate> ConvertRankingJSONtoRelationTriplesWrapper(List<RankingJSONTemplate> rankingJSONTemplateList,
                                                                                            String coref_flag){
            List<RelationWrapperJSONTemplate> relationWrapperJSONTemplateList = new ArrayList<>();
            StanfordRelationExtractor stanfordRelationExtractor = new StanfordRelationExtractor(coref_flag);
            rankingJSONTemplateList.forEach((jsonTemplate)->{
                RelationWrapperJSONTemplate relationWrapperJSONTemplate = new RelationWrapperJSONTemplate();
                relationWrapperJSONTemplate.setQueryid(jsonTemplate.getQueryid());
                relationWrapperJSONTemplate.setContextid(jsonTemplate.getContextid());
                relationWrapperJSONTemplate.setContexttext(jsonTemplate.getContexttext());
                relationWrapperJSONTemplate.setContextrank(jsonTemplate.getContextrank());
                relationWrapperJSONTemplate.setContextscore(jsonTemplate.getContextscore());
                relationWrapperJSONTemplate.setSentences(stanfordRelationExtractor.fetchRelationTriples(jsonTemplate.getContexttext()));
                relationWrapperJSONTemplateList.add(relationWrapperJSONTemplate);
                System.out.println(relationWrapperJSONTemplateList.size());
            });
            return relationWrapperJSONTemplateList;
        }
    }
}
