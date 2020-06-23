package main.java.entityrelation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WATBM25FeatureBuilder {

    private Map<String, Integer> iterateParas(Map<String, Map<Integer, List<String>>> paraRanking){
        HashMap<String, Integer> entityfreq = new HashMap<>();
        for(Map.Entry<String, Map<Integer, List<String>>> para: paraRanking.entrySet()){
            for(Map.Entry<Integer, List<String>> entities: para.getValue().entrySet()){
                List<String> ent_list = entities.getValue();
                for(int i = 0; i < ent_list.size(); i++){
                    if(entityfreq.containsKey(ent_list.get(i))){
                        entityfreq.put(ent_list.get(i), entityfreq.get(ent_list.get(i))+1);
                    }else{
                        entityfreq.put(ent_list.get(i), 1);
                    }
                }
            }
        }
        System.out.println("query entities : "+entityfreq.size());
        return entityfreq;
    }


    private Map<String, Map<String, Integer>> iterateQueries(Map<String, Map<String, Map<Integer, List<String>>>> BM25Ranking){
        System.out.println("Total queries : "+BM25Ranking.size());
        Map<String, Map<String, Integer>> query_entities = new HashMap<>();
        for(Map.Entry<String, Map<String, Map<Integer, List<String>>>> query: BM25Ranking.entrySet()){
            System.out.println("processing query : "+query.getKey());
            Map<String, Integer> entities = iterateParas(query.getValue());
            query_entities.put(query.getKey(), entities);
        }
        return query_entities;
    }

    public Map<String, Map<String, Integer>> getFeatures(Map<String, Map<String, Map<Integer, List<String>>>> BM25Ranking){
        Map<String, Map<String, Integer>> query_entities = iterateQueries(BM25Ranking);
        return query_entities;
    }
}
