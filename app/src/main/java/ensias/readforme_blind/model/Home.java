package ensias.readforme_blind.model;

import java.util.ArrayList;

public class Home {
    public final static int PRE_START_PAGE=0;
    private int total;
    private int per_page;
    private int current_page;
    private int last_page;
    private String first_page_url;
    private String last_page_url;
    private String next_page_url;
    private String prev_page_url;
    private String path;
    private int  from;
    private int  to;
    private ArrayList<File> data;


    public Home() {
       data=new ArrayList<>();
        current_page=PRE_START_PAGE;
        last_page=1;
    }

    public int getNextPage(){
        return current_page+1<=last_page?current_page+1:current_page;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPer_page() {
        return per_page;
    }

    public void setPer_page(int per_page) {
        this.per_page = per_page;
    }

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public int getLast_page() {
        return last_page;
    }

    public void setLast_page(int last_page) {
        this.last_page = last_page;
    }

    public String getFirst_page_url() {
        return first_page_url;
    }

    public void setFirst_page_url(String first_page_url) {
        this.first_page_url = first_page_url;
    }

    public String getLast_page_url() {
        return last_page_url;
    }

    public void setLast_page_url(String last_page_url) {
        this.last_page_url = last_page_url;
    }

    public String getNext_page_url() {
        return next_page_url;
    }

    public void setNext_page_url(String next_page_url) {
        this.next_page_url = next_page_url;
    }

    public String getPrev_page_url() {
        return prev_page_url;
    }

    public void setPrev_page_url(String prev_page_url) {
        this.prev_page_url = prev_page_url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public ArrayList<File> getData() {
        return data;
    }

    public void setData(ArrayList<File> data) {
        this.data = data;
    }
}
