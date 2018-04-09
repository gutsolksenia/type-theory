grammar Lambda;

@header{
import ru.itmo.gutsol.tt.common.LambdaStub;
import ru.itmo.gutsol.tt.lambda.Lambda;
import ru.itmo.gutsol.tt.common.StubGenerator;
import java.util.Collections;
}

let_expression returns[LambdaStub ret]
    : LET VAR EQ def=let_expression IN expr=let_expression
        { $ret = StubGenerator.let($VAR.text, $def.ret, $expr.ret);}
    | expression { $ret = $expression.ret;}
    ;

expression returns[LambdaStub ret]
    : application abstraction  { $ret = StubGenerator.application($application.ret, $abstraction.ret); }
    | application { $ret = $application.ret; }
    | abstraction { $ret = $abstraction.ret; }
    ;

abstraction returns[LambdaStub ret] locals [List<String> variables]
    : LAMBDA
        { $variables = new ArrayList<>(1); }
    ( VAR
        { $variables.add($VAR.text); }
    )+
    DOT expression
        { $ret = $expression.ret; Collections.reverse($variables); for(String variable: $variables) { $ret = StubGenerator.abstraction(variable, $ret); }}
    ;

application returns[LambdaStub ret]
    : app=application atomic { $ret = StubGenerator.application($app.ret, $atomic.ret); }
    | atomic { $ret = $atomic.ret; }
    ;

atomic returns[LambdaStub ret]
    : OBR expression CBR { $ret = $expression.ret; }
    | VAR { $ret = StubGenerator.variable($VAR.text); }
    ;


LET : 'let';
IN : 'in';
EQ : '=';
CBR : ')';
OBR : '(';
LAMBDA : '\\';
DOT : '.';
VAR :  [a-zA-Z] [a-zA-Z0-9\']*;
WS : (' ' | '\t' | '\n')+ -> skip;