package hvpaiva;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

class WorkflowTest {

    UnaryOperator<Integer> plusOne = x -> x + 1;
    UnaryOperator<Integer> square = x -> x * x;
    UnaryOperator<Integer> cube = x -> x * x * x;
    UnaryOperator<Integer> doubleIt = x -> x * 2;
    UnaryOperator<Integer> half = x -> x / 2;
    UnaryOperator<Integer> negate = x -> -x;
    UnaryOperator<Integer> abs = Math::abs;
    UnaryOperator<List<Integer>> plusOneList = x -> x.stream().map(plusOne).toList();
    UnaryOperator<Integer> minusOne = x -> x - 1;
    Predicate<Integer> isOdd = x -> x % 2 == 1;
    Predicate<Integer> isEven = x -> x % 2 == 0;
    UnaryOperator<List<Integer>> nullify = x -> (List<Integer>) null;

    @Test
    void test() {
        var v = Workflow.ofNullable(List.of(1, 2))
                .step(plusOneList)
                .step(nullify)
                .thenReturn()
                .orElseGet(Collections::emptyList);
        System.out.println(v);
        Assertions.assertEquals(Collections.emptyList(), v);

        Workflow.of(10)
                .step(doubleIt.andThen(minusOne))
                .binary(isEven, negate, half)
                .step(square)
                .step(cube)
                .binaryOrThrow(isOdd, abs, RuntimeException::new)
                .then(System.out::println);


        var flow = Workflow.of("HIGH");

        Workflow.of(flow)
                .flatStep(x -> x.step(String::toLowerCase))
                .then(System.out::println);
    }
}