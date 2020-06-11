package main.java.entitylinker.wat;

import org.json.JSONException;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import main.java.containers.EntityAnnotation;

/**
 * @author poojaoza
 **/

public class WATEntityLinker {
    private String input_text = null;
    private final String EURL = "https://wat.d4science.org/wat/tag/tag";
    private final String TOKEN = "1d178c55-a94f-4540-87ee-a935b467f154-843339462";
    private LinkedHashMap<String, EntityAnnotation> entity_details = new LinkedHashMap();

    public WATEntityLinker(String text){
        input_text = text;
    }

    public void annotateText(){
        setData(input_text);
    }

    public LinkedHashMap getEntitiesDetails(){
        return entity_details;
    }

    private void setEntitiesDetails(String title,
                                    int id,
                                    int start,
                                    int end,
                                    double rho,
                                    String spot){
        entity_details.put(String.valueOf(id),
                            new EntityAnnotation(title, id, start, end, rho, spot));
    }

    private void setData(String text){
        try {
            Document doc = getData(text);
            if (doc != null) {
                if (doc.text() != null) {
                    JSONObject jsonObject = new JSONObject(doc.text());
                    if (jsonObject.has("annotations")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("annotations");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonAnnotations = jsonArray.getJSONObject(i);
                            setEntitiesDetails(jsonAnnotations.has("title") ? jsonAnnotations.getString("title") : "",
                                    jsonAnnotations.has("id") ? jsonAnnotations.getInt("id") : 0,
                                    jsonAnnotations.has("start") ? jsonAnnotations.getInt("start") : 0,
                                    jsonAnnotations.has("end") ? jsonAnnotations.getInt("end") : 0,
                                    jsonAnnotations.has("rho") ? jsonAnnotations.getDouble("rho") : 0,
                                    jsonAnnotations.has("spot") ? jsonAnnotations.getString("spot") : "");
                        }
                    }
                }
            }
        }catch (JSONException jsonex){
            jsonex.printStackTrace();
        }
    }

    private Document getData(String text){
        Document doc;
        try {
            doc = Jsoup.connect(EURL)
                    .data("gcube-token", TOKEN)
                    .data("text", text)
                    .data("lang", "en")
                    .data("tokenizer", "lucene")
                    .data("debug", "9")
                    .data("method", "spotter:includeUserHint=true:includeNamedEntity=true:includeNounPhrase=true,prior:k=50,filter-valid,centroid:rescore=true,topk:k=5,voting:relatedness=lm,ranker:model=0046.model,confidence:model=pruner-wiki.linear")
                    .timeout(50 * 1000)
                    .ignoreContentType(true)
                    .get();
        }catch (IOException ioe){
            ioe.printStackTrace();
            return null;
        }
        return doc;

    }
}
