package org.auth.api.application;

public abstract class UseCase<IN, OUT> {
    public abstract OUT execute(IN input);
}
