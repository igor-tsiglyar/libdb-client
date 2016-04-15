package utils;

import java.util.Date;

public class BookInfo {

    public int id;
    public String title;
    public int amount;
    public String kind;
    public Date dateToReturn;

    public BookInfo(int id, String title, int amount, String kind, Date dateToReturn) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.kind = kind;
        this.dateToReturn = dateToReturn;
    }

}
