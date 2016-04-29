package controllers;

import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletionStage;

public class Session extends Action.Simple {

    public CompletionStage<Result> call(Http.Context context) {
        return delegate.call(context);
    }

    public static Boolean isLoggedOn() {
        return Http.Context.current().session().get("email") != null;
    }

    public static Boolean isAdmin() {
        return "admin@email.com".equals(Http.Context.current().session().get("email"));
    }

}