package models;

public class Book {

    private String title;
    private int amount;
    private int typeId;

    public Book() {}

    public Book(String title, int amount, int typeId) {
        this.title = title;
        this.amount = amount;
        this.typeId = typeId;
    }

    public String getTitle() {
        return title;
    }

    public int getAmount() {
        return amount;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

}
