package com.yondu.knowledgebase.Utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

public class MultipleSort {
    public static List<Order> sortWithOrders(String[] sort, String[] additionalSortingOrders) {
        List<Order> orders = new ArrayList<Order>();

        populateSortOrders(orders, sort);
        populateSortOrders(orders, additionalSortingOrders);

        return orders;
    }

    private static Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }

        return Sort.Direction.ASC;
    }

    private static void populateSortOrders(List<Order> orders, String[] sort) {
        if (sort.length == 0)
            return;
        if (sort[0].contains(",")) {
            // will sort more than 2 columns
            for (String sortOrder : sort) {
                // sortOrder="column, direction"
                String[] _sort = sortOrder.split(",");
                orders.add(new Order(getSortDirection(_sort[1]), _sort[0]));
            }
        } else {
            // sort=[column, direction]
            orders.add(new Order(getSortDirection(sort[1]), sort[0]));
        }
    }
}
