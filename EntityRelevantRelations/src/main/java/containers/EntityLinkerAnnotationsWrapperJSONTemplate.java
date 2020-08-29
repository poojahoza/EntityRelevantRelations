package main.java.containers;

import java.util.List;

/**
 * @author poojaoza
 **/
public class EntityLinkerAnnotationsWrapperJSONTemplate {

    private String queryid;
    private String contextid;
    private String contexttext;
    private String contextrank;
    private String contextscore;
    private List<EntityLinkerAnnotationsJSONTemplate> WATAnnotations;


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

    public List<EntityLinkerAnnotationsJSONTemplate> getWATAnnotations() {return WATAnnotations;}

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

    public void setWATAnnotations(List<EntityLinkerAnnotationsJSONTemplate> watAnnotations){this.WATAnnotations=watAnnotations;}
}
