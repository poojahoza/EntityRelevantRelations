package main.java.entitylinker.wat;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.json.JSONArray;
import org.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.client.utils.URIBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;
import main.java.containers.EntityAnnotation;

import javax.print.URIException;

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
            String message = getData1(text);
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

    private String getData1(String text){
        String message = null;
        URIBuilder builder = new URIBuilder();
        builder.setScheme("https").setHost("wat.d4science.org").setPath("/wat/tag/tag")
                .setParameter("gcube-token", TOKEN)
                .setParameter("text", text)
                .setParameter("lang", "en")
                .setParameter("tokenizer", "lucene")
                .setParameter("debug", "9")
                .setParameter("method", "spotter:includeUserHint=true:includeNamedEntity=true:includeNounPhrase=true,prior:k=50,filter-valid,centroid:rescore=true,topk:k=5,voting:relatedness=lm,ranker:model=0046.model,confidence:model=pruner-wiki.linear");
        try {
            URI uri = builder.build();
            //HttpClient client = new HttpClient()
            HttpGet httpGet = new HttpGet(uri);
            httpGet.setHeader("Content-type","application/json");
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse httpResponse = null;
            httpResponse = httpClient.execute(httpGet);
            int StatusCode = httpResponse.getStatusLine().getStatusCode();
            message = httpResponse.getStatusLine().getReasonPhrase();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
            String line = bufferedReader.readLine();
            //JSONObject json = (JSONObject) new JSONParser().parse(line);
            Map<String, String> output_map = new Gson().fromJson(line, new TypeToken<Map<String, String>>(){}.getType());
            //System.out.println("message : "+message);
            //System.out.println("**********"+httpResponse.toString());
            for (Map.Entry<String, String> iter: output_map.entrySet()){
                System.out.println("key: "+iter.getKey()+" -- "+iter.getValue());
            }
        }catch (URISyntaxException uriSyntaxException){
            uriSyntaxException.printStackTrace();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
        return message;
    }
}

