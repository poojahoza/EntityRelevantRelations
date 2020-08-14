package main.java.searcher;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DocumentEntityIndexSearcher extends BaseSearcher  {

    //String field = "EntityLinks";

    public DocumentEntityIndexSearcher(String indexLoc) throws IOException {

        super(indexLoc);
        searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(indexLoc))));
        parser = new QueryParser("Id", new EnglishAnalyzer());
        //field = searchField;
    }

    @Override
    public TopDocs performSearch(String paraid, int n)
            throws IOException, ParseException, NullPointerException {

        queryObj = parser.parse(QueryParser.escape(paraid));
        return searcher.search(queryObj, n);
    }

    protected List<Document> getDocuments(ScoreDoc[] scoreDocs, String paraid)
            throws IOException {
        List<Document> rankings = new ArrayList<Document>();
        for(int ind=0; ind<scoreDocs.length; ind++){

            //Get the scoring document
            ScoreDoc scoringDoc = scoreDocs[ind];

            //Create the rank document from searcher
            Document rankedDoc = searcher.doc(scoringDoc.doc);

            //Print out the results from the rank document
            String paraId = rankedDoc.getField("Id").stringValue();
            if(paraId.equals(paraid)){
                //String entity_links = rankedDoc.getField(field).stringValue();
                rankings.add(rankedDoc);
                //System.out.println("processing query : "+paraid);
                break;
            }else{
                rankings.add(null);
            }
        }
        return rankings;
    }

    public List<Document> getLuceneDocuments(String paraid){
        List<Document> formattedRankings = new ArrayList<>();
        try {
            TopDocs searchDocs = this.performSearch(paraid, 25);
            ScoreDoc[] scoringDocuments = searchDocs.scoreDocs;
            formattedRankings = this.getDocuments(scoringDocuments, paraid);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return formattedRankings;
    }

}
