package main.java.searcher;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class ParagraphIndexSearcher{

    protected IndexSearcher searcher = null;
    protected QueryParser parser = null;
    protected Query queryObj = null;

    public ParagraphIndexSearcher(String indexLoc) throws IOException{
        searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(indexLoc))));
        parser = new QueryParser("Id", new EnglishAnalyzer());
    }

    private TopDocs performSearch(String paraid, int n)
            throws IOException, ParseException, NullPointerException {

        queryObj = parser.parse(QueryParser.escape(paraid));
        return searcher.search(queryObj, n);
    }

    private Boolean getRankings(ScoreDoc[] scoreDocs, String entityid)
            throws IOException {
        Boolean exists = Boolean.FALSE;
        for(int ind=0; ind<scoreDocs.length; ind++){

            //Get the scoring document
            ScoreDoc scoringDoc = scoreDocs[ind];

            //Create the rank document from searcher
            Document rankedDoc = searcher.doc(scoringDoc.doc);

            //Print out the results from the rank document
            String paraId = rankedDoc.getField("Id").stringValue();
            if(paraId.equals(entityid)){
                exists = Boolean.TRUE;
                System.out.println("processing query : "+entityid);
                break;
            }
        }
        return exists;
    }

    private Boolean checkExistenceOfIdinIndex(String entityid){
        Boolean formattedRankings = null;
        try {
            TopDocs searchDocs = this.performSearch(entityid, 25);
            ScoreDoc[] scoringDocuments = searchDocs.scoreDocs;
            formattedRankings = this.getRankings(scoringDocuments, entityid);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (NullPointerException nullPointerException){
            nullPointerException.printStackTrace();
        }
        return formattedRankings;
    }

    public Map<String, Boolean> checkExistenceOfIdinIndexWrapper(HashSet<String> entities){
        Map<String, Boolean> checkExistence = new HashMap<>();
        for(String id:entities){
            checkExistence.put(id, checkExistenceOfIdinIndex(id));
        }
        return checkExistence;
    }

}
