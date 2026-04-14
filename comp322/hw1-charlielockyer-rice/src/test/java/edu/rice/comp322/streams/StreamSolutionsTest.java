package edu.rice.comp322.streams;

import java.text.DecimalFormat;
import java.util.*;

import edu.rice.comp322.provided.streams.DemoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
import edu.rice.comp322.provided.streams.repos.CustomerRepo;
import edu.rice.comp322.provided.streams.repos.OrderRepo;
import edu.rice.comp322.provided.streams.repos.ProductRepo;

import org.junit.jupiter.api.Assertions;

import org.springframework.boot.test.context.SpringBootTest;
import javax.transaction.Transactional;

import static edu.rice.comp322.solutions.StreamSolutions.*;

@SpringBootTest(classes = DemoApplication.class)
@Slf4j
public class StreamSolutionsTest {

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private ProductRepo productRepo;

    /**
     * 0) Test problem. To ensure that the repositories are loading properly simply run
     * the test.
     */
    @Transactional
    @Test
    public void problem0() {
        /*
         * Sequential
         */
        log.info("problem 0: verifying data loads correctly ");
        long startTime = System.currentTimeMillis();
        List<Long> result = problemZeroSeq(customerRepo, orderRepo, productRepo);
        long endTime = System.currentTimeMillis();
        log.info(String.format("problem 0 sequential execution time: %1$d ms", (endTime - startTime)));
        Assertions.assertEquals(Arrays.asList(11L, 50L, 30L), result);

        /*
         * Parallel
         */
        log.info("problem 0: verifying data loads correctly ");
        startTime = System.currentTimeMillis();
        List<Long> parResult = problemZeroPar(customerRepo, orderRepo, productRepo);
        endTime = System.currentTimeMillis();
        log.info(String.format("problem 0 parallel execution time: %1$d ms", (endTime - startTime)));
        Assertions.assertEquals(Arrays.asList(11L, 50L, 30L), parResult);
    }


    /**
     * 1) Calculate the companies maximum potential revenue during February.
     * In other words, ignore sales and assume that everyone paid full price.
     */
    @Transactional
    @Test
    public void problem1() {

        /*
         * Sequential
         */
        log.info("problem 1 seq. - Calculate the total lump sum of all orders placed in February at full price.");
        long startTime = System.currentTimeMillis();
        Double result = problemOneSeq(customerRepo, orderRepo, productRepo);
        long endTime = System.currentTimeMillis();
        log.info(String.format("problem 1 - sequential execution time: %1$d ms", (endTime - startTime)));
        log.info("Total lump sum = " + result);

        DecimalFormat round = new DecimalFormat("#.##");
        Double rounded = Double.valueOf(round.format(result));
        Assertions.assertEquals(2621.77, rounded);

        /*
         * Parallel
         */
        log.info("problem 1 seq. - Calculate the total lump sum of all orders placed in February at full price.");
        startTime = System.currentTimeMillis();
        Double parResult = problemOnePar(customerRepo, orderRepo, productRepo);
        endTime = System.currentTimeMillis();
        log.info(String.format("problem 1 - parallel execution time: %1$d ms", (endTime - startTime)));
        log.info("Total lump sum = " + parResult);

        rounded = Double.valueOf(round.format(result));
        Assertions.assertEquals(2621.77, rounded);
    }

    /**
     * 2) Get the 5 most recent placed order IDs
     */
    @Transactional
    @Test
    public void problem2() {

        Set<Long> answer = new HashSet<>(Arrays.asList(33L, 23L, 39L, 9L, 30L));

        /*
         * Sequential
         */
        log.info("problem2 seq. - Get the 5 most recent placed order");
        long startTime = System.currentTimeMillis();
        Set<Long> result = problemTwoSeq(customerRepo, orderRepo, productRepo);
        long endTime = System.currentTimeMillis();
        log.info(String.format("problem2 - sequential execution time: %1$d ms", (endTime - startTime)));
        log.info("Order IDs of 5 most recently placed orders: " + result.toString());

        Assertions.assertEquals(answer, result);

        /*
         * Parallel
         */
        log.info("problem2 par. - Get the 5 most recent placed order");
        startTime = System.currentTimeMillis();
        Set<Long> parResult = problemTwoPar(customerRepo, orderRepo, productRepo);
        endTime = System.currentTimeMillis();
        log.info(String.format("problem2 - parallel execution time: %1$d ms", (endTime - startTime)));
        log.info("Order IDs of 5 most recently placed orders: " + result.toString());

        Assertions.assertEquals(answer, parResult);
    }

