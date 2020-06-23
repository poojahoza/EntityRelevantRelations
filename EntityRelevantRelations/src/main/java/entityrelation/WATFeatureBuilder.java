package main.java.entityrelation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class WATFeatureBuilder {

    private Map<String, Double> calcEntityScore(HashMap<String, Map<String, Double>> entitypairs,
                                 int length){
        Map<String, Double> entityscores = new HashMap<>();
        for(Map.Entry<String, Map<String, Double>> entities: entitypairs.entrySet()){
            double score = 0.0;
            for(Map.Entry<String, Double> pairs: entities.getValue().entrySet()){
                score += (double)pairs.getValue();
            }
            entityscores.put(entities.getKey(), score/length);
        }
        return entityscores;
    }

    private Map<String, Double> iterateParas(Map<String, Map<Integer, List<String>>> paraRanking){
        HashMap<String, Map<String, Double>> entitypairs = new HashMap<>();
        HashSet<String> entities_list = new HashSet<>();
        for(Map.Entry<String, Map<Integer, List<String>>> para: paraRanking.entrySet()){
            for(Map.Entry<Integer, List<String>> entities: para.getValue().entrySet()){
                List<String> ent_list = entities.getValue();
                for(int i = 0; i <= ent_list.size()-2; i++){
                    entities_list.add(ent_list.get(i));
                    for(int j = i+1; j <= ent_list.size()-1; j++){
                        entities_list.add(ent_list.get(j));
                        if(entitypairs.containsKey(ent_list.get(i))){
                            Map<String, Double> temp = entitypairs.get(ent_list.get(i));
                            double val = (1/entities.getKey());
                            try {
                                if (temp.containsKey(ent_list.get(j))) {
                                    val += temp.get(ent_list.get(j));
                                }
                                temp.put(ent_list.get(j), val);
                            }catch(IndexOutOfBoundsException ioe){
                                System.out.println("j : "+j);
                            }
                        }else{
                            Map<String, Double> newentitypair = new HashMap<>();
                            newentitypair.put(ent_list.get(j), (double)(1/entities.getKey()));
                            entitypairs.put(ent_list.get(i), newentitypair);
                        }

                        if(entitypairs.containsKey(ent_list.get(j))){
                            Map<String, Double> temp = entitypairs.get(ent_list.get(j));
                            double val = (1/entities.getKey());
                            if(temp.containsKey(ent_list.get(i))){
                                val += temp.get(ent_list.get(i));
                            }
                            temp.put(ent_list.get(i), val);
                        }else{
                            Map<String, Double> newentitypair = new HashMap<>();
                            newentitypair.put(ent_list.get(i), (double)(1/entities.getKey()));
                            entitypairs.put(ent_list.get(j), newentitypair);
                        }
                    }
                }
            }
        }
        System.out.println("query entities : "+entitypairs.size());
        Map<String, Double> entities = calcEntityScore(entitypairs, entities_list.size());
        return entities;
    }


    private Map<String, Map<String, Double>> iterateQueries(Map<String, Map<String, Map<Integer, List<String>>>> BM25Ranking){
        System.out.println("Total queries : "+BM25Ranking.size());
        Map<String, Map<String, Double>> query_entities = new HashMap<>();
        for(Map.Entry<String, Map<String, Map<Integer, List<String>>>> query: BM25Ranking.entrySet()){
            System.out.println("processing query : "+query.getKey());
            Map<String, Double> entities = iterateParas(query.getValue());
            query_entities.put(query.getKey(), entities);
        }
        return query_entities;
    }

    public Map<String, Map<String, Double>> getFeatures(Map<String, Map<String, Map<Integer, List<String>>>> BM25Ranking){
        Map<String, Map<String, Double>> query_entities = iterateQueries(BM25Ranking);
        return query_entities;
    }
}
