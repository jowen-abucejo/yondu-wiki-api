package com.yondu.knowledgebase.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.JpaSort;

public class MultipleSort {
    public static final String DATE_CREATED = "dateCreated";
    public static final String DATE_MODIFIED = "dateModified";
    public static final String RELEVANCE = "relevance";
    public static final String TOTAL_COMMENTS = "totalComments";
    public static final String TOTAL_RATINGS = "totalRatings";
    public static final String PAGE_TITLE = "versionTitle";
    public static final Set<String> VALID_SORT_ALIASES = Set.of(DATE_CREATED, DATE_MODIFIED, RELEVANCE,
            TOTAL_COMMENTS, TOTAL_RATINGS, PAGE_TITLE);

    public static List<Order> sortWithOrders(String[] sort, String[] defaultSorting) {
        List<Order> orders = new ArrayList<Order>();

        populateSortOrders(orders, sort);
        if (sort.length == 0)
            populateSortOrders(orders, defaultSorting);

        return orders;
    }

    public static List<Order> sortWithOrders(String[] sort, String[] defaultSorting,
            Set<String> validAliases) {
        if (validAliases.isEmpty())
            validAliases = VALID_SORT_ALIASES;

        List<Order> orders = new ArrayList<Order>();

        populateSortOrders(orders, sort, validAliases);
        if (sort.length == 0)
            populateSortOrders(orders, defaultSorting, validAliases);

        return orders;
    }

    private static void populateSortOrders(List<Order> orders, String[] sort, Set<String> validAliases) {
        if (validAliases.isEmpty())
            validAliases = VALID_SORT_ALIASES;

        if (sort.length == 0)
            return;
        if (sort[0].contains(",")) {
            // will sort more than 2 columns
            for (String sortOrder : sort) {
                String[] _sort = sortOrder.split(",");

                // sortOrder="column, direction"
                if (validAliases.contains(_sort[0]))
                    orders.add(addColumnSortDirection(_sort));

                // sort "column, column"
                if (_sort.length > 1 && validAliases.contains(_sort[1]))
                    orders.add(addColumnSortDirection(new String[] { _sort[1] }));
            }
        } else {
            // sort=[column, direction]
            if (validAliases.contains(sort[0])) {
                orders.add(addColumnSortDirection(sort));
            }

            // sort "column, column"
            if (sort.length > 1 && validAliases.contains(sort[1]))
                orders.add(addColumnSortDirection(new String[] { sort[1] }));
        }
    }

    private static Sort.Direction getSortDirection(String direction) {
        if (direction.toLowerCase().equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.toLowerCase().equals("desc")) {
            return Sort.Direction.DESC;
        }

        return Sort.Direction.DESC;
    }

    private static void populateSortOrders(List<Order> orders, String[] sort) {
        if (sort.length == 0)
            return;
        if (sort[0].contains(",")) {
            // will sort more than 2 columns
            for (String sortOrder : sort) {
                // sortOrder="column, direction"
                String[] _sort = sortOrder.split(",");
                orders.add(addColumnSortDirection(_sort));
            }
        } else {
            orders.add(addColumnSortDirection(sort));
        }
    }

    private static Order addColumnSortDirection(String[] sortFieldAndDirection) {
        if (sortFieldAndDirection.length > 1)// check if sort order exists
            return new Order(getSortDirection(sortFieldAndDirection[1]), sortFieldAndDirection[0]);
        else// use fallback sort order if not exists
            return new Order(getSortDirection(""), sortFieldAndDirection[0]);
    }

    public static Pageable sortByAliases(Pageable pageable) {

        Sort sort = Sort.by(Collections.emptyList());
        for (final Sort.Order order : pageable.getSort()) {
            if (order.getProperty().matches("^\\(.*\\)$")) {
                sort = sort.and(JpaSort.unsafe(order.getDirection(), order.getProperty()));
            } else {
                sort = sort.and(Sort.by(order.getDirection(), order.getProperty()));
            }
        }
        return PageRequest
                .of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

}
