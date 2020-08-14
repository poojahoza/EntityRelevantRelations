package main.java.commandparser;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/*
All the necessary commands should be registered here first for your implementation.
*/

public class RegisterCommands
{
    public enum qeType
    {
        entityText , entityID , entityTextID,entityIDInEntityField;
    }
     /*
      All the commands as part of the index should be registered here
      */
     @Parameters(separators = "=",commandDescription = "Command to Index the Corpus")
     public static class CommandIndex
     {

          @Parameter(names = {"-i","--corpus-file"},description = "Corpus file to index. In case of Entity Abstract, please specify Entity Index location",required=true)
          private String IndexPath;

          @Parameter(names = {"-d","--dest-location"},description = "Location to save the index file")
          private String destpath = System.getProperty("user.dir") + System.getProperty("file.separator") + "indexed_file";

         @Parameter(names = {"--para-index"}, description = "Perform Paragraph Index")
         private Boolean isParaIndex = false;

         @Parameter(names = {"--entity-index"}, description = "Perform Entity Index")
         private Boolean isEntity = false;

         @Parameter(names = {"--abstract-index"}, description = "Perform Entity Abstract Index")
         private Boolean isEntityAbstract = false;


          @Parameter(names = "--help", help = true)
          private boolean help;

          boolean isHelp()
          {
               return help;
          }

          public String getIndexPath()
          {
               return IndexPath;
          }

          public String getDestpath()
          {
               return destpath;
          }

          public Boolean getIsParaIndex() {return isParaIndex;}

          public Boolean getIsEntity() {return isEntity;}

         public Boolean getIsEntityAbstract() {return isEntityAbstract;}


     }

     /*
     All the commands as part of the search should be registered here
     The required parameter is set to true because its mandatory

     Example command for your search implementation.
     --rerank one of the method implementation requires word embeddings file, whatever data you need, you can accept it.

     There are some helper functions that needs to be implemented to validate the inputs for your method, if the user missed any
     data, the program should exit with a status message

     */

     @Parameters(separators = "=",commandDescription = "Command to search")
     public static class CommandSearch
     {
          @Parameter(names = {"-i", "--index-loc"}, description = "Indexed directory to search", required = true)
          private String indexlocation = null;

          @Parameter(names = {"-q", "--query-cbor"}, description = "Query file (CBOR file)", required = true)
          private String queryfile = null;

         @Parameter(names = {"-qrel", "--entity-qrel"}, description = "Entity qrel file")
         private String qrelfile = null;

         @Parameter(names = {"-ent-link", "--entity-linker"}, description = "Entity Linker Index Location")
         private String entitylinker = null;

         @Parameter(names = {"-para-aggr-csv", "--para-aggr-csv"}, description = "Para aggr csv file location")
         private String paracsvloc = null;

         @Parameter(names = {"-wiki-ent-link", "--wiki-entity-linker"}, description = "Wiki Entity Linker Index Location")
         private String wikientitylinker = null;

         @Parameter(names = {"-json-f", "--json-file"}, description = "JSON File Location")
         private String jsonfile = null;

        @Parameter(names = "--help", help = true)
        private boolean help;
         @Parameter(names = {"-entity-run", "--entity-run"}, description = "Entity run file")
         private String ecmentityfile = null;


         @Parameter(names = {"-ecm-qe-num", "--ecm-query-expansion-terms-num"}, description = "ECM  Query Expansion Terms Number")
         private Integer ecmqenum = 20;


          @Parameter(names = {"-k","--candidate-set-val"}, description = "How many candidate set to retrieve using BM25")
          private Integer kVAL=100;



         @Parameter(names = {"-we","--word-embedding"},description ="Pass the word embedding file GloVe/ Word2Vec")
         private String word_embedding_file = null;


         @Parameter(names = {"-dim","--word-dimension"},description ="Dimension of the Word embeddings")
         private Integer dimension=0;

         @Parameter(names = {"-model","--ranklib-model"},description ="Pass the file location of ranklib model file")
         private String ranklib_model=null;

