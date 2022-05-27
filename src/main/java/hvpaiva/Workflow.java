package hvpaiva;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class Workflow<T> {
    private static final Workflow<?> EMPTY = new Workflow<>(null);
    private final T value;

    private Workflow(T value) {
        this.value = value;
    }

    public static <T> Workflow<T> ofNullable(T value) {
        return value == null
                ? empty()
                : new Workflow<>(value);
    }

    public static <T> Workflow<T> of(T value) {
        return new Workflow<>(Objects.requireNonNull(value));
    }

    @SuppressWarnings("unchecked")
    public static <T> Workflow<T> empty() {
        return (Workflow<T>) EMPTY;
    }

    <U> Workflow<U> step(Function<? super T, ? extends U> function) {
        if (isEmpty())
            return empty();

        return Workflow.ofNullable(function.apply(value));
    }

    public <U> Workflow<U> flatStep(Function<? super T, ? extends Workflow<? extends U>> function) {
        if (isEmpty())
            return empty();

        @SuppressWarnings("unchecked")
        var r = (Workflow<U>) function.apply(value);
        return Objects.requireNonNull(r);
    }

    public <U> Workflow<U> binary(
            Predicate<? super T> predicate,
            Function<? super T, ? extends U> trueAction,
            Function<? super T, ? extends U> falseAction
    ) {
        if (isEmpty()) {
            return empty();
        } else {
            return predicate.test(value)
                    ? Workflow.ofNullable(trueAction.apply(value))
                    : Workflow.ofNullable(falseAction.apply(value));
        }
    }

    public <U, X extends Throwable> Workflow<U> binaryOrThrow(
            Predicate<? super T> predicate,
            Function<? super T, ? extends U> trueAction,
            Supplier<? extends X> exceptionSupplier
    ) throws X {
        if (isEmpty())
            return empty();

        if (predicate.test(value)) {
            return Workflow.ofNullable(trueAction.apply(value));
        }

        throw exceptionSupplier.get();
    }


    public void then(Consumer<? super T> action) {
        if (isPresent()) {
            action.accept(value);
        }
    }

    public Optional<T> thenReturn() {
        return Optional.ofNullable(value);
    }

    public Stream<T> stream() {
        return Stream.ofNullable(value);
    }

    private boolean isPresent() {
        return !isEmpty();
    }

    private boolean isEmpty() {
        return value == null;
    }
}
