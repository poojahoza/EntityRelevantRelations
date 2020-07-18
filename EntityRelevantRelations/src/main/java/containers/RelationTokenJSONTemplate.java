package main.java.containers;

public class RelationTokenJSONTemplate {

    private String token;
    private int beginPosition;
    private int endPosition;

    public void setToken(String token){this.token=token;}

    public void setBeginPosition(int beginPosition){this.beginPosition=beginPosition;}

    public void setEndPosition(int endPosition){this.endPosition=endPosition;}

    public String getToken(){return token;}

    public int getBeginPosition(){return beginPosition;}

    public int getEndPosition(){return endPosition;}
}
