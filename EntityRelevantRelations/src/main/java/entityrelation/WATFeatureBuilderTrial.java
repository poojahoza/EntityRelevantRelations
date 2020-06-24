package main.java.entityrelation;

import java.util.*;

public class WATFeatureBuilderTrial {

    private Map<String, Double[]> calcEntityScore(HashMap<String, Map<String, Double[]>> entitypairs,
                                                  int length){
        Map<String, Double[]> entityscores = new HashMap<>();
        for(Map.Entry<String, Map<String, Double[]>> entities: entitypairs.entrySet()){
            double score = 0.0;
            double count_score = 0.0;
            for(Map.Entry<String, Double[]> pairs: entities.getValue().entrySet()){
                score += (double)pairs.getValue()[0];
                count_score += (double)pairs.getValue()[1];
            }
            entityscores.put(entities.getKey(), new Double[]{score, count_score});
        }
        return entityscores;
    }


    private HashMap<String, Map<String, Double[]>> buildFeatures(Map<String, Map<Integer, List<String>>> paraRanking,
                                                ArrayList<String> entities_list) {
        HashMap<String, Map<String, Double[]>> entitypairs = new HashMap<>();
        for (int i = 0; i < entities_list.size(); i++) {
            for (int j = 0; j < entities_list.size(); j++) {
                if (i != j) {
                    for (Map.Entry<String, Map<Integer, List<String>>> para : paraRanking.entrySet()) {
                        for (Map.Entry<Integer, List<String>> entities : para.getValue().entrySet()) {
                            if (entities.getValue().contains(entities_list.get(i)) && entities.getValue().contains(entities_list.get(j))) {
                                if (entitypairs.containsKey(entities_list.get(i))) {
                                    Map<String, Double[]> objectentities = entitypairs.get(entities_list.get(i));
                                    double rel = (1 / entities.getKey());
                                    double count = 1;
                                    if (objectentities.containsKey(entities_list.get(j))) {
                                        rel += objectentities.get(entities_list.get(j))[0];
                                        count += objectentities.get(entities_list.get(j))[1];
                                    }
                                    objectentities.put(entities_list.get(j), new Double[]{rel, count});
                                } else {
                                    Map<String, Double[]> temp = new HashMap<>();
                                    temp.put(entities_list.get(j), new Double[]{(double) (1 / entities.getKey()), 1.0});
                                    entitypairs.put(entities_list.get(i), temp);
                                }
                            }
                        }
                    }
                }
            }
        }
        return entitypairs;
    }

    private Map<String, Double[]> iterateParas(Map<String, Map<Integer, List<String>>> paraRanking){
        HashMap<String, Map<String, Double[]>> entitypairs = new HashMap<>();
        HashSet<String> entities_list = new HashSet<>();
        for(Map.Entry<String, Map<Integer, List<String>>> para: paraRanking.entrySet()) {
            for (Map.Entry<Integer, List<String>> entities : para.getValue().entrySet()) {
                List<String> ent_list = entities.getValue();
                for (int i = 0; i < ent_list.size(); i++) {
                    entities_list.add(ent_list.get(i));
                }
            }
        }
        ArrayList<String> para_entities = new ArrayList<>(entities_list);
        entitypairs = buildFeatures(paraRanking, para_entities);
        return calcEntityScore(entitypairs, entities_list.size());
    }


    private Map<String, Map<String, Double[]>> iterateQueries(Map<String, Map<String, Map<Integer, List<String>>>> BM25Ranking){
        System.out.println("Total queries : "+BM25Ranking.size());
        Map<String, Map<String, Double[]>> query_entities = new HashMap<>();
        for(Map.Entry<String, Map<String, Map<Integer, List<String>>>> query: BM25Ranking.entrySet()){
            System.out.println("processing query : "+query.getKey());
            Map<String, Double[]> entities = iterateParas(query.getValue());
            query_entities.put(query.getKey(), entities);
        }
        return query_entities;
    }

    public Map<String, Map<String, Double[]>> getFeatures(Map<String, Map<String, Map<Integer, List<String>>>> BM25Ranking){
        Map<String, Map<String, Double[]>> query_entities = iterateQueries(BM25Ranking);
        return query_entities;
    }
}
