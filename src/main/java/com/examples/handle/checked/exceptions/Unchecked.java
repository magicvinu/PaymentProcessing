package com.examples.handle.checked.exceptions;

import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class Unchecked<T, R , E extends Exception>{

    public static Logger log =Logger.getLogger(Unchecked.class.getName());
    static <T, R, E extends Exception> Function<T, R> applyOrThrow(ThrowingFunction<T, R, E> f) {
        return t -> {
            try {
                return f.apply(t);
            } catch (Exception e) {
                throw new RuntimeException((E) e);
            }
        };
    }

    static <T, R , E extends Exception> Function<T, R> apply(ThrowingFunction<T, R, E> f, Function<T, R> defaultFunction) {
        return t -> {
            try {
                return f.apply(t);
            } catch (Exception e) {
                log.log(Level.SEVERE,e.getMessage());
                return defaultFunction.apply(t);
            }
        };
    }
}
