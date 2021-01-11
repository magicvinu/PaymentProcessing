package com.examples.handle.checked.exceptions;

import lombok.SneakyThrows;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class UseThrowingFunction {

        public static void main(String[] args) {

            //Refer below blog
            //http://victorrentea.ro/blog/exceptions-and-streams/
            usingThrowingFunctionalInterfaceToHandleCheckedExceptionWithLamdaWithoutThrowingExceptions();
            usingThrowingFunctionalInterfaceToHandleCheckedExceptionWithLamda();
            usingLombokSneakyThrowToHandleCheckedExceptionWithLamda();
            traditionalWayToHandleCheckedExceptionWithLamda();

        }

        private static void traditionalWayToHandleCheckedExceptionWithLamda() {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            List<String> dateList = asList("2020-10-11", "2020-nov-12", "2020-12-01");
            List<Date> dates = dateList.stream().map(s -> {
                try {
                    return format.parse(s);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }).collect(toList());

            System.out.println("traditionalWayToHandleCheckedExceptionWithLamda" + dates);

        }

        private static void usingThrowingFunctionalInterfaceToHandleCheckedExceptionWithLamda() {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            List<String> dateList = asList("2020-10-11", "2020-nov-12", "2020-12-01");
            ThrowingFunction<String, Date, ParseException> throwingFunction = format::parse;
            Function<String, Date> wrappedThrowingFunction = Unchecked.applyOrThrow(throwingFunction);

            List<Date> dates = dateList.stream().map(wrappedThrowingFunction).collect(toList());
            System.out.println("traditionalWayToHandleCheckedExceptionWithLamda" + dates);
        }

    private static void usingThrowingFunctionalInterfaceToHandleCheckedExceptionWithLamdaWithoutThrowingExceptions() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        List<String> dateList = asList("2020-10-11", "2020-nov-12", "2020-12-01");
        ThrowingFunction<String, Optional<Date>, ParseException> throwingFunction = source -> Optional.ofNullable(format.parse(source));
        Function<String, Optional<Date>> defaultFunction = (s) -> Optional.empty();
        Function<String, Optional<Date>> wrappedThrowingFunctionWithoutRethrow = Unchecked.apply(throwingFunction, defaultFunction);
        List<Optional<Date>> dates = dateList.stream().map(wrappedThrowingFunctionWithoutRethrow).collect(toList());
        System.out.println("usingThrowingFunctionalInterfaceToHandleCheckedExceptionWithLamdaWithoutThrowingExceptions" + dates);
    }

    private static void usingLombokSneakyThrowToHandleCheckedExceptionWithLamda() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        List<String> dateList = asList("2020-10-11", "2020-nov-12", "2020-12-01");
        List<Date> dates = dateList.stream().map(s -> parseAndSneakyThrow(format, s)).collect(toList());
        System.out.println("usingLombokSneakyThrowToHandleCheckedExceptionWithLamda" + dates);
    }

    @SneakyThrows
    private static Date parseAndSneakyThrow(SimpleDateFormat format, String s) {
        return format.parse(s);
    }

    }