         @Parameter(names = {"-f", "--entity-feature"}, description = "Entity feature vector file")
         private String featurevectorfile = null;

         @Parameter(names = {"-bm25","--default-bm25"},description ="Rerank the initial retrieved cluster using document similarity")
         private boolean isBM25 =false;

         @Parameter(names = {"-V","--verbose"},description ="Print out  some of the results  into stdout")
         private boolean isVerbose =false;

         @Parameter(names = "--rerank",description ="Rerank the initial retrieved document using document similarity")
         private boolean isReRank =false;

         @Parameter(names = "--bias-fact",description ="Bias factor to get the document representation")
         private Integer biasFactor = 1;

         @Parameter(names = {"--rerank-idf"},description ="Rerank the document based on the IDF")
         private boolean isIDFReRank =false;

         @Parameter(names = {"--rerank-df"},description ="Rerank the document based on the DF")
         private boolean isDFReRank =false;

         @Parameter(names = {"--cosine-sim"},description ="Rerank the document based on the cosine similarity between two strings")
         private boolean isCosineSimilarity =false;

         @Parameter(names = {"--jaccard-sim"},description ="Rerank the document based on the Jaccard similarity between two strings")
         private boolean isJaccardSimilarity = false;

         @Parameter(names = {"--jaro-sim"},description ="Rerank the document based on the Jaro Winkler similarity between two strings")
         private boolean isJaroEnabled = false;

         @Parameter(names = {"--dice-sim"},description ="Rerank the document based on the Sorensen Dice coefficient similarity between two strings")
         private boolean isDiceEnabled = false;

         @Parameter(names = {"--leven-sim"},description ="Rerank the document based on the NormalizedLevenshtein similarity between two strings")
         private boolean isLevenSim = false;

         @Parameter(names = {"-dbpedia","--exist-dbpedia"},description ="Find out if an entity exist in dbpedia")
         private boolean isExistDBpedia =false;

         @Parameter(names = {"-dbpcontain","--dbpedia-contain"},description ="Change the search to contain")
         private boolean isDBpediaContain =false;

         @Parameter(names = {"-qe","--query-expansion"},description ="Rerank the document using Query expansion")
         private boolean isQE =false;

         @Parameter(names = {"-qe-type","--query-expansion-type"},description ="Select type of Query expansion (entityText, entityID , entityTextID) ")
         private qeType qeTypeValue = qeType.entityText;

         @Parameter(names = {"-top"},description ="specify the top number of selected entity to used in the Query expansion")
         private int numberOfReturnedEntity = 3;


         @Parameter(names = "--entity-default-freq",description ="Rerank the initial retrieved document using entity frequency")
         private boolean isEntityFreq =false;

         @Parameter(names = "--create-entity-JSON",description ="Create JSON files of BM25 from index")
         private boolean isCreateEntityJSON =false;

         @Parameter(names = "--create-index-JSON",description ="Create JSON files from index")
         private boolean iscreateIndexJSON =false;

         @Parameter(names = "--create-rel-triple",description ="Create Stanford Relation triples JSON files")
         private boolean createRelTriple =false;

         @Parameter(names = "--check-index-exist",description ="Check existence of entities in entity index")
         private boolean checkIndexExist =false;

         @Parameter(names = "--convert-entity-id",description ="Generate file for conversion from title to entity id")
         private boolean performEntityIdConversion =false;

         @Parameter(names = "--compare-index",description ="Compare two indexes")
         private boolean compareIndex =false;

         @Parameter(names = "--para-aggr",description ="flag to convert para aggr to BM25 ranking hashmap")
         private boolean paraAggr =false;

         @Parameter(names = "--entity-degree",description ="Rerank the initial retrieved document using entity degree")
         private boolean isEntityDegree =false;

         @Parameter(names = "--entity-index",description ="Pass the index location of entity index")
         private String entityIndLoc = null;

         @Parameter(names = "--entity-sim",description ="Rerank the initial retrieved document using entity abstract similarity")
         private boolean isEntitySim =false;

         @Parameter(names = "--entity-expand",description ="Rerank the initial retrieved document using expanded query")
         private boolean isQueryExpand =false;

