package main.java.containers;

import java.util.List;

public class RankingJSONTemplate {

    private String queryid;
    private String contextid;
    private String contexttext;
    private String contextrank;
    private String contextscore;
    private List<String> WATEntitiesTitle;
    private List<String> WikiEntitiesId;

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

    public List<String> getWATEntitiesTitle(){
        return WATEntitiesTitle;
    }

    public List<String> getWikiEntitiesId(){
        return WikiEntitiesId;
    }

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

    public void setWATEntitiesTitle(List<String> WATEntitiesTitle){
        this.WATEntitiesTitle = WATEntitiesTitle;
    }

    public void setWikiEntitiesId(List<String> WikiEntitiesId){
        this.WikiEntitiesId = WikiEntitiesId;
    }
}
