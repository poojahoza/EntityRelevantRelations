package main.java.containers;

import java.util.List;

public class RelationSentenceJSONTemplate {

    private String sentence;
    private List<RelationTripleJSONTemplate> triples;
    private List<RelationTokenJSONTemplate> tokens;

    public void setSentence(String sentence){this.sentence=sentence;}

    public void setTriples(List<RelationTripleJSONTemplate> triples){this.triples=triples;}

    public void setTokens(List<RelationTokenJSONTemplate> tokens){this.tokens=tokens;}

    public List<RelationTripleJSONTemplate> getTriples(){return triples;}

    public List<RelationTokenJSONTemplate> getTokens(){return tokens;}

    public String getSentence(){return sentence;}
}