         @Parameter(names = "--entity-ecm-expand",description ="Rerank the initial retrieved document using ecm entities to expand query")
         private boolean isEcmExpandEnabled =false;


         @Parameter(names = "--entity-relation",description ="Generate the feature vectors and ranklib model")
         private boolean isEntityRelationEnabled =false;

         @Parameter(names = "--entity-ranklib",description ="Rerank the passages using entity ranklib")
         private boolean isEntityRanklibEnabled =false;

         @Parameter(names = "--entity-centroid",description ="Rerank the passages using entity average centroid")
         private boolean isEntityCentroidEnabled =false;

         @Parameter(names = "article",description ="Article level retrieval")
         private boolean isArticleEnabled =false;

         @Parameter(names = "section",description ="Section level retrieval")
         private boolean isSectionEnabled =false;

         @Parameter(names = "wiki",description ="Wiki JSON file input")
         private boolean isWikiEnabled =false;

         @Parameter(names = "wat",description ="WAT JSON file input")
         private boolean isWatEnabled =false;

         @Parameter(names = "coref-flag",description ="flag to indicate stanford coref should be enabled or disabled")
         private boolean isCorefFlag =false;

         @Parameter(names = "--entity-doc-sim",description ="Rerank the initial retrieved document using entity abstract similarity")
         private boolean isEntityDocSimEnabled = false;

         @Parameter(names = "--spam-filter",description ="Uses the spam filter before performing the re-rank")
         private static boolean isSpamFilterEnabled = false;

         @Parameter(names = "--spam-filter2",description ="Uses the spam filter before performing the re-rank")
         private static boolean isSpecialCharSpamFilterEnabled = false;

         @Parameter(names = {"--spam-loc"}, description = "Directory to spam train file")
         private static String spamLocation = null;


         @Parameter(names = {"--ham-loc"}, description = "Directory to ham train file")
         private static String hamLocation = null;


         @Parameter(names = "--parallel",description ="Uses the parallel stream for the reranker methods")
         private boolean isParallelEnabled = false;

         @Parameter(names = "--mrf",description ="Uses the parallel stream for the reranker methods")
         private boolean isMrfEnabled = false;

         @Parameter(names = "--cluster",description ="Cluster Ranking")
         private boolean isClusterRankerEnabled = false;

         @Parameter(names = "--qrel-path",description ="Pass the absolute path of the Qrel")
         private String qrelPath = null;

         @Parameter(names = "--rank-lib",description ="Provide path to the Ranklib")
         private String ranklibpath = null;

         @Parameter(names = "--qe-reranking",description ="Query expansion method based on the Entities and reranking")
         private boolean qe_reranking = false;

         @Parameter(names = "--qe-entity-degree",description ="Query expansion based on the entity degree and reranking")
         private boolean qe_entity_degree_reranking = false;

        @Parameter(names = "--qe-exp-df", description = "Query expansion using PRF and the terms selected using DF")
        private boolean isQueryExDF = false;

        @Parameter(names = "--qe-exp-idf", description = "Query expansion using PRF and the terms selected using IDF")
        private boolean isQueryExIDF = false;

        @Parameter(names = "--qe-exp-entity", description = "Query expansion using PRF, also considering entity abstract")
        private boolean isQueryExpEntity = false;


        @Parameter(names = "--prf-val", description = "Top k documents to consider as Pseudo relevance feedback")
        private Integer prfVAL = 5;

        @Parameter(names = "--prf-val-term", description = "Top k terms to consider per query term")
        private Integer prfValTerms = 10;

        @Parameter(names = "--prf-val-k", description = "Top k terms to consider for query expansion")
        private Integer prfValTermsKterms = 50;

        @Parameter(names = "--qe-exp-rm3", description = "Relevance model 3 query expansion")
        private boolean isQueryExpRm3 = false;

        @Parameter(names = "--test", description = "Only for testing purposes")
        private boolean isTestEnabled = false;

        public boolean isQueryExpRm3() {
            return isQueryExpRm3;
        }

