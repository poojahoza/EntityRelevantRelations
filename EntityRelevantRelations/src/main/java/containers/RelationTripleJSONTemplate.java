package main.java.containers;

public class RelationTripleJSONTemplate {

    private String subject;
    private String predicate;
    private String object;
    private double confidence_score;
    private String sentence;

    public void setSubject(String subject){this.subject=subject;}

    public void setRelation(String relation){this.predicate=relation;}

    public void setObject(String object){this.object=object;}

    public void setConfidence_score(double score){confidence_score=score;}

    public void setSentence(String sentence){this.sentence=sentence;}

    public String getSubject(){return subject;}

    public String getRelation(){return predicate;}

    public String getObject(){return object;}

    public double getConfidence_score(){return confidence_score;}

    public String getSentence(){return sentence;}
}
