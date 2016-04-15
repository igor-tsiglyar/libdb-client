package controllers;

import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;

import utils.Librarian;

import javax.inject.Inject;

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
}
