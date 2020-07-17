package main.java.relationextractor.stanfordcorenlp;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

import main.java.containers.RelationTripleJSONTemplate;

public class StanfordRelationExtractor {

    StanfordCoreNLP pipeline;

    public StanfordRelationExtractor(String coref_flag){

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,depparse,natlog, coref, openie");
        props.setProperty("openie.resolve_coref",coref_flag);
        pipeline = new StanfordCoreNLP(props);
    }

    public List<RelationTripleJSONTemplate> fetchRelationTriples(String text){
        List<RelationTripleJSONTemplate> relationTripleJSONTemplateList = new ArrayList<>();

        Annotation doc = new Annotation(text);
        pipeline.annotate(doc);

        // Loop over sentences in the document
        for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
            // Get the OpenIE triples for the sentence
            Collection<RelationTriple> triples =
                    sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
            // Print the triples
            for (RelationTriple triple : triples) {
                /*System.out.println(triple.confidence + "\t" +
                        triple.subjectLemmaGloss() + "\t" +
                        triple.relationLemmaGloss() + "\t" +
                        triple.objectLemmaGloss());*/
                RelationTripleJSONTemplate relationTriple = new RelationTripleJSONTemplate();
                relationTriple.setSubject(triple.subjectLemmaGloss());
                relationTriple.setRelation(triple.relationLemmaGloss());
                relationTriple.setObject(triple.objectLemmaGloss());
                relationTriple.setConfidence_score(triple.confidence);
                relationTripleJSONTemplateList.add(relationTriple);
            }
        }
        return relationTripleJSONTemplateList;
    }

    public static void main(String[] args) throws Exception {
        // Create the Stanford CoreNLP pipeline
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner, depparse,natlog, coref, openie");
        props.setProperty("openie.resolve_coref","true");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // Annotate an example document.
        Annotation doc = new Annotation("Obama was born in Hawaii. He is our president.");
        pipeline.annotate(doc);

        // Loop over sentences in the document
        int sentNo = 0;
        for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
            // Get the OpenIE triples for the sentence
            System.out.println("Sentence #" + ++sentNo + ": " + sentence.get(CoreAnnotations.TextAnnotation.class));

            Collection<RelationTriple> triples =
                    sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
            // Print the triples
            for (RelationTriple triple : triples) {
                System.out.println(triple.confidence + "\t" +
                        triple.subjectLemmaGloss() + "\t" +
                        triple.relationLemmaGloss() + "\t" +
                        triple.objectLemmaGloss());
            }
        }
    }
}
