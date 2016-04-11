package models;

import java.util.Date;

public class JournalRecord {

    private int id;
    private int bookId;
    private int clientId;
    private Date dateBorrowed;
    private Date dateToReturn;
    private Date returnDate;

    public JournalRecord(int id, int bookId, int clientId, Date dateBorrowed, Date dateToReturn, Date returnDate) {
        this.id = id;
        this.bookId = bookId;
        this.clientId = clientId;
        this.dateBorrowed = dateBorrowed;
        this.dateToReturn = dateToReturn;
        this.returnDate = returnDate;
    }

    public int getId() {
        return id;
    }

    public int getBookId() {
        return bookId;
    }

    public int getClientId() {
        return clientId;
    }

    public Date getDateBorrowed() {
        return dateBorrowed;
    }

    public Date getDateToReturn() {
        return dateToReturn;
    }

    public Date getReturnDate() {
        return returnDate;
    }

}
