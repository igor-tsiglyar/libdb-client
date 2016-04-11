package utils;

import models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import play.db.*;
import javax.inject.Inject;


public class Librarian {

    Connection conn;

    @Inject Database db;
    public Librarian() throws SQLException {
        conn = DB.getConnection();
        conn.setAutoCommit(false);
    }

    public void giveBook(int bookId, int clientId, Date dateBorrowed) {
        String query = "INSERT INTO JOURNAL (BOOK_ID, CLIENT_ID, DATE_BORROWED, DATE_TO_RETURN, RETURN_DATE)" +
                       "VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setInt(1, bookId);
            ps.setInt(2, clientId);
            ps.setDate(3, dateBorrowed);
            String getBorrowingTimeDaysQuery = "SELECT BORROWING_TIME_DAYS " +
                                               "FROM BOOK_TYPES JOIN BOOKS ON BOOK_TYPES.ID = BOOKS.TYPE_ID " +
                                               "WHERE BOOKS.ID = ?";
            PreparedStatement getBorrowingTimeDaysPS = conn.prepareStatement(getBorrowingTimeDaysQuery);
            getBorrowingTimeDaysPS.setInt(1, bookId);
            ResultSet rs = getBorrowingTimeDaysPS.executeQuery();
            rs.next();
            int borrowingTimeDays = rs.getInt("BORROWING_TIME_DAYS");
            Date dateToReturn = Utils.advanceDate(dateBorrowed, borrowingTimeDays);
            ps.setDate(4, dateToReturn);

            ps.execute();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void takeBook(int bookId, int clientId, Date dateBorrowed, Date returnDate) {
        String query = "UPDATE JOURNAL " +
                       "SET RETURN_DATE = ? " +
                       "WHERE ID = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(query);

            String getJournalRecordId = "SELECT ID " +
                                        "FROM JOURNAL " +
                                        "WHERE BOOK_ID = ? AND CLIENT_ID = ? AND DATE_BORROWED = ?";
            PreparedStatement getJournalRecordIdPS = conn.prepareStatement(getJournalRecordId);
            getJournalRecordIdPS.setInt(1, bookId);
            getJournalRecordIdPS.setInt(2, clientId);
            getJournalRecordIdPS.setDate(3, dateBorrowed);
            ResultSet rs = getJournalRecordIdPS.executeQuery();
            rs.next();
            int journalRecordId = rs.getInt("ID");

            ps.setDate(1, returnDate);
            ps.setInt(2, journalRecordId);

            ps.execute();
            conn.commit();
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
        }
    }

    public List<JournalRecord> listJournalRecords() {
        List<JournalRecord> journalRecords = new ArrayList<>();
        String query = "SELECT * " +
                       "FROM JOURNAL";

        try {
            PreparedStatement ps = conn.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                journalRecords.add(new JournalRecord(
                        rs.getInt("ID"),
                        rs.getInt("BOOK_ID"),
                        rs.getInt("CLIENT_ID"),
                        rs.getDate("DATE_BORROWED"),
                        rs.getDate("DATE_TO_RETURN"),
                        rs.getDate("RETURN_DATE")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return journalRecords;
    }

    public void addClient(Client client) throws SQLException {
        String query = "INSERT INTO CLIENTS (FIRST_NAME, LAST_NAME, PASSPORT_SER, PASSPORT_NUM) " +
                       "VALUES (?, ?, ?, ?)";

        PreparedStatement ps = conn.prepareStatement(query);

        ps.setString(1, client.getFirstName());
        ps.setString(2, client.getLastName());
        ps.setString(3, client.getPassportSer());
        ps.setString(4, client.getPassportNum());

        ps.execute();
        conn.commit();
    }

    public void deleteClient(Client client) throws SQLException {
        String query = "SELECT * " +
                       "FROM CLIENTS " +
                       "WHERE FIRST_NAME = ? AND LAST_NAME = ? AND PASSPORT_SER = ? AND PASSPORT_NUM = ?";

        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, client.getFirstName());
        ps.setString(2, client.getLastName());
        ps.setString(3, client.getPassportSer());
        ps.setString(4, client.getPassportNum());

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            query = "DELETE FROM CLIENTS " +
                    "WHERE FIRST_NAME = ? AND LAST_NAME = ? AND PASSPORT_SER = ? AND PASSPORT_NUM = ?";
            ps = conn.prepareStatement(query);
            ps.setString(1, client.getFirstName());
            ps.setString(2, client.getLastName());
            ps.setString(3, client.getPassportSer());
            ps.setString(4, client.getPassportNum());
            ps.execute();
            conn.commit();
        } else {
            throw new SQLException("No such client");
        }
    }

    public ClientInfo getClientInfo(int id) throws SQLException {
        String query = "SELECT * " +
                       "FROM CLIENTS " +
                       "WHERE ID = ?";

        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Client client = new Client(
                    rs.getString("FIRST_NAME"),
                    rs.getString("LAST_NAME"),
                    rs.getString("PASSPORT_SER"),
                    rs.getString("PASSPORT_NUM")
            );

            query = "SELECT COUNT(*) AS BOOKS_AMOUNT " +
                    "FROM JOURNAL " +
                    "WHERE ID = ? AND RETURN_DATE IS NULL";

            ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            rs = ps.executeQuery();
            rs.next();
            int booksAmount = rs.getInt("BOOKS_AMOUNT");

            query = "SELECT SUM(FINE) AS FINE_AMOUNT " +
                    "FROM (JOURNAL JOIN BOOKS ON JOURNAL.BOOK_ID = BOOKS.ID) JOIN " +
                          "BOOK_TYPES ON JOURNAL.BOOK_ID = BOOK_TYPES.ID " +
                    "WHERE CLIENT_ID = ?";

            ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            rs = ps.executeQuery();
            rs.next();
            int fineAmount = rs.getInt("FINE_AMOUNT");

            return new ClientInfo(client, id, fineAmount, booksAmount);
        } else {
            throw new SQLException("No such client");
        }
    }

