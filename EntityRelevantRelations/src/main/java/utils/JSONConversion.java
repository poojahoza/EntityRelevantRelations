package main.java.utils;

import main.java.containers.Container;
import main.java.containers.RankingJSONTemplate;
import main.java.searcher.BaseSearcher;
import org.apache.lucene.document.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

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
    }



}
