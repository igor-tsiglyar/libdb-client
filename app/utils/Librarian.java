package utils;

import models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import play.db.Database;


public class Librarian {

    Database library;

    public Librarian(Database library) {
        this.library = library;
    }

    public List<JournalRecord> listJournalRecords() {
        List<JournalRecord> journalRecords = new ArrayList<>();

        try (Connection conn = library.getConnection()) {
            String query = "SELECT * " +
                           "FROM JOURNAL";

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

    public void addClient(Client client) {
        try (Connection conn = library.getConnection()) {
            String query = "INSERT INTO CLIENTS (FIRST_NAME, LAST_NAME, PASSPORT_SER, PASSPORT_NUM) " +
                           "VALUES (?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, client.getFirstName());
            ps.setString(2, client.getLastName());
            ps.setString(3, client.getPassportSer());
            ps.setString(4, client.getPassportNum());

            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteClient(int id) {
        try (Connection conn = library.getConnection()) {
            String query = "DELETE FROM JOURNAL " +
                           "WHERE CLIENT_ID = ?";

            PreparedStatement ps = conn.prepareStatement(query);

            ps.setInt(1, id);

            ps.execute();

            query = "DELETE FROM CLIENTS " +
                    "WHERE ID = ?";

            ps = conn.prepareStatement(query);

            ps.setInt(1, id);

            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ClientInfo getClientInfo(int id) throws IllegalArgumentException {
        Client client = null;
        int booksAmount = -1;
        int fineAmount = -1;

        if (library == null) {
            System.out.println("No database");
        }

        try (Connection conn = library.getConnection()) {
            String query = "SELECT * " +
                           "FROM CLIENTS " +
                           "WHERE ID = ?";

            PreparedStatement ps = conn.prepareStatement(query);

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                client = new Client(
                        rs.getString("FIRST_NAME"),
                        rs.getString("LAST_NAME"),
                        rs.getString("PASSPORT_SER"),
                        rs.getString("PASSPORT_NUM")
                );

                query = "SELECT COUNT(*) AS BOOKS_AMOUNT " +
                        "FROM JOURNAL " +
                        "WHERE CLIENT_ID = ? AND RETURN_DATE IS NULL";

                ps = conn.prepareStatement(query);

                ps.setInt(1, id);

                rs = ps.executeQuery();

                if (rs.next()) {
                    booksAmount = rs.getInt("BOOKS_AMOUNT");
                }

                query = "SELECT SUM(FINE * (EXTRACT (DAY FROM (RETURN_DATE - DATE_TO_RETURN)))) AS FINE_AMOUNT " +
                        "FROM (JOURNAL JOIN BOOKS ON JOURNAL.BOOK_ID = BOOKS.ID) JOIN " +
                        "BOOK_TYPES ON BOOKS.TYPE_ID = BOOK_TYPES.ID " +
                        "WHERE CLIENT_ID = ? AND RETURN_DATE > DATE_TO_RETURN";

                ps = conn.prepareStatement(query);

                ps.setInt(1, id);

                rs = ps.executeQuery();

                if (rs.next()) {
                    fineAmount = rs.getInt("FINE_AMOUNT");
                }

            } else {
                throw new IllegalArgumentException("No such client");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ClientInfo(client, id, fineAmount, booksAmount);
    }

    public int identifyClient(Client client) throws IllegalArgumentException {
        int id = -1;

        try (Connection conn = library.getConnection()) {
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
                id = rs.getInt("ID");
            } else {
                throw new IllegalArgumentException("No such client");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }

    public List<Client> listClients()  {
        List<Client> clients = new ArrayList<>();

        try (Connection conn = library.getConnection()) {
            String query = "SELECT * " +
                           "FROM CLIENTS";

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

    public void addBook(Book book) {
        try (Connection conn = library.getConnection()) {
            String query = "INSERT INTO BOOKS (TITLE, AMOUNT, TYPE_ID) " +
                           "VALUES (?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, book.getTitle());
            ps.setInt(2, book.getAmount());
            ps.setInt(3, book.getTypeId());

            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean deleteBook(int id, int amount) throws IllegalArgumentException {
        boolean allDeleted = false;

        try (Connection conn = library.getConnection()) {
            String query = "SELECT AMOUNT " +
                           "FROM BOOKS " +
                           "WHERE ID = ?";

            PreparedStatement ps = conn.prepareStatement(query);

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int booksAmount = rs.getInt("AMOUNT");

                if (amount < booksAmount) {
                    query = "UPDATE BOOKS " +
                            "SET AMOUNT = AMOUNT - ? " +
                            "WHERE ID = ?";

                    ps = conn.prepareStatement(query);

                    ps.setInt(1, amount);
                    ps.setInt(2, id);
                } else if (amount == booksAmount) {
                    query = "DELETE FROM BOOKS " +
                            "WHERE ID = ?";

                    ps = conn.prepareStatement(query);

                    ps.setInt(1, id);

                    allDeleted = true;
                } else {
                    throw new IllegalArgumentException("There are less books than requested to delete");
                }

                ps.execute();
            } else {
                throw new IllegalArgumentException("No such book");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return allDeleted;
    }

    public int identifyBook(String title) throws IllegalArgumentException {
        int id = -1;

        try (Connection conn = library.getConnection()) {
            String query = "SELECT ID " +
                           "FROM BOOKS " +
                           "WHERE TITLE = ?";

            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, title);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                id = rs.getInt("ID");
            } else {
                throw new IllegalArgumentException("No such book");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }

    public BookInfo getBookInfo(int id) throws IllegalArgumentException {
        BookInfo bookInfo = null;

        try (Connection conn = library.getConnection()) {
            String query = "SELECT * " +
                           "FROM BOOKS JOIN BOOK_TYPES ON BOOK_TYPES.ID = BOOKS.TYPE_ID " +
                           "WHERE BOOKS.ID = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                bookInfo = new BookInfo(
                        rs.getInt("ID"),
                        rs.getString("TITLE"),
                        rs.getInt("AMOUNT"),
                        rs.getString("NAME"),
                        null
                );
            } else {
                throw new IllegalArgumentException("No such book");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookInfo;
    }

    public List<Book> listBooks() {
        List<Book> books = new ArrayList<>();

        try (Connection conn = library.getConnection()) {
            String query;
            PreparedStatement ps;

            query = "SELECT * " +
                    "FROM BOOKS";

            ps = conn.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                books.add(new Book(
                        rs.getString("TITLE"),
                        rs.getInt("AMOUNT"),
                        rs.getInt("TYPE_ID")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }


    public List<BookInfo> getTopBooksInfo(int size) {
        List<BookInfo> booksInfo = new ArrayList<BookInfo>();

        try (Connection conn = library.getConnection()) {
            String query = "SELECT * " +
                           "FROM (BOOKS JOIN (SELECT BOOK_ID, COUNT(CLIENT_ID) AS TIMES_BORROWED " +
                                             "FROM JOURNAL " +
                                             "GROUP BY BOOK_ID " +
                                             "ORDER BY TIMES_BORROWED DESC) ON BOOK_ID = ID) " +
                                 "JOIN BOOK_TYPES ON BOOK_TYPES.ID = BOOKS.TYPE_ID " +
                           "WHERE ROWNUM <= ?";

            PreparedStatement ps = conn.prepareStatement(query);

            ps.setInt(1, size);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                booksInfo.add(new BookInfo(
                        rs.getInt("ID"),
                        rs.getString("TITLE"),
                        rs.getInt("AMOUNT"),
                        rs.getString("NAME"),
                        null
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return booksInfo;
    }


    public List<BookInfo> getClientsBooksInfo(int id) {
        List<BookInfo> booksInfo = new ArrayList<>();

        try (Connection conn = library.getConnection()) {
            String query = "SELECT * " +
                           "FROM BOOKS JOIN JOURNAL ON BOOKS.ID = JOURNAL.BOOK_ID " +
                           "WHERE CLIENT_ID = ? AND RETURN_DATE IS NULL ";

            PreparedStatement ps = conn.prepareStatement(query);

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                booksInfo.add(new BookInfo(
                        rs.getInt("BOOK_ID"),
                        rs.getString("TITLE"),
                        0,
                        null,
                        rs.getDate("DATE_TO_RETURN")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return booksInfo;
    }

    public List<BookInfo> getAvaliableBooksInfo(int id) {
        List<BookInfo> booksInfo = new ArrayList<>();

        try (Connection conn = library.getConnection()) {
            String query = "SELECT *" +
                           "FROM BOOKS " +
                           "WHERE ID NOT IN (SELECT BOOK_ID " +
                                             "FROM BOOKS JOIN JOURNAL ON BOOKS.ID = JOURNAL.BOOK_ID " +
                                             "WHERE CLIENT_ID = ? AND RETURN_DATE IS NULL) AND AMOUNT > 0";

            PreparedStatement ps = conn.prepareStatement(query);

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                booksInfo.add(new BookInfo(
                        rs.getInt("ID"),
                        rs.getString("TITLE"),
                        0,
                        null,
                        null
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return booksInfo;
    }

    public void takeBook(int clientId, int bookId) {
        try (Connection conn = library.getConnection(false)) {
            String query = "UPDATE JOURNAL " +
                           "SET RETURN_DATE = ?" +
                           "WHERE CLIENT_ID = ? AND BOOK_ID = ? ";

            PreparedStatement ps = conn.prepareStatement(query);

            ps.setDate(1, Utils.todayDate());
            ps.setInt(2, clientId);
            ps.setInt(3, bookId);

            ps.execute();

            query = "UPDATE BOOKS " +
                    "SET AMOUNT = AMOUNT + 1 " +
                    "WHERE ID = ?";

            ps = conn.prepareStatement(query);

            ps.setInt(1, bookId);

            ps.execute();

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void giveBook(int clientId, int bookId) {
        try (Connection conn = library.getConnection(false)) {
            String query = "SELECT BORROWING_TIME_DAYS " +
                           "FROM BOOK_TYPES JOIN BOOKS ON BOOK_TYPES.ID = BOOKS.TYPE_ID " +
                           "WHERE BOOKS.ID = ?";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setInt(1, bookId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int days = rs.getInt("BORROWING_TIME_DAYS");

                query = "INSERT INTO JOURNAL (BOOK_ID, CLIENT_ID, DATE_BORROWED, DATE_TO_RETURN) " +
                        "VALUES (?, ?, ?, ?)";

                ps = conn.prepareStatement(query);

                ps.setInt(1, bookId);
                ps.setInt(2, clientId);
                ps.setDate(3, Utils.todayDate());
                ps.setDate(4, Utils.advanceDate(Utils.todayDate(), days));

                ps.execute();

                query = "UPDATE BOOKS " +
                        "SET AMOUNT = AMOUNT - 1 " +
                        "WHERE ID = ?";

                ps = conn.prepareStatement(query);

                ps.setInt(1, bookId);

                ps.execute();

                conn.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean canSignIn(Login login) {
        try (Connection conn = library.getConnection()) {
            String query = "SELECT * " +
                           "FROM USERS " +
                           "WHERE EMAIL = ? AND PASSWORD = ?";
            PreparedStatement ps = conn.prepareStatement(query);;

            ps.setString(1, login.email);
            ps.setString(2, login.password);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

}