    public int identifyClient(Client client) throws SQLException {
        String query = "SELECT ID " +
                       "FROM CLIENTS " +
                       "WHERE FIRST_NAME = ? AND LAST_NAME = ? AND PASSPORT_SER = ? AND PASSPORT_NUM = ?";

        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, client.getFirstName());
        ps.setString(2, client.getLastName());
        ps.setString(3, client.getPassportSer());
        ps.setString(4, client.getPassportNum());

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            int id = rs.getInt("ID");

            return id;
        } else {
            throw new SQLException("No such client");
        }
    }

    public List<Client> listClients() {
        List<Client> clients = new ArrayList<>();
        String query = "SELECT * " +
                       "FROM CLIENTS";

        try {
            PreparedStatement ps = conn.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                clients.add(new Client(
                        rs.getString("FIRST_NAME"),
                        rs.getString("LAST_NAME"),
                        rs.getString("PASSPORT_SER"),
                        rs.getString("PASSPORT_NUM")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clients;
    }

    public void addBook(Book book) throws SQLException {
        String query = "INSERT INTO BOOKS (TITLE, AMOUNT, TYPE_ID) " +
                       "VALUES (?, ?, ?)";

        PreparedStatement ps = conn.prepareStatement(query);

        ps.setString(1, book.getTitle());
        ps.setInt(2, book.getAmount());
        ps.setInt(3, book.getTypeId());

        ps.execute();
        conn.commit();
    }

    public void deleteBook(Book book) throws SQLException {
        String query = "SELECT AMOUNT " +
                       "FROM BOOKS " +
                       "WHERE TITLE = ?";
        PreparedStatement ps = conn.prepareStatement(query);

        ps.setString(1, book.getTitle());
        ResultSet rs = ps.executeQuery();
        rs.next();
        int booksAmount = rs.getInt("AMOUNT");
        if (book.getAmount() < booksAmount) {
            query = "UPDATE BOOKS " +
                    "SET AMOUNT = AMOUNT - ?";
            ps = conn.prepareStatement(query);
            ps.setInt(1, book.getAmount());
        } else if (book.getAmount() == booksAmount) {
            query = "DELETE FROM BOOKS " +
                    "WHERE TITLE = ?";
            ps = conn.prepareStatement(query);
            ps.setString(1, book.getTitle());
        } else {
            throw new SQLException("There are less books than requested to delete");
        }

        ps.execute();
        conn.commit();
    }

    public List<Book> listBooks(int amount) throws SQLException {
        List<Book> books = new ArrayList<>();
        String query;
        PreparedStatement ps;

        if (amount == 0) {
            query = "SELECT * " +
                    "FROM BOOKS";
            ps = conn.prepareStatement(query);
        } else {
            query = "WITH R AS ( " +
                        "SELECT * " +
                        "FROM BOOKS JOIN (SELECT BOOK_ID, COUNT(CLIENT_ID) AS TIMES_BORROWED " +
                                          "FROM JOURNAL " +
                                          "GROUP BY BOOK_ID " +
                                          "ORDER BY TIMES_BORROWED DESC) ON BOOK_ID = ID) " +
                    "SELECT * " +
                    "FROM R " +
                    "WHERE ROWNUM <= ?";
            ps = conn.prepareStatement(query);
            ps.setInt(1, amount);
        }

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            books.add(new Book(
                    rs.getString("TITLE"),
                    rs.getInt("AMOUNT"),
                    rs.getInt("TYPE_ID")
            ));
        }

        return books;
    }

}
