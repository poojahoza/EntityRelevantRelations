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
                String[] para_ids = new String[]{"232f55315d6a5c26ffc91083240e1a449d808a46",
                "b88776b46cdcf3cc3ca524893360d44ff71d4d49",
                "76dbb24b39fac12a7be9486164f45f9a12fc8993",
                "edc624f6c22ed13953b4ad63e14ed16df5b63eff",
                "4d2e3dcd4804221bbfc61a4ce9e790d99b53da2f"};
                Document doc1 = null;
                Document doc2 = null;
                Set<String> doc1_set_out = null;
                Set<String> doc1_set_in = null;
                Set<String> doc2_set = null;
                //String[] doc1_ent;
                //String[] doc2_ent;
                //String[] doc2_ent_2;
                for(int z = 0; z < para_ids.length; z++){
                    System.out.println("Processing doc : "+para_ids[z]);
                    for(int i=0; i < oneIndexNumDocs; i++){
                        doc1 = oneIndex.document(i);
                        if(doc1.getField("Id").stringValue().equals(para_ids[z])){
                            String[] doc1_ent = doc1.getField("OutlinkIds").stringValue().split("\n");
                            String[] doc1_ent_in = doc1.getField("InlinkIds").stringValue().split("\n");
                            doc1_set_out = new HashSet<String>(Arrays.asList(doc1_ent));
                            doc1_set_in = new HashSet<String>(Arrays.asList(doc1_ent_in));
                            break;
                        }
                    }
                    for(int x=0; x < twoIndexNumDocs; x++){
                        doc2 = twoIndex.document(x);
                        if(doc2.getField("Id").stringValue().equals(para_ids[z])){
                            String[] doc2_ent = doc2.getField("OutlinkIds").stringValue().split("\n");
                            String[] doc2_ent_2 = new String[doc2_ent.length];
                            for(int j = 0; j < doc2_ent.length; j++){
                                doc2_ent_2[j]=doc2_ent[j].split("_")[0];
                            }
                            doc2_set = new HashSet<String>(Arrays.asList(doc2_ent_2));
                            break;
                        }
                    }
                    System.out.println(doc1_set_out);
                    System.out.println(doc1_set_in);
                    System.out.println(doc2_set);
                    System.out.println("============================================");
                    /*for(int i=0; i < oneIndexNumDocs; i++){
                        if(i==5){break;}
                        Document doc1 = oneIndex.document(i);
                        for(int x = 0; x < twoIndexNumDocs; x++){
                            Document doc2 = twoIndex.document(x);
                            if(doc1.getField("Id").stringValue().equals(doc2.getField("Id").stringValue())){
                                System.out.println(doc1.getField("Id").stringValue());
                                String[] doc1_ent = doc1.getField("OutlinkIds").stringValue().split("\n");
                                String[] doc2_ent = doc2.getField("OutlinkIds").stringValue().split("\n");
                                String[] doc2_ent_2 = new String[doc2_ent.length];
                                for(int j = 0; j < doc2_ent.length; j++){
                                    doc2_ent_2[j]=doc2_ent[j].split("_")[0];
                                }
                                Set<String> doc1_set = new HashSet<String>(Arrays.asList(doc1_ent));
                                Set<String> doc2_set = new HashSet<String>(Arrays.asList(doc2_ent_2));
                                System.out.println(doc1_set);
                                System.out.println(doc2_set);
                                System.out.println("============================================");
                                break;
                            }
                        }
                    }*/
                }

                oneIndex.close();
                twoIndex.close();
            }catch (IOException ioe){
                ioe.printStackTrace();
            }
        }

    }
}
