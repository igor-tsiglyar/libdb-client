package controllers;

import play.data.Form;
import play.mvc.*;
import utils.Librarian;
import utils.Login;

import javax.inject.Inject;

@With(Session.class)
public class Application extends Controller {

    @Inject
    play.data.FormFactory formFactory;

    @Inject
    play.db.Database library;

    public Result index() {
        return ok(views.html.index.render());
    }

    public Result notFound404() {
        return notFound(views.html.notFound.render());
    }

    public Result login() {
        return ok(views.html.login.render());
    }

    public Result auth() {
        Form<Login> loginForm = formFactory.form(Login.class);
        Login login = loginForm.bindFromRequest().get();

        Librarian librarian = new Librarian(library);

        if (librarian.canSignIn(login)) {
            session().clear();
            session("email", login.email);
            return redirect(routes.Application.index());
        }

        flash("message", "Incorrect email or password");

        return redirect(routes.Application.login());
    }

    public Result logout() {
        session().clear();
        return redirect(routes.Application.index());
    }

}
