package controllers;

import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import models.Client;
import play.mvc.Security;
import utils.*;

import javax.inject.Inject;

public class Clients extends Controller {

    @Inject
    play.data.FormFactory formFactory;

    @Inject
    play.db.Database library;

    @Security.Authenticated(Secured.class)
    public Result view(int id) {
        ClientInfo clientInfo;

        try {
            Librarian librarian = new Librarian(library);

            clientInfo = librarian.getClientInfo(id);
        } catch (IllegalArgumentException e) {
            return redirect(routes.Application.notFound404());
        }

        return ok(views.html.clients.view.render(clientInfo));
    }

    @Security.Authenticated(Secured.class)
    public Result renderSearch() {
        return ok(views.html.clients.search.render());
    }

    public Result search() {
        int id;

        try {
            Form<Client> clientForm = formFactory.form(Client.class);
            Client client = clientForm.bindFromRequest().get();

            Librarian librarian = new Librarian(library);

            id = librarian.identifyClient(client);
        } catch (IllegalArgumentException e) {
            flash("message", e.getMessage());
            return redirect(routes.Clients.renderSearch());
        }

        return redirect(routes.Clients.view(id));
    }

    @Security.Authenticated(Secured.class)
    public Result renderAdd() {
        return ok(views.html.clients.add.render());
    }

    public Result add() {
        int id;

        try {
            Form<Client> clientForm = formFactory.form(Client.class);
            Client client = clientForm.bindFromRequest().get();

            Librarian librarian = new Librarian(library);

            librarian.addClient(client);

            id = librarian.identifyClient(client);

        } catch (IllegalArgumentException e) {
            flash("message", e.getMessage());
            return redirect(routes.Clients.renderAdd());
        }

        return redirect(routes.Clients.view(id));
    }

    public Result delete() {
        int id = -1;

        try {
            DynamicForm form = formFactory.form().bindFromRequest();
            id = Integer.parseInt(form.get("id"));

            Librarian librarian = new Librarian(library);

            librarian.deleteClient(id);
        } catch (IllegalArgumentException e) {
            flash("message", e.getMessage());
            if (id > 0) {
                return redirect(routes.Clients.view(id));
            }
        }

        return redirect(routes.Application.index());
    }
}
