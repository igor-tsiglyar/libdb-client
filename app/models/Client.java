package models;

public class Client {

    private String firstName;
    private String lastName;
    private String passportSer;
    private String passportNum;

    public Client() {}

    public Client(String firstName, String lastName, String passportSer, String passportNum) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.passportSer = passportSer;
        this.passportNum = passportNum;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassportSer() {
        return passportSer;
    }

    public String getPassportNum() {
        return passportNum;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPassportSer(String passportSer) {
        this.passportSer = passportSer;
    }

    public void setPassportNum(String passportNum) {
        this.passportNum = passportNum;
    }

}
