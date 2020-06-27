package main.java.indexer;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        }
    }
}
