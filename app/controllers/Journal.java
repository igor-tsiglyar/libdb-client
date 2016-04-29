package controllers;

import models.JournalRecord;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;

import play.mvc.Security;
import utils.Librarian;

import java.util.List;

import javax.inject.Inject;

import static play.libs.Json.toJson;

public class Journal extends Controller {

    @Inject
    play.data.FormFactory formFactory;

    @Inject
    play.db.Database library;

    public Result add() {
        DynamicForm form = formFactory.form().bindFromRequest();

        int clientId = Integer.parseInt(form.get("clientId"));

        int bookId = Integer.parseInt(form.get("bookId"));

        Librarian librarian = new Librarian(library);

        librarian.giveBook(clientId, bookId);

        return redirect(routes.Clients.view(clientId));
    }

    public Result delete() {
        DynamicForm form = formFactory.form().bindFromRequest();

        int clientId = Integer.parseInt(form.get("clientId"));

        int bookId = Integer.parseInt(form.get("bookId"));

        Librarian librarian = new Librarian(library);

        librarian.takeBook(clientId, bookId);

        return redirect(routes.Clients.view(clientId));
    }

    @Security.Authenticated(Secured.class)
    public Result view() {
        return ok(views.html.journal.view.render());
    }

    public Result viewJson() {
        Librarian librarian = new Librarian(library);

        List<JournalRecord> journalRecords = librarian.listJournalRecords();

        return ok(toJson(journalRecords));
    }
}
