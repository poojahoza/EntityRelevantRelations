package main.java.entitylinker.wat;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

public class WATEntityLinkerTrial {

    public static void main(String[] args) throws Exception {
         //String input_text = null;
         String EURL = "https://wat.d4science.org/wat/tag/tag";
         String TOKEN = "1d178c55-a94f-4540-87ee-a935b467f154-843339462";
         String text = "Per the The ICU Book \\\"The first rule of antibiotics is try not to use them, and the second rule is try not to use too many of them.\\\" Inappropriate antibiotic treatment and overuse of antibiotics have contributed to the emergence of antibiotic-resistant bacteria. Self prescription of antibiotics is an example of misuse. Many antibiotics are frequently prescribed to treat symptoms or diseases that do not respond to antibiotics or that are likely to resolve without treatment. Also, incorrect or suboptimal antibiotics are prescribed for certain bacterial infections. The overuse of antibiotics, like penicillin and erythromycin, has been associated with emerging antibiotic resistance since the 1950s. Widespread usage of antibiotics in hospitals has also been associated with increases in bacterial strains and species that no longer respond to treatment with the most common antibiotics.";
        URIBuilder builder = new URIBuilder();
        builder.setScheme("https").setHost("wat.d4science.org").setPath("/wat/tag/tag")
                .setParameter("gcube-token", TOKEN)
                .setParameter("text", text)
                .setParameter("lang", "en")
                .setParameter("tokenizer", "lucene")
                .setParameter("debug", "9")
                .setParameter("method", "spotter:includeUserHint=true:includeNamedEntity=true:includeNounPhrase=true,prior:k=50,filter-valid,centroid:rescore=true,topk:k=5,voting:relatedness=lm,ranker:model=0046.model,confidence:model=pruner-wiki.linear");
        try {
            String message = null;
            URI uri = builder.build();
            //HttpClient client = new HttpClient()
            HttpGet httpGet = new HttpGet(uri);
            httpGet.setHeader("Content-type","application/json");
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse httpResponse = null;
            httpResponse = httpClient.execute(httpGet);
            int StatusCode = httpResponse.getStatusLine().getStatusCode();
            if(StatusCode == 200){
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
                message = bufferedReader.readLine();
            }
            System.out.println(message);
            //JsonReader json = new JsonReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
            //JSONTokener jsonTokener = new JSONTokener(bufferedReader);
            //JSONObject jsonObject = new JSONObject(line);
            //System.out.println(jsonObject);
            //Map<String, String> output_map = new Gson().fromJson(line, new TypeToken<Map<String, String>>(){}.getType());
            //System.out.println("message : "+message);
            //System.out.println("**********"+httpResponse.toString());
//            for (Map.Entry<String, String> iter: output_map.entrySet()){
//                System.out.println("key: "+iter.getKey()+" -- "+iter.getValue());
//            }
        }catch (URISyntaxException uriSyntaxException){
            uriSyntaxException.printStackTrace();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }
}