    /**
     * 3) Count the total number of distinct customers who made purchases in this dataset.
     */
    @Transactional
    @Test
    public void problem3() {

        /*
         * Sequential
         */
        log.info("problem3 - Count the total number of customers who made purchases");
        long startTime = System.currentTimeMillis();
        Long result = problemThreeSeq(customerRepo, orderRepo, productRepo);
        long endTime = System.currentTimeMillis();
        log.info(String.format("problem3 - sequential execution time: %1$d ms", (endTime - startTime)));
        log.info(String.format("Number of customers: %1$d", result));

        Assertions.assertEquals(10L, result);

        /*
         * Parallel
         */
        log.info("problem3 - Count the total number of customers who made purchases");
        startTime = System.currentTimeMillis();
        Long parResult = problemThreePar(customerRepo, orderRepo, productRepo);
        endTime = System.currentTimeMillis();
        log.info(String.format("problem3 - parallel execution time: %1$d ms", (endTime - startTime)));
        log.info(String.format("Number of customers: %1$d", parResult));

        Assertions.assertEquals(10L, parResult);
    }

    /**
     * 4) Calculate the total discount for all purchases made in March
     */
    @Transactional
    @Test
    public void problem4() {

        /*
         * Sequential
         */
        log.info("problem4 seq. - Calculate total discount");
        long startTime = System.currentTimeMillis();
        Double result = problemFourSeq(customerRepo, orderRepo, productRepo);
        long endTime = System.currentTimeMillis();
        log.info(String.format("problem4 - sequential execution time: %1$d ms", (endTime - startTime)));
        log.info("total discount = " + result);

        DecimalFormat round = new DecimalFormat("#.##");
        Double rounded = Double.valueOf(round.format(result));
        Assertions.assertEquals(284.72, rounded);

        /*
         * Parallel
         */
        log.info("problem4 par. - Calculate total discount");
        startTime = System.currentTimeMillis();
        Double parResult = problemFourPar(customerRepo, orderRepo, productRepo);
        endTime = System.currentTimeMillis();
        log.info(String.format("problem4 - parallel execution time: %1$d ms", (endTime - startTime)));
        log.info("total discount = " + parResult);

        Double parRounded = Double.valueOf(round.format(result));
        Assertions.assertEquals(284.72, parRounded);
    }

    /**
     * 5) Create a mapping between customer IDs and the amount they spent on products.
     */
    @Transactional
    @Test
    public void problem5() {

        Map<Long, Double> answer = new HashMap<>();
        answer.put(1L, 3437.85);
        answer.put(2L, 938.46);
        answer.put(3L, 1188.62);
        answer.put(4L, 1706.39);
        answer.put(5L, 2140.61);
        answer.put(6L, 1460.76);
        answer.put(7L, 526.53);
        answer.put(8L, 1019.35);
        answer.put(9L, 2004.98);
        answer.put(10L, 417.95);

        /*
         * Sequential
         */
        log.info("problem5 seq. - Create customer and dollar spent mapping");
        long startTime = System.currentTimeMillis();
        Map<Long, Double> result = problemFiveSeq(customerRepo, orderRepo, productRepo);
        long endTime = System.currentTimeMillis();
        log.info(String.format("problem5 - sequential execution time: %1$d ms", (endTime - startTime)));
        log.info(result.toString());

        DecimalFormat round = new DecimalFormat("#.##");
        Map<Long, Double> roundedResult = new HashMap<>();
        for (Map.Entry<Long, Double> e: result.entrySet()) {
            roundedResult.put(e.getKey(), Double.valueOf(round.format(e.getValue())));
        }

        Assertions.assertEquals(answer, roundedResult);

        /*
         * Parallel
         */
        log.info("problem5 par. - Create customer and dollar spent mapping");
        startTime = System.currentTimeMillis();
        Map<Long, Double> parResult = problemFivePar(customerRepo, orderRepo, productRepo);
        endTime = System.currentTimeMillis();
        log.info(String.format("problem5 - parallel execution time: %1$d ms", (endTime - startTime)));
        log.info(parResult.toString());

        Map<Long, Double> roundedParResult = new HashMap<>();
        for (Map.Entry<Long, Double> e: result.entrySet()) {
            roundedParResult.put(e.getKey(), Double.valueOf(round.format(e.getValue())));
        }

        Assertions.assertEquals(answer, roundedParResult);
    }

    /**
     * 6) Create a mapping between product categories and the average cost of an item in that category
     */

    /**
     * 7) Create a mapping between tech product IDs and the IDs of the customers who ordered
     * those products.
     */

    /**
     * 8) Create a mapping between customer IDs and sales utilization rate of that customer.
     */

}
