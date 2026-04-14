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
public class StreamSolutionsTestPrivate {

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


    /**
     * 1) Calculate the companies maximum potential revenue during February.
     * In other words, ignore sales and assume that everyone paid full price.
     */

    /**
     * 2) Get the 5 most recent placed order IDs
     */

    /**
     * 3) Count the total number of distinct customers who made purchases in this dataset.
     */

    /**
     * 4) Calculate the total discount for all purchases made in March
     */

    /**
     * 5) Create a mapping between customer IDs and the amount they spent on products.
     */

    /**
     * 6) Create a mapping between product categories and the average cost of an item in that category
     */
    @Transactional
    @Test
    public void problem6() {

        Map<String, Double> answer = new HashMap<>();
        answer.put("Grocery", 24.44);
        answer.put("Tech", 292.54);
        answer.put("Toys", 81.03);
        answer.put("Games", 26.68);
        answer.put("Baby", 26.48);
        answer.put("Books", 60.55);

        /*
         * Sequential
         */
        log.info("problem6 seq. - Product category, average cost mapping");
        long startTime = System.currentTimeMillis();
        Map<String, Double> result = problemSixSeq(customerRepo, orderRepo, productRepo);
        long endTime = System.currentTimeMillis();
        log.info(String.format("problem6 - sequential execution time: %1$d ms", (endTime - startTime)));
        log.info(result.toString());

        DecimalFormat round = new DecimalFormat("#.##");
        Map<String, Double> roundedResult = new HashMap<>();
        for (Map.Entry<String, Double> e : result.entrySet()) {
            roundedResult.put(e.getKey(), Double.valueOf(round.format(e.getValue())));
        }
        Assertions.assertEquals(answer, roundedResult);

        /*
         * Parallel
         */
        log.info("problem6 par. - Product category, average cost mapping");
        startTime = System.currentTimeMillis();
        Map<String, Double> parResult = problemSixPar(customerRepo, orderRepo, productRepo);
        endTime = System.currentTimeMillis();
        log.info(String.format("problem6 - parallel execution time: %1$d ms", (endTime - startTime)));
        log.info(parResult.toString());

        Map<String, Double> roundedParResult = new HashMap<>();
        for (Map.Entry<String, Double> e : result.entrySet()) {
            roundedParResult.put(e.getKey(), Double.valueOf(round.format(e.getValue())));
        }
        Assertions.assertEquals(answer, roundedParResult);
    }

    /**
     * 7) Create a mapping between tech product IDs and the IDs of the customers who ordered
     * those products.
     */
    @Transactional
    @Test
    public void problem7() {

        Map<Long, Set<Long>> answer = new HashMap<>();
        answer.put(18L, new HashSet<>(Arrays.asList(1L, 4L, 6L, 8L, 9L)));
        answer.put(20L, new HashSet<>(Arrays.asList(2L, 4L, 8L)));
        answer.put(22L, new HashSet<>(Arrays.asList(1L, 2L, 3L, 5L, 6L, 9L)));
        answer.put(30L, new HashSet<>());

        /*
         * Sequential
         */
        log.info("problem7 seq. - Create a mapping between products and customers who ordered them");
        long startTime = System.currentTimeMillis();
        Map<Long, Set<Long>> result = problemSevenSeq(customerRepo, orderRepo, productRepo);
        long endTime = System.currentTimeMillis();
        log.info(String.format("problem7 - sequential execution time: %1$d ms", (endTime - startTime)));
        log.info(result.toString());

        Assertions.assertEquals(answer, result);

        /*
         * Parallel
         */
        log.info("problem7 par. - Create a mapping between products and customers who ordered them");
        startTime = System.currentTimeMillis();
        Map<Long, Set<Long>> parResult = problemSevenPar(customerRepo, orderRepo, productRepo);
        endTime = System.currentTimeMillis();
        log.info(String.format("problem7 - parallel execution time: %1$d ms", (endTime - startTime)));
        log.info(parResult.toString());

        Assertions.assertEquals(answer, parResult);
    }

    /**
     * 8) Create a mapping between customer IDs and sales utilization rate of that customer.
     */
    @Transactional
    @Test
    public void problem8() {

        Map<Long, Double> answer = new HashMap<>();
        answer.put(2L, 0.1875);
        answer.put(5L, 0.3214);
        answer.put(8L, 0.0667);
        answer.put(9L, 0.2632);

        /*
         * Sequential
         */
        log.info("problem8 seq. - Create a mapping between customers and sales utilization rate");
        long startTime = System.currentTimeMillis();
        Map<Long, Double> result = problemEightSeq(customerRepo, orderRepo, productRepo);
        long endTime = System.currentTimeMillis();
        log.info(String.format("problem8 - sequential execution time: %1$d ms", (endTime - startTime)));
        log.info(result.toString());

        DecimalFormat round = new DecimalFormat("#.####");
        Map<Long, Double> roundedResult = new HashMap<>();
        for (Map.Entry<Long, Double> e: result.entrySet()) {
            roundedResult.put(e.getKey(), Double.valueOf(round.format(e.getValue())));
        }

        Assertions.assertEquals(answer, roundedResult);

        /*
         * Parallel
         */
        log.info("problem8 par. - Create a mapping between customers and sales utilization rate");
        startTime = System.currentTimeMillis();
        Map<Long, Double> parResult = problemEightPar(customerRepo, orderRepo, productRepo);
        endTime = System.currentTimeMillis();
        log.info(String.format("problem8 - parallel execution time: %1$d ms", (endTime - startTime)));
        log.info(parResult.toString());

        Map<Long, Double> roundedParResult = new HashMap<>();
        for (Map.Entry<Long, Double> e: result.entrySet()) {
            roundedParResult.put(e.getKey(), Double.valueOf(round.format(e.getValue())));
        }

        Assertions.assertEquals(answer, roundedParResult);
    }

}
