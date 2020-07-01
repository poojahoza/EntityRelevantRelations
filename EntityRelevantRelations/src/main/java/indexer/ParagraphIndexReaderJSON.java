package main.java.indexer;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ParagraphIndexReaderJSON {

    private IndexReader indexReader;

    public ParagraphIndexReaderJSON(String indexLoc) throws IOException {
        Path indexPath = Paths.get(indexLoc);
        FSDirectory indexDir = FSDirectory.open(indexPath);
        IndexWriterConfig conf = new IndexWriterConfig(new EnglishAnalyzer());
        conf.setOpenMode(IndexWriterConfig.OpenMode.APPEND);
        indexReader = DirectoryReader.open(new IndexWriter(indexDir, conf));
    }

    public Map<String, String> getContent(String fieldname){
        int numDocs = indexReader.numDocs();
        Map<String, String> paradata = new LinkedHashMap<>();
        try {
            for(int i = 0; i < numDocs; i++){
                if(i%10000==0){
                System.out.print(".");}
                Document doc = indexReader.document(i);
                Boolean ent_links = doc.getField(fieldname).stringValue().contains("75% Less Fat");
                if(ent_links){
                    System.out.println("**********************************");
                    System.out.println(doc.getField(fieldname).stringValue());
                    System.out.println("**********************************");
                }
                paradata.put(doc.getField("Id").stringValue(),
                        doc.getField(fieldname).stringValue());
            }
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
        return paradata;
    }
}