        public boolean isTestEnabled() {
            return isTestEnabled;
        }

        public Integer getPrfVAL() {
            return prfVAL;
        }

        public Integer getPrfValTermsKterms() {
            return prfValTermsKterms;
        }

        public Integer getPrfValTerms() {
            return prfValTerms;
        }

        public boolean isQueryExDFEnabled() {
            return isQueryExDF;
        }

        public boolean isQueryExIDFEnabled() {
            return isQueryExIDF;
        }

        public boolean isQueryExpEntityEnabled() {
            return isQueryExpEntity;
        }

        public boolean is_qe_reranking() {
            return qe_reranking;
        }

        public boolean isQe_entity_degree_rerankingEnabled() {
            return qe_entity_degree_reranking;
        }

        public String getRanklibPath() {
            return ranklibpath;
        }

         public boolean isClusterRankerEnabled()
         {
             return isClusterRankerEnabled;
         }
         public boolean isLevenSimEnabled() {return isLevenSim;}
         public String getQrelPath()
         {
             return qrelPath;
         }
         public boolean isMrfEnabled() {return  isMrfEnabled;}
         public boolean isParallelEnabled(){return isParallelEnabled;}
         public static boolean isSpamFilterEnabled()
         {
             return isSpamFilterEnabled;
         }
         public static String SpamLocation()
         {
             return spamLocation;
         }
         public static String hamLocation()
         {
             return hamLocation;
         }
         public boolean isArticleEnabled()
         {
             return isArticleEnabled;
         }
         public boolean isWikiEnabled(){return isWikiEnabled;}
         public boolean isWatEnabled() {return isWatEnabled;}
         public boolean isCorefFlag(){return isCorefFlag;}
         public boolean isSectionEnabled()
         {
             return isSectionEnabled;
         }
         public boolean isExistinDBpedia(){return isExistDBpedia;}
         public boolean isDBpediaContain() {return isDBpediaContain;}
         public boolean isQEEnabled(){return isQE;}
         public String getEntitylinker(){return entitylinker;}
         public String getParacsvloc(){return paracsvloc;}
         public String getWikientitylinker(){return wikientitylinker;}
         public String getJsonfile(){return jsonfile;}
         public qeType getQEType () {return qeTypeValue;}
         public int getNumberOfReturnedEntity() {return numberOfReturnedEntity;}

         public Integer getBiasFactor() {return biasFactor;}
         public boolean isDiceEnabled() { return isDiceEnabled;}
         public boolean isJaroSimilarityEnabled(){return isJaroEnabled;}
         public boolean isJaccardSimilarityEnabled(){return isJaccardSimilarity;}

         public boolean isIDFReRankEnabled()
         {
             return isIDFReRank;
         }
         public boolean isDFReRankEnabled()
         {
             return isDFReRank;
         }

        public String getIndexlocation() {
            return indexlocation;
        }

        public String getQueryfile() {
            return queryfile;
        }

        public String getQrelfile() {
            return qrelfile;
        }


         public String getEcmentityfile()
         {
             return ecmentityfile;
         }

         public String getFeaturevectorfile()
         {
             return featurevectorfile;
         }

          public boolean isReRankEnabled()
          {
               return isReRank;
          }

          public Integer getkVAL()
          {
               return kVAL;
          }

          public String getWordEmbeddingFile()
          {
              return word_embedding_file;
          }

          public Integer getDimension()
          {
              return dimension;
          }

          public Boolean getisVerbose()
          {
              return isVerbose;
          }

          public boolean isBM25Enabled()
          {
              return isBM25;
          }

          public boolean isCosineSimilarityEnabled() {return isCosineSimilarity;}


            public boolean isEntityDocSimEnabled()
            {
                return isEntityDocSimEnabled;
            }

          public boolean isEntityDegreeEnabled()
         {
             return isEntityDegree;
         }

         public boolean isEntityFreqEnabled() { return isEntityFreq;}

         public boolean isCreateEntityJSON(){return isCreateEntityJSON;}

         public boolean isCompareIndex(){return compareIndex;}

         public boolean isParaAggr(){return paraAggr;}

