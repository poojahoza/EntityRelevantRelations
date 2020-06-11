package main.java.containers;

/**
 * @author poojaoza
 **/
public class EntityAnnotation {

    private String title;
    private int id;
    private int start;
    private int end;
    private double rho;
    private String spot;

    public EntityAnnotation(String title,
                            int id,
                            int start,
                            int end,
                            double rho,
                            String spot){
        this.title = title;
        this.id = id;
        this.start = start;
        this.end = end;
        this.rho = rho;
        this.spot = spot;
    }

    public String getTitle(){return title;}
    public int getId(){return id;}
    public int getStart(){return start;}
    public int getEnd(){return end;}
    public double getRho(){return rho;}
    public String getSpot(){return spot;}
}
