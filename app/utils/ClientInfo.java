package utils;

import models.Client;

public class ClientInfo {

    public String firstName;
    public String lastName;
    public String passportSer;
    public String passportNum;
    public int id;
    public int fineAmount;
    public int booksAmount;


    public ClientInfo(Client client, int id, int fineAmount, int booksAmount) {
        this.firstName = client.getFirstName();
        this.lastName = client.getLastName();
        this.passportSer = client.getPassportSer();
        this.passportNum = client.getPassportNum();
        this.id = id;
        this.fineAmount = fineAmount;
        this.booksAmount = booksAmount;
    }

}
