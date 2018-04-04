package ru.itmo.gutsol.tt.common;

import ru.itmo.gutsol.tt.lambda.Lambda;

public interface LambdaStub {
    Lambda resolve(Scope scope);
}