         public boolean isCreateIndexJSON(){return iscreateIndexJSON;}

         public boolean isCreateRelTriple(){return createRelTriple;}

         public boolean isCheckIndexExist(){return checkIndexExist;}

         public boolean isPerformEntityIdConversion(){return performEntityIdConversion;}

         public Integer getEcmqenum() { return  ecmqenum; }

         public boolean isEntityRelationEnabled()
         {
             return isEntityRelationEnabled;
         }

         public boolean isEntityRanklibEnabled()
         {
             return isEntityRanklibEnabled;
         }

         public boolean isEntityCentroidEnabled()
         {
             return isEntityCentroidEnabled;
         }

         public boolean isEcmExpandEnabled(){ return isEcmExpandEnabled;}

         public String getRankLibModelFile()
         {
             return ranklib_model;
         }

        public String getEntityIndLoc() {
            return entityIndLoc;
        }

        public boolean isEntitySimEnabled() {
            return isEntitySim;
        }

        public boolean isQueryExpand() {
            return isQueryExpand;
        }

        boolean isHelp() {
            return help;
        }

        public static boolean isSpecialCharSpamFilterEnabled()
        {
            return isSpecialCharSpamFilterEnabled;
        }
    }

    @Parameters(separators = "=", commandDescription = "Command to create training and test data for the spam classifier")
    public static class IndexHamSpam {

        @Parameter(names = {"-p", "--paragraphs-file"}, description = "paragraph corpus directory")
        private String paragraphPath = System.getProperty("user.dir") + System.getProperty("file.separator") + "indexed_file";

        @Parameter(names = {"-q", "--qrels-file"}, description = "qrels file")
        private String qrelPath = System.getProperty("user.dir") + System.getProperty("file.separator") + "indexed_file";

        @Parameter(names = {"-spamTrain"}, description = "Location to save the spam training data")
        private String spamTrainPath = System.getProperty("user.dir") + System.getProperty("file.separator") + "indexed_file";

        @Parameter(names = {"-hamTrain"}, description = "Location to save the ham training data")
        private String hamTrainPath = System.getProperty("user.dir") + System.getProperty("file.separator") + "indexed_file";

        @Parameter(names = {"-hamSpamTest"}, description = "Location to save the ham and spam test data")
        private String hamSpamTestpath = System.getProperty("user.dir") + System.getProperty("file.separator") + "indexed_file";

        @Parameter(names = {"-hamTest"}, description = "Location to save the ham test data")
        private String hamTestpath = System.getProperty("user.dir") + System.getProperty("file.separator") + "indexed_file";

        @Parameter(names = {"-spamTest"}, description = "Location to save the spam test data")
        private String spamTestpath = System.getProperty("user.dir") + System.getProperty("file.separator") + "indexed_file";


        @Parameter(names = "--help", help = true)
        private boolean help;

        boolean isHelp() {
            return help;
        }

        public String getParagraphPath() {
            return paragraphPath;
        }

        public String getQrelPath() {
            return qrelPath;
        }

        public String getSpamTrainPath() {
            return spamTrainPath;
        }

        public String getHamTrainPath() {
            return hamTrainPath;
        }

        public String getHamSpamTestPath() {
            return hamSpamTestpath;
        }

        public String getHamTestPath() {
            return hamTestpath;
        }

        public String getSpamTestPath() {
            return spamTestpath;
        }

    }

    @Parameters(separators = "=", commandDescription = "Help Information")
    public static class CommandHelp {

    }

    @Parameters(separators = "=", commandDescription = "Ranker")
    public static class Ranker {
        @Parameter(names = {"--model-file"}, description = "Location of the model file", required = true)
        private String modelFile = null;

        @Parameter(names = {"--run-file"}, description = "Location of the run file", required = true)
        private String runfile = null;

        @Parameter(names = {"--mname"}, description = "Method name suffix")
        private String mname = "mrfupdated";

        public String getModelFile() {
            return modelFile;
        }

        public String getRunfile() {
            return runfile;
        }

        public String getMname() {
            return mname;
        }

    }

}
