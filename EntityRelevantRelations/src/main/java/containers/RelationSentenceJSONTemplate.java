package main.java.containers;

import java.util.List;

public class RelationSentenceJSONTemplate {

    private String sentence;
    private List<Integer> sentenceOffsetBegin;
    private List<Integer> sentenceOffsetEnd;
    private List<RelationTripleJSONTemplate> triples;
    private List<RelationTokenJSONTemplate> tokens;

    public void setSentence(String sentence){this.sentence=sentence;}

    public void setTriples(List<RelationTripleJSONTemplate> triples){this.triples=triples;}

    public void setTokens(List<RelationTokenJSONTemplate> tokens){this.tokens=tokens;}

    public void setSentenceOffsetBegin(List<Integer> sentenceOffsetBegin){this.sentenceOffsetBegin=sentenceOffsetBegin;}

    public void setSentenceOffsetEnd(List<Integer> sentenceOffsetEnd){this.sentenceOffsetEnd=sentenceOffsetEnd;}

    public List<RelationTripleJSONTemplate> getTriples(){return triples;}

    public List<RelationTokenJSONTemplate> getTokens(){return tokens;}

    public String getSentence(){return sentence;}

    public List<Integer> getSentenceOffsetBegin(){return sentenceOffsetBegin;}

    public List<Integer> getSentenceOffsetEnd(){return sentenceOffsetEnd;}
}
