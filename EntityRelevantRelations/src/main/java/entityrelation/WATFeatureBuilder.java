package main.java.entityrelation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WATFeatureBuilder {

    private void iterateParas(Map<String, Map<Integer, List<String>>> paraRanking){
        HashMap<String, Map<String, Double>> entityCoOcc = new HashMap<>();
        for(Map.Entry<String, Map<Integer, List<String>>> para: paraRanking.entrySet()){
            for(Map.Entry<Integer, List<String>> entities: para.getValue().entrySet()){
                List<String> ent_list = entities.getValue();
                for(int i = 0; i <= ent_list.size()-2; i++){
                    for(int j = i+1; j <= ent_list.size()-1; j++){

                        if(entityCoOcc.containsKey(ent_list.get(i))){
                            Map<String, Double> temp = entityCoOcc.get(ent_list.get(i));
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
                            entityCoOcc.put(ent_list.get(i), newentitypair);
                        }

                        if(entityCoOcc.containsKey(ent_list.get(j))){
                            Map<String, Double> temp = entityCoOcc.get(ent_list.get(j));
                            double val = (1/entities.getKey());
                            if(temp.containsKey(ent_list.get(i))){
                                val += temp.get(ent_list.get(i));
                            }
                            temp.put(ent_list.get(i), val);
                        }else{
                            Map<String, Double> newentitypair = new HashMap<>();
                            newentitypair.put(ent_list.get(i), (double)(1/entities.getKey()));
                            entityCoOcc.put(ent_list.get(j), newentitypair);
                        }
                    }
                }
            }
        }
        System.out.println("query entities : "+entityCoOcc.size());
    }


    private void iterateQueries(Map<String, Map<String, Map<Integer, List<String>>>> BM25Ranking){
        System.out.println("Total queries : "+BM25Ranking.size());
        for(Map.Entry<String, Map<String, Map<Integer, List<String>>>> query: BM25Ranking.entrySet()){
            System.out.println("processing query : "+query.getKey());
            iterateParas(query.getValue());
        }

    }

    public void getFeatures(Map<String, Map<String, Map<Integer, List<String>>>> BM25Ranking){
        iterateQueries(BM25Ranking);
    }
}
