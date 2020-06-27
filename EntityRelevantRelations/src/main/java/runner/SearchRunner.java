package main.java.runner;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import main.java.commandparser.CommandParser;
import main.java.commandparser.RegisterCommands;
import main.java.commandparser.ValidateCommands;
import main.java.containers.Container;
import main.java.entityrelation.*;
import main.java.indexer.IndexCompare;
import main.java.searcher.BaseBM25;
import main.java.utils.*;
import main.java.containers.RankingJSONTemplate;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.List;

//import main.java.dbpedia.DBpedia;
//import main.java.queryexpansion.QueryExpansion;

/*
The searchParser object will hold all the information that is passed as the command line argument.
There are helper methods to get the data.
*/
public class SearchRunner implements ProgramRunner
{
    private RegisterCommands.CommandSearch searchParser = null;
    private ValidateCommands.ValidateSearchCommands validate = null;

    public SearchRunner(CommandParser parser)
    {
        searchParser = parser.getSearchCommand();
        validate = new ValidateCommands.ValidateSearchCommands(searchParser);
    }

    @Override
    public void run()  {

        Map<String,String> queryCBOR = null;

        validate.ValidateRetrievalOptions();
        if(searchParser.isArticleEnabled())
        {
            queryCBOR = SearchUtils.readOutline(searchParser.getQueryfile());
        }else
        {
            queryCBOR = SearchUtils.readOutlineSectionPath(searchParser.getQueryfile());
        }

        if(searchParser.isBM25Enabled())
        {
            BaseBM25 bm = null;
            try {
                bm = new BaseBM25(searchParser.getkVAL(),searchParser.getIndexlocation());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Map<String,Map<String, Container>> res = bm.getRanking(queryCBOR);

            String datafile ="";
            if(searchParser.getQueryfile().toLowerCase().contains("test".toLowerCase()))
            {
                datafile = "_test";
            }
            else if(searchParser.getQueryfile().toLowerCase().contains("train".toLowerCase()))
            {
                datafile = "_train";
            }

            String level = searchParser.isArticleEnabled()? "_article": "_section";
            String mname= "BM_25"+level+datafile;

            RunWriter.writeRunFile(mname,res);
        }

        if(searchParser.isCompareIndex()){
            validate.ValidateEntityDegree();
            try{
                IndexCompare indexCompare = new IndexCompare(searchParser.getIndexlocation(),
                        searchParser.getEntitylinker());
                indexCompare.compareIndexes();
            }catch (IOException ioe){
                ioe.printStackTrace();
            }
        }

        if(searchParser.isCreateEntityJSON()){
            validate.ValidateEntityDegree();
            try{
                BaseBM25 bm25 = new BaseBM25(searchParser.getkVAL(), searchParser.getIndexlocation());
                Map<String, Map<String, Container>> bm25_ranking = bm25.getRanking(queryCBOR);
                JSONConversion.BM25RankingConversion bm25RankingConversion = new JSONConversion.BM25RankingConversion(searchParser.getIndexlocation());
                List<RankingJSONTemplate> rankingJSONTemplate = bm25RankingConversion.convertBM25RankingToRankingJSON(bm25_ranking);
                WriteJSONFile writeJSONFile = new WriteJSONFile();
                writeJSONFile.writeMultipleFiles("output_query_json_file",
                        rankingJSONTemplate,
                        "JSON");

                List<RankingJSONTemplate> watJSONTemplate = bm25RankingConversion.convertBM25RankingToWATEntityJSON(bm25_ranking,
                        searchParser.getEntitylinker());
                writeJSONFile.writeMultipleFiles("output_query_wat_json_file",
                        watJSONTemplate,
                        "WAT_JSON");

                List<RankingJSONTemplate> wikiJSONTemplate = bm25RankingConversion.convertBM25RankingToWATEntityJSON(bm25_ranking,
                        searchParser.getWikientitylinker());
                writeJSONFile.writeMultipleFiles("output_query_wiki_json_file",
                        watJSONTemplate,
                        "Wiki_JSON");

                /*JSONConversion.RankingJSONTemplateConversion rankingJSONTemplateConversion = new JSONConversion.RankingJSONTemplateConversion();
                Map<String, Map<String, Map<Integer, List<String>>>> rankingMap = rankingJSONTemplateConversion.ConvertRankingJSONtoMap(watJSONTemplate);

                WATBM25FeatureBuilder watbm25FeatureBuilder = new WATBM25FeatureBuilder();
                Map<String, Map<String, Integer>> query_ent_list = watbm25FeatureBuilder.getFeatures(rankingMap);

                //Entities e = new Entities();
                //Map<String, Map<String, Integer>> query_ent_list = e.getSortedEntitiesPerQuery(bm25_ranking);
                //Map<String, Map<String, Integer>> query_ent_list = e.getSortedEntitiesPerQueryMentionFreq(bm25_ranking);
                //Map<String, Map<String, Double>> query_ent_list = e.getSortedEntitiesPerQueryMentionReciprocalRank(bm25_ranking);
                //Map<String, Map<String, Double>> query_ent_list = e.getSortedEntitiesPerQueryMentionScore(bm25_ranking);

                WriteFile write_file = new WriteFile();
                String level = searchParser.isArticleEnabled()? "_article": "_section";
                String datafile ="";
                if(searchParser.getQueryfile().toLowerCase().contains("test".toLowerCase()))
                {
                    datafile = "_test";
                }
                else if(searchParser.getQueryfile().toLowerCase().contains("train".toLowerCase()))
                {
                    datafile = "_train";
                }
                write_file.generateEntityRunFile(query_ent_list, "wat_entityBM25Freq"+level+datafile);

                 */


            }catch (IOException ioe){
                System.out.println(ioe.getMessage());
            }
        }

        if(searchParser.isEntityFreqEnabled()){
            validate.ValidateEntityDegree();
            try{
                /*BaseBM25 bm25 = new BaseBM25(searchParser.getkVAL(), searchParser.getIndexlocation());
                Map<String, Map<String, Container>> bm25_ranking = bm25.getRanking(queryCBOR);
                JSONConversion.BM25RankingConversion bm25RankingConversion = new JSONConversion.BM25RankingConversion(searchParser.getIndexlocation());
                List<RankingJSONTemplate> rankingJSONTemplate = bm25RankingConversion.convertBM25RankingToRankingJSON(bm25_ranking);
                WriteJSONFile writeJSONFile = new WriteJSONFile();
                writeJSONFile.writeMultipleFiles("output_query_json_file",
                        rankingJSONTemplate,
                        "JSON");

                List<RankingJSONTemplate> watJSONTemplate = bm25RankingConversion.convertBM25RankingToWATEntityJSON(bm25_ranking,
                        searchParser.getEntitylinker());
                writeJSONFile.writeMultipleFiles("output_query_wat_json_file",
                        watJSONTemplate,
                        "WAT_JSON");

                List<RankingJSONTemplate> watJSONTemplate = bm25RankingConversion.convertBM25RankingToWATEntityJSON(bm25_ranking,
                        searchParser.getEntitylinker());
                writeJSONFile.writeMultipleFiles("output_query_wiki_json_file",
                        watJSONTemplate,
                        "Wiki_JSON");*/

                ReadJSONFile readJSONFile = new ReadJSONFile();
                List<RankingJSONTemplate> rankingJSONTemplate = readJSONFile.getRankingJSONfromFolder(searchParser.getJsonfile());
                System.out.println(rankingJSONTemplate.size());

                JSONConversion.RankingJSONTemplateConversion rankingJSONTemplateConversion = new JSONConversion.RankingJSONTemplateConversion();
                Map<String, Map<String, Map<Integer, List<String>>>> rankingMap = rankingJSONTemplateConversion.ConvertRankingJSONtoMap(rankingJSONTemplate);

                WATBM25FeatureBuilder watbm25FeatureBuilder = new WATBM25FeatureBuilder();
                Map<String, Map<String, Integer>> query_ent_list = watbm25FeatureBuilder.getFeatures(rankingMap);


                //Entities e = new Entities();
                //Map<String, Map<String, Integer>> query_ent_list = e.getSortedEntitiesPerQuery(bm25_ranking);
                //Map<String, Map<String, Integer>> query_ent_list = e.getSortedEntitiesPerQueryMentionFreq(bm25_ranking);
                //Map<String, Map<String, Double>> query_ent_list = e.getSortedEntitiesPerQueryMentionReciprocalRank(bm25_ranking);
                //Map<String, Map<String, Double>> query_ent_list = e.getSortedEntitiesPerQueryMentionScore(bm25_ranking);

                WriteFile write_file = new WriteFile();
                String level = searchParser.isArticleEnabled()? "_article": "_section";
                String datafile ="";
                if(searchParser.getQueryfile().toLowerCase().contains("test".toLowerCase()))
                {
                    datafile = "_test";
                }
                else if(searchParser.getQueryfile().toLowerCase().contains("train".toLowerCase()))
                {
                    datafile = "_train";
                }
                write_file.generateEntityRunFile(query_ent_list, "wat_entityBM25Freq"+level+datafile);


            }catch (Exception ioe){
                System.out.println(ioe.getMessage());
            }

        }


        if(searchParser.isEcmExpandEnabled()){
            validate.ValidateEcmExpansion();
            try{
                Entities e = new Entities();
                QueryExapansion qe = new QueryExapansion();
                Map<String, Map<String, Double>> ecm_entities = e.readEntityRunFile(searchParser.getEcmentityfile());
                Map<String, String> expanded_query = qe.expandQueryWithEntities(queryCBOR,
                                                                                ecm_entities,
                                                                                searchParser.getEcmqenum());

                //BaseBM25 bm25 = new BaseBM25(100, searchParser.getIndexlocation());
                BaseBM25 bm25 = new BaseBM25(searchParser.getkVAL(), searchParser.getIndexlocation());
                Map<String, Map<String, Container>> expanded_bm25_ranking = bm25.getRanking(expanded_query);

                WriteFile write_file = new WriteFile();
                String level = searchParser.isArticleEnabled()? "_article": "_section";
                String datafile ="";
                if(searchParser.getQueryfile().toLowerCase().contains("test".toLowerCase()))
                {
                    datafile = "_test";
                }
                else if(searchParser.getQueryfile().toLowerCase().contains("train".toLowerCase()))
                {
                    datafile = "_train";
                }
                write_file.generateBM25RunFile(expanded_bm25_ranking, "EcmX-BM25"+level+datafile);

            }catch (IOException ioe){
                System.out.println(ioe.getMessage());
            }
        }

        if(searchParser.isEntityRelationEnabled()){
            validate.ValidateEntityRelation();

            try {
                //Map<String,String> querysecCBOR = SearchUtils.readOutlineSectionPath(searchParser.getQueryfile());

                BaseBM25 bm25 = new BaseBM25(searchParser.getkVAL(), searchParser.getIndexlocation());
                Map<String, Map<String, Container>> bm25_ranking = bm25.getRanking(queryCBOR);
                /*JSONConversion.BM25RankingConversion bm25RankingConversion = new JSONConversion.BM25RankingConversion(searchParser.getIndexlocation());
                List<RankingJSONTemplate> rankingJSONTemplate = bm25RankingConversion.convertBM25RankingToRankingJSON(bm25_ranking);
                WriteJSONFile writeJSONFile = new WriteJSONFile();
                writeJSONFile.writeFile("output_query_json_file.json", rankingJSONTemplate);*/
                ReadJSONFile readJSONFile = new ReadJSONFile();
                List<RankingJSONTemplate> rankingJSONTemplate = readJSONFile.getRankingJSONfromFolder(searchParser.getJsonfile());
                System.out.println(rankingJSONTemplate.size());
                JSONConversion.RankingJSONTemplateConversion rankingJSONTemplateConversion = new JSONConversion.RankingJSONTemplateConversion();
                Map<String, Map<String, Map<Integer, List<String>>>> rankingMap = rankingJSONTemplateConversion.ConvertRankingJSONtoMap(rankingJSONTemplate);

                /*WATFeatureBuilder watFeatureBuilder = new WATFeatureBuilder();
                Map<String, Map<String, Double[]>> featureVectors = watFeatureBuilder.getFeatures(rankingMap);*/


                FeatureGenerator featuregenerator = new FeatureGenerator();

                WATFeatureBuilderTrial watFeatureBuilderTrial = new WATFeatureBuilderTrial();
                Map<String, Map<String, Double[]>> featureVectors = watFeatureBuilderTrial.getFeatures(rankingMap);


                Map<String, Map<String, Double>> coOccRelFeatureVectors = featuregenerator.extractFeatures(featureVectors, 0);
                Map<String, Map<String, Double>> coOccCountFeatureVectors = featuregenerator.extractFeatures(featureVectors, 1);
                Map<String, Map<String, Double>> sortedCoOccRelFeatureVectors = featuregenerator.sortFeatureVectors(coOccRelFeatureVectors);
                Map<String, Map<String, Double>> sortedCoOccCountFeatureVectors = featuregenerator.sortFeatureVectors(coOccCountFeatureVectors);

                WriteFile write_file = new WriteFile();
                String level = searchParser.isArticleEnabled()? "_article": "_section";
                String datafile ="";
                if(searchParser.getQueryfile().toLowerCase().contains("test".toLowerCase()))
                {
                    datafile = "_test";
                }
                else if(searchParser.getQueryfile().toLowerCase().contains("train".toLowerCase()))
                {
                    datafile = "_train";
                }
                write_file.generateEntityRunFile(sortedCoOccRelFeatureVectors, "wat_rel_comention_feature_vector"+level+datafile);
                write_file.generateEntityRunFile(sortedCoOccCountFeatureVectors, "wat_count_comention_feature_vector"+level+datafile);

                /*Entities e = new Entities();
                Map<String, Map<String, Integer>> query_ent_list = e.getSortedEntitiesPerQuery(bm25_ranking);
                Map<String, Map<String, Double[]>> entity_ranking_list = e.readEntityRunFileDetails(searchParser.getEcmentityfile());

                //FeatureGenerator featuregenerator = new FeatureGenerator();
                WATFeatureGenerator featuregenerator = new WATFeatureGenerator();
                Map<String, Map<String, Double[]>> featureVectors = featuregenerator.getFeatureVectors(query_ent_list, bm25_ranking, entity_ranking_list, searchParser.getIndexlocation());
                //Map<String, Map<String, Double[]>> featureVectors = featuregenerator.getNormalizedFeatureVectors(query_ent_list, bm25_ranking, entity_ranking_list);
                Map<String, Map<String, Double>> hopRelationfeatureVectors = featuregenerator.extractFeatures(featureVectors, 0);
                Map<String, Map<String, Double>> relComentionfeatureVectors = featuregenerator.extractFeatures(featureVectors, 1);
                Map<String, Map<String, Double>> comentionfeatureVectors = featuregenerator.extractFeatures(featureVectors, 2);
                Map<String, Map<String, Double>> cocouplingcountfeatureVectors = featuregenerator.extractFeatures(featureVectors, 3);
                Map<String, Map<String, Double>> cocouplingrelfeatureVectors = featuregenerator.extractFeatures(featureVectors, 4);
                Map<String, Map<String, Double>> biblorelcouplingfeatureVectors = featuregenerator.extractFeatures(featureVectors, 5);
                Map<String, Map<String, Double>> biblocountcouplingfeatureVectors = featuregenerator.extractFeatures(featureVectors, 6);
                Map<String, Map<String, Double>> outlinksDirectlinksfeatureVectors = featuregenerator.extractFeatures(featureVectors, 7);
                Map<String, Map<String, Double>> inlinksDirectlinksfeatureVectors = featuregenerator.extractFeatures(featureVectors, 8);
                Map<String, Map<String, Double>> bidirlinksDirectlinksfeatureVectors = featuregenerator.extractFeatures(featureVectors, 9);
                Map<String, Map<String, Double>> sortedhopRelationFeatureVectors = featuregenerator.sortFeatureVectors(hopRelationfeatureVectors);
                Map<String, Map<String, Double>> sortedrelComentionFeatureVectors = featuregenerator.sortFeatureVectors(relComentionfeatureVectors);
                Map<String, Map<String, Double>> sortedcomentionFeatureVectors = featuregenerator.sortFeatureVectors(comentionfeatureVectors);
                Map<String, Map<String, Double>> sortedcocouplingCountFeatureVectors = featuregenerator.sortFeatureVectors(cocouplingcountfeatureVectors);
                Map<String, Map<String, Double>> sortedcocouplingRelFeatureVectors = featuregenerator.sortFeatureVectors(cocouplingrelfeatureVectors);
                Map<String, Map<String, Double>> sortedbiblorelcouplingFeatureVectors = featuregenerator.sortFeatureVectors(biblorelcouplingfeatureVectors);
                Map<String, Map<String, Double>> sortedbiblocountcouplingFeatureVectors = featuregenerator.sortFeatureVectors(biblocountcouplingfeatureVectors);
                Map<String, Map<String, Double>> sortedoutlinksDirectlinksFeatureVectors = featuregenerator.sortFeatureVectors(outlinksDirectlinksfeatureVectors);
                Map<String, Map<String, Double>> sortedinlinksDirectlinksFeatureVectors = featuregenerator.sortFeatureVectors(inlinksDirectlinksfeatureVectors);
                Map<String, Map<String, Double>> sortedbidirlinksDirectlinksFeatureVectors = featuregenerator.sortFeatureVectors(bidirlinksDirectlinksfeatureVectors);

                WriteFile write_file = new WriteFile();
                String level = searchParser.isArticleEnabled()? "_article": "_section";
                String datafile ="";
                if(searchParser.getQueryfile().toLowerCase().contains("test".toLowerCase()))
                {
                    datafile = "_test";
                }
                else if(searchParser.getQueryfile().toLowerCase().contains("train".toLowerCase()))
                {
                    datafile = "_train";
                }
                write_file.generateEntityRunFile(sortedhopRelationFeatureVectors, "1hoprelation_feature_vector"+level+datafile);
                write_file.generateEntityRunFile(sortedrelComentionFeatureVectors, "rel_comention_feature_vector"+level+datafile);
                write_file.generateEntityRunFile(sortedcomentionFeatureVectors, "count_comention_feature_vector"+level+datafile);
                write_file.generateEntityRunFile(sortedcocouplingCountFeatureVectors, "co_coupling_count_feature_vector"+level+datafile);
                write_file.generateEntityRunFile(sortedcocouplingRelFeatureVectors, "co_coupling_relevance_feature_vector"+level+datafile);
                write_file.generateEntityRunFile(sortedbiblorelcouplingFeatureVectors, "biblo_relevance_coupling_feature_vector"+level+datafile);
                write_file.generateEntityRunFile(sortedbiblocountcouplingFeatureVectors, "biblo_count_coupling_feature_vector"+level+datafile);
                write_file.generateEntityRunFile(sortedoutlinksDirectlinksFeatureVectors, "outlinks_feature_vector"+level+datafile);
                write_file.generateEntityRunFile(sortedinlinksDirectlinksFeatureVectors, "inlinks_feature_vector"+level+datafile);
                write_file.generateEntityRunFile(sortedbidirlinksDirectlinksFeatureVectors, "bidirectional_feature_vector"+level+datafile);
                write_file.generateFeatureVectorRunFile(featureVectors, "feature_vectors"+level+datafile);
                write_file.generateEntityRankLibRunFile(featureVectors, searchParser.getQrelfile(), "rank_lib"+level+datafile);

                Map<String, Map<String, Double>> hop_entities_score = e.getParagraphsScoreDouble(bm25_ranking, sortedhopRelationFeatureVectors);
                hop_entities_score = e.getRerankedParas(hop_entities_score);

                write_file.generateEntityRunFile(hop_entities_score, "paragraph_1hoprelation_feature"+level+datafile);

                Map<String, Map<String, Double>> rel_comention_entities_score = e.getParagraphsScoreDouble(bm25_ranking, sortedrelComentionFeatureVectors);
                rel_comention_entities_score = e.getRerankedParas(rel_comention_entities_score);

                write_file.generateEntityRunFile(rel_comention_entities_score, "paragraph_rel_comention_feature"+level+datafile);


                Map<String, Map<String, Double>> comention_entities_score = e.getParagraphsScoreDouble(bm25_ranking, sortedcomentionFeatureVectors);
                comention_entities_score = e.getRerankedParas(comention_entities_score);

                write_file.generateEntityRunFile(comention_entities_score, "paragraph_count_comention_feature"+level+datafile);

                Map<String, Map<String, Double>> co_coupling_entities_score = e.getParagraphsScoreDouble(bm25_ranking, sortedcocouplingCountFeatureVectors);
                co_coupling_entities_score = e.getRerankedParas(co_coupling_entities_score);

                write_file.generateEntityRunFile(co_coupling_entities_score, "paragraph_co_coupling_feature"+level+datafile);

                Map<String, Map<String, Double>> biblo_co_coupling_entities_score = e.getParagraphsScoreDouble(bm25_ranking, sortedbiblorelcouplingFeatureVectors);
                biblo_co_coupling_entities_score = e.getRerankedParas(biblo_co_coupling_entities_score);

                write_file.generateEntityRunFile(biblo_co_coupling_entities_score, "paragraph_biblo_co_coupling_feature"+level+datafile);
                */

            }catch (Exception ioe){
                ioe.printStackTrace();
            }
        }
        if(searchParser.isEntityRanklibEnabled()){
            validate.ValidateEntityRankLib();

            try {
                FeatureGenerator featureGenerator = new FeatureGenerator();
                Map<String, Map<String, Double>> query_entity_scores = featureGenerator.generateDotProduct(searchParser.getFeaturevectorfile(),
                        searchParser.getRankLibModelFile());
                //System.out.println(query_entity_scores);
                String level = searchParser.isArticleEnabled()? "_article": "_section";
                String datafile ="";
                if(searchParser.getQueryfile().toLowerCase().contains("test".toLowerCase()))
                {
                    datafile = "_test";
                }
                else if(searchParser.getQueryfile().toLowerCase().contains("train".toLowerCase()))
                {
                    datafile = "_train";
                }
                WriteFile write_file = new WriteFile();
                write_file.generateEntityRunFile(query_entity_scores, "entity_ranklib"+level+datafile);

                BaseBM25 bm25 = new BaseBM25(searchParser.getkVAL(), searchParser.getIndexlocation());
                Map<String, Map<String, Container>> bm25_ranking = bm25.getRanking(queryCBOR);

                Entities e = new Entities();
                Map<String, Map<String, Double>> ranked_entities_score = e.getParagraphsScoreDouble(bm25_ranking, query_entity_scores);
                ranked_entities_score = e.getRerankedParas(ranked_entities_score);

                write_file.generateEntityRunFile(ranked_entities_score, "paragraph_ranklib"+level+datafile);

            }catch (IOException ioe){
                ioe.printStackTrace();
            }
        }
        if(searchParser.isEntityCentroidEnabled()){
            validate.ValidateEntityCentroid();

            try {
                FeatureGenerator featureGenerator = new FeatureGenerator();
                Map<String, Map<String, Double>> query_entity_scores = featureGenerator.generateAverageCentroidVector(searchParser.getFeaturevectorfile());
                //System.out.println(query_entity_scores);
                String level = searchParser.isArticleEnabled()? "_article": "_section";
                String datafile ="";
                if(searchParser.getQueryfile().toLowerCase().contains("test".toLowerCase()))
                {
                    datafile = "_test";
                }
                else if(searchParser.getQueryfile().toLowerCase().contains("train".toLowerCase()))
                {
                    datafile = "_train";
                }
                WriteFile write_file = new WriteFile();
                write_file.generateEntityRunFile(query_entity_scores, "entity_avg_centroid"+level+datafile);

                BaseBM25 bm25 = new BaseBM25(searchParser.getkVAL(), searchParser.getIndexlocation());
                Map<String, Map<String, Container>> bm25_ranking = bm25.getRanking(queryCBOR);

                Entities e = new Entities();
                Map<String, Map<String, Double>> ranked_entities_score = e.getParagraphsScoreDouble(bm25_ranking, query_entity_scores);
                ranked_entities_score = e.getRerankedParas(ranked_entities_score);

                write_file.generateEntityRunFile(ranked_entities_score, "paragraph_avg_centroid"+level+datafile);

            }catch (IOException ioe){
                ioe.printStackTrace();
            }
        }

//        if(searchParser.isQEEnabled())
//        {
//            validate.ValidateQE();
//            QueryExpansion qe = new QueryExpansion(searchParser,queryCBOR);
//            qe.doQueryExpansion();
//
//        }
//
//        if(searchParser.isExistinDBpedia())
//        {
//            DBpedia qe = new DBpedia(searchParser,queryCBOR);
//            qe.retriveExistanceinDBpeda(searchParser.isDBpediaContain());
//        }



        if(searchParser.getisVerbose())
        {
            PrintUtils.displayQuery(queryCBOR);
        }

//        if(searchParser.is_qe_reranking())
//        {
//            validate.ValidateQE();
//            validate.ValidateReRank();
//            QueryExpansion qe = new QueryExpansion(searchParser,queryCBOR);
//            Map<String,Map<String,Container >> res = qe.doQueryExpansion();
//            QueryExpansionReRanking geReRank = new QueryExpansionReRanking(searchParser,queryCBOR,res);
//            String mname= "qe_rerank_"+searchParser.getQEType().toString();
//            geReRank.getDocumentFrequencyReRanking(mname);
//
//        }

       }
    }

