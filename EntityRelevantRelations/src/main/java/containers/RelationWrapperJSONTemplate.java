package main.java.containers;

import java.util.List;

import main.java.containers.RelationTripleJSONTemplate;

public class RelationWrapperJSONTemplate {

    private String queryid;
    private String contextid;
    private String contexttext;
    private String contextrank;
    private String contextscore;
    private List<RelationTripleJSONTemplate> triples;
    private List<RelationTokenJSONTemplate> tokens;

    public String getQueryid(){
        return queryid;
    }

    public String getContextid(){
        return contextid;
    }

    public String getContexttext(){
        return contexttext;
    }

    public String getContextrank(){
        return contextrank;
    }

    public String getContextscore(){
        return contextscore;
    }

    public List<RelationTripleJSONTemplate> getTriples(){return triples;}

    public List<RelationTokenJSONTemplate> getTokens(){return tokens;}

    public void setQueryid(String queryid){
        this.queryid = queryid;
    }

    public void setContextid(String contextid){
        this.contextid = contextid;
    }

    public void setContexttext(String contexttext){
        this.contexttext = contexttext;
    }

    public void setContextrank(String contextrank){
        this.contextrank = contextrank;
    }

    public void setContextscore(String contextscore){
        this.contextscore = contextscore;
    }

    public void setTriples(List<RelationTripleJSONTemplate> triples){this.triples=triples;}

    public void setTokens(List<RelationTokenJSONTemplate> tokens){this.tokens=tokens;}
}
