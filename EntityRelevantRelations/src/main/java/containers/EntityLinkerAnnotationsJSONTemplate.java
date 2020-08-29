package main.java.containers;

/**
 * @author poojaoza
 **/
public class EntityLinkerAnnotationsJSONTemplate {

    private String wiki_title;
    private int wiki_id;
    private int start;
    private int end;
    private double rho;
    private String spot;

    public void setWiki_title(String title){wiki_title=title;}

    public void setWiki_id(int wiki_id){this.wiki_id=wiki_id;}

    public void setStart(int start){this.start=start;}

    public void setEnd(int end){this.end=end;}

    public void setRho(double rho){this.rho=rho;}

    public void setSpot(String spot){this.spot=spot;}

    public String getWiki_title(){return wiki_title;}

    public int getWiki_id(){return wiki_id;}

    public int getStart(){return start;}

    public int getEnd(){return end;}

    public double getRho(){return rho;}

    public String getSpot(){return spot;}
}
