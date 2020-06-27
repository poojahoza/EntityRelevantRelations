package main.java.indexer;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class IndexCompare {

    private IndexReader oneIndex;
    private IndexReader twoIndex;

    public IndexCompare(String oneIndexLoc,
                        String twoIndexLoc) throws IOException{

        Path indexPath = Paths.get(oneIndexLoc);
        FSDirectory indexDir = FSDirectory.open(indexPath);
        //Standard Analyzer
        IndexWriterConfig conf = new IndexWriterConfig(new EnglishAnalyzer());
        conf.setOpenMode(IndexWriterConfig.OpenMode.APPEND);
        oneIndex = DirectoryReader.open(new IndexWriter(indexDir, conf));

        Path indexPath2 = Paths.get(twoIndexLoc);
        FSDirectory indexDir2 = FSDirectory.open(indexPath2);
        //Standard Analyzer
        IndexWriterConfig conf1 = new IndexWriterConfig(new EnglishAnalyzer());
        conf1.setOpenMode(IndexWriterConfig.OpenMode.APPEND);
        twoIndex = DirectoryReader.open(new IndexWriter(indexDir2, conf1));
    }

    public void compareIndexes(){
        int oneIndexNumDocs = oneIndex.numDocs();
        int twoIndexNumDocs = twoIndex.numDocs();

        if(oneIndexNumDocs==twoIndexNumDocs){
            System.out.println("both indexes have equal number of docs");
            try {
                for(int i=0; i < oneIndexNumDocs; i++){
                    if(i==10){break;}
                    Document doc1 = oneIndex.document(i);
                    for(int x = 0; x < twoIndexNumDocs; x++){
                        Document doc2 = twoIndex.document(x);
                        if(doc1.getField("Id").stringValue().equals(doc2.getField("Id").stringValue())){
                            System.out.println(doc1.getField("Id").stringValue());
                            String[] doc1_ent = doc1.getField("EntityLinks").stringValue().split("\n");
                            String[] doc2_ent = doc2.getField("OutlinkIds").stringValue().split("\n");
                            String[] doc2_ent_2 = new String[doc2_ent.length];
                            for(int j = 0; j < doc2_ent.length; j++){
                                doc2_ent_2[j]=doc2_ent[j].split("_")[0];
                            }
                            Set<String> doc1_set = new HashSet<String>(Arrays.asList(doc1_ent));
                            Set<String> doc2_set = new HashSet<String>(Arrays.asList(doc2_ent_2));
                            if(!doc1_set.containsAll(doc2_set)) {
                                System.out.println("Wiki entities does not contain all the entities of WAT");
                            }
                            if(!doc2_set.containsAll(doc1_set)){
                                System.out.println("WAT entities does not contain all the entities of Wiki");
                            }
                            break;
                        }
                    }
                }
                oneIndex.close();
                twoIndex.close();
            }catch (IOException ioe){
                ioe.printStackTrace();
            }
        }

    }
}
