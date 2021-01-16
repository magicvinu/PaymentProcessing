package com.examples.asserts;

import com.nexus.payment.processing.exceptions.ServiceException;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 *
 * @param <T>
 * @param <E>
 */
public class AssertUtil<T, E extends RuntimeException> {

    /**
     *
     * @param t
     * @param predicate
     * @param eSupplier
     * @param <T>
     * @param <E>
     * @throws RuntimeException
     */
    public static <T, E extends RuntimeException> void check(T t, Predicate<T> predicate, Supplier<E> eSupplier) throws RuntimeException{
            if(!predicate.test(t)){
                throw eSupplier.get();
            }
    }
}
