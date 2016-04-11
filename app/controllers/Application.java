package controllers;

import utils.*;
import play.data.Form;
import play.mvc.*;

import models.*;
import views.html.base;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import static play.libs.Json.toJson;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class Application extends Controller {

    @Inject play.data.FormFactory formFactory;

    public Result index() {
        return ok(views.html.index.render("Welcome to the library!"));
    }


    public Result clientGetHandler() {
        return ok(views.html.client.main.render());
    }

    public Result clientInfoGetHandler(int id) {
        ClientInfo clientInfo;

        try {
            Librarian librarian = new Librarian();

            clientInfo = librarian.getClientInfo(id);
        } catch (Exception e) {
            return notFound("Not found");
        }

        return ok(views.html.client.info.render(clientInfo));
    }

    public Result clientGetInfoGetHandler() {
        return ok(views.html.client.get.render());
    }

    public Result clientGetInfoPostHandler() {
        int id;

        try {
            Form<Client> clientForm = formFactory.form(Client.class);
            Client client = clientForm.bindFromRequest().get();

            Librarian librarian = new Librarian();

            id = librarian.identifyClient(client);
        } catch (Exception e) {
            flash("message", e.getMessage());
            return redirect(routes.Application.clientGetInfoPostHandler());
        }

        return redirect(routes.Application.clientInfoGetHandler(id));
    }

    public Result addClientGetHandler() {
        return ok(views.html.client.add.render());
    }

    public Result deleteClientGetHandler() {
        return ok(views.html.client.delete.render());
    }

    public Result addClientPostHandler() {
        try {
            Form<Client> clientForm = formFactory.form(Client.class);
            Client client = clientForm.bindFromRequest().get();

            Librarian librarian = new Librarian();

            librarian.addClient(client);
        } catch (Exception e) {
            flash("message", e.getMessage());
            return redirect(routes.Application.addClientGetHandler());
        }

        flash("message", "New main successfully added");
        return redirect(routes.Application.clientGetHandler());
    }

    public Result deleteClientPostHandler() {
        try {
            Form<Client> clientForm = formFactory.form(Client.class);
            Client client = clientForm.bindFromRequest().get();

            Librarian librarian = new Librarian();

            librarian.deleteClient(client);
        } catch (SQLException e) {
            flash("message", e.getMessage());
            return redirect(routes.Application.deleteClientGetHandler());
        }

        flash("message", "Client successfully deleted");
        return redirect(routes.Application.clientGetHandler());
    }


    public Result bookGetHandler() {
        return ok(views.html.book.main.render());
    }

    public Result addBookGetHandler() {
        return ok(views.html.book.add.render());
    }

    public Result deleteBookGetHandler() {
        return ok(views.html.book.delete.render());
    }

    public Result addBookPostHandler() {
        try {
            Form<Book> bookForm = formFactory.form(Book.class);
            Book book = bookForm.bindFromRequest().get();

            Librarian librarian = new Librarian();

            librarian.addBook(book);
        } catch (Exception e) {
            flash("message", e.getMessage());
            return redirect(routes.Application.addBookGetHandler());
        }

        flash("message", "New book successfully added");
        return redirect(routes.Application.bookGetHandler());
    }

    public Result deleteBookPostHandler() {
        try {
            Form<Book> bookForm = formFactory.form(Book.class);
            Book book = bookForm.bindFromRequest().get();

            Librarian librarian = new Librarian();

            librarian.deleteBook(book);
        } catch (Exception e) {
            flash("message", e.getMessage());
            return redirect(routes.Application.deleteBookGetHandler());
        }

        flash("message", "Book successfully deleted");
        return redirect(routes.Application.bookGetHandler());
    }

    public Result booksJsonGetHandler() {
        List<Book> books = null;

        try {
            Librarian librarian = new Librarian();

            books = librarian.listBooks(0);
        } catch (Exception e) {
            flash("message", e.getMessage());
            return redirect(routes.Application.index());
        }

        return ok(toJson(books));
    }

    public Result booksGetHandler() {
        return ok(views.html.books.main.render());
    }

    public Result topBooksJsonGetHandler() {
        List<Book> books = null;

        try {
            Librarian librarian = new Librarian();

            books = librarian.listBooks(10);
        } catch (Exception e) {
            flash("message", e.getMessage());
            return redirect(routes.Application.index());
        }

        return ok(toJson(books));
    }

    public Result topBooksGetHandler() {
        return ok(views.html.books.top.render());
    }

}
