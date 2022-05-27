package hvpaiva.flow;

import java.util.function.Function;
import java.util.function.Predicate;

public interface Workflow<T> {

    <R> Workflow<R> step(Function<? extends T, ? super R> function);

    <R> Workflow<R> binary(
        Predicate<? extends T> predicate,
        Function<? extends T, ? super R> trueFunction,
        Function<? extends T, ? super R> falseFunction
    );
    
}
