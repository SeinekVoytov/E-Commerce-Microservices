package org.example.productservice.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PaginationUtils {

    public static <T, R> Page<R> collectionToPageWithSortAndFilter(Collection<T> data,
                                                                             Pageable pageable,
                                                                             Function<T, R> mapper,
                                                                             Predicate<T> filter,
                                                                             Comparator<T> cmp) {
        return constructPage(
                data.stream().filter(filter).sorted(cmp).map(mapper), pageable, data.size()
        );
    }

    public static <T, R> Page<R> collectionToPageWithFilter(Collection<T> data,
                                                                      Pageable pageable,
                                                                      Function<T, R> mapper,
                                                                      Predicate<T> filter) {
        return constructPage(
                data.stream().filter(filter).map(mapper), pageable, data.size()
        );
    }

    private static <T> Page<T> constructPage(Stream<T> s, Pageable pageable, long total) {

        long limit = pageable.getPageSize();
        long offset = pageable.getOffset();

        return new PageImpl<>(
                s.skip(offset).limit(limit).toList(), pageable, total
        );
    }
}
