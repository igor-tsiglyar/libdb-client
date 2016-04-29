package controllers;

import org.apache.commons.codec.binary.Base64;
import play.data.Form;
import play.libs.F.Tuple;
import play.mvc.*;
import utils.Librarian;
import utils.Login;
import utils.Sha;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

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

    public Result register() {
        return ok(views.html.register.render());
    }

    public Result addUser() {
        Form<Login> loginForm = formFactory.form(Login.class);
        Login login = loginForm.bindFromRequest().get();

        Librarian librarian = new Librarian(library);

        try {
            SecureRandom random = new SecureRandom();
            byte bytes[] = new byte[64];
            random.nextBytes(bytes);

            String salt = Base64.encodeBase64String(bytes).substring(0, 64);
            String hash = Sha.hash256(salt + login.password);

            librarian.addUser(login.email, hash, salt);

        } catch (Exception e) {
            e.printStackTrace();
            return redirect(routes.Application.register());
        }

        return redirect(routes.Application.index());
    }

    public Result login() {
        return ok(views.html.login.render());
    }

    public Result auth() {
        Form<Login> loginForm = formFactory.form(Login.class);
        Login login = loginForm.bindFromRequest().get();

        Librarian librarian = new Librarian(library);

        Tuple<String, String> hashAndSalt = librarian.getHashAndSalt(login.email);
        if (hashAndSalt != null) {
            try {
                String hash = Sha.hash256(hashAndSalt._2 + login.password);
                if (hash.equals(hashAndSalt._1)) {
                    session().clear();
                    session("email", login.email);
                    return redirect(routes.Application.index());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        flash("message", "Incorrect email or password");

        return redirect(routes.Application.login());
    }

    public Result logout() {
        session().clear();
        return redirect(routes.Application.index());
    }

}
