package controllers;

import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import models.Book;
import play.mvc.Security;
import utils.*;

import javax.inject.Inject;
import java.util.List;

import static play.libs.Json.toJson;

public class Books extends Controller {

    @Inject
    play.data.FormFactory formFactory;

    @Inject
    play.db.Database library;

    @Security.Authenticated(Secured.class)
    public Result view(int id) {
        BookInfo bookInfo;

        try {
            Librarian librarian = new Librarian(library);

            bookInfo = librarian.getBookInfo(id);
        } catch (IllegalArgumentException e) {
            return redirect(routes.Application.notFound404());
        }

        return ok(views.html.books.view.render(bookInfo));
    }

    @Security.Authenticated(Secured.class)
    public Result renderSearch() {
        return ok(views.html.books.search.render());
    }

    public Result search() {
        int id;

        try {
            DynamicForm form = formFactory.form().bindFromRequest();
            String title = form.get("title");

            Librarian librarian = new Librarian(library);
            id = librarian.identifyBook(title);
        } catch (IllegalArgumentException e) {
            flash("message", e.getMessage());
            return redirect(routes.Books.renderSearch());
        }

        return redirect(routes.Books.view(id));
    }

    @Security.Authenticated(Secured.class)
    public Result renderAdd() {
        return ok(views.html.books.add.render());
    }

    public Result add() {
        int id;

        try {
            Form<Book> bookForm = formFactory.form(Book.class);
            Book book = bookForm.bindFromRequest().get();

            Librarian librarian = new Librarian(library);

            librarian.addBook(book);

            id = librarian.identifyBook(book.getTitle());
        } catch (IllegalArgumentException e) {
            flash("message", e.getMessage());
            return redirect(routes.Books.add());
        }

        return redirect(routes.Books.view(id));
    }

    public Result delete() {
        int id = -1;
        boolean allDeleted = false;

        try {
            DynamicForm form = formFactory.form().bindFromRequest();
            id = Integer.parseInt(form.get("id"));
            int amount = Integer.parseInt(form.get("amount"));

            Librarian librarian = new Librarian(library);

            allDeleted = librarian.deleteBook(id, amount);

            if (!allDeleted) {
                return redirect(routes.Books.view(id));
            }
        } catch (IllegalArgumentException e) {
            flash("message", e.getMessage());
            return redirect(routes.Books.view(id));
        }

        return redirect(routes.Application.index());
    }

    public Result allJson() {
        Librarian librarian = new Librarian(library);

        List<Book> books = librarian.listBooks();

        return ok(toJson(books));
    }

    public Result topJson() {
        Librarian librarian = new Librarian(library);

        List<BookInfo> booksInfo = librarian.getTopBooksInfo(10);

        return ok(toJson(booksInfo));
    }

    @Security.Authenticated(Secured.class)
    public Result top() {
        return ok(views.html.books.top.render());
    }

    public Result withClient(int id) {
        Librarian librarian = new Librarian(library);

        List<BookInfo> booksInfo = librarian.getClientsBooksInfo(id);

        return ok(toJson(booksInfo));
    }

    public Result avaliable(int id) {
        Librarian librarian = new Librarian(library);

        List<BookInfo>  booksInfo = librarian.getAvaliableBooksInfo(id);

        return ok(toJson(booksInfo));
    }

}
