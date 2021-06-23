package services;

import domain.AdmissionYear;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.DatabaseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BatchService {

    private static final Logger logger = LoggerFactory.getLogger(BatchService.class);
    private static final int STARTING_INDEX = 0;
    private static final int LAST_INDEX = 2;
    private final UserInputService inputService;
    private final AdmissionYearListService yearListService;
    private final SheetReadingService readingService;
    private final DatabaseRepository repository;

    public void batch() {
        /**
         get google sheet rows.
         take user input.
         Make a Executors pool
         */
        List<List<Object>> values = readingService.getRows();
        List<Object> colNameList = values.get(0);
        String colNameStr = colNameList.stream()
                .map(x -> (String) x)
                .collect(Collectors.joining(","));

        String tableName = inputService.getTableName();
        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        int pkColumnName = repository.getPrimaryKey(tableName);

        //get list of years present and make list called batch
        AdmissionYear year = (AdmissionYear) yearListService.getListOfYear(values, pkColumnName).toArray()[0];
        int yearOfAdmission = year.getYear();
        List<List<Object>> batch = new ArrayList<>();
        /**
         start iterating rows and make new list at the beginning  of iteration and add elements.
         Parse the column which is supposed to be primary key and its starting  2 letters are year in which student
         took admission to integer
         */
        for (List<Object> rows : values.subList(1, values.size())) {
            List<Object> currentList = new ArrayList<>();
            int currentYearInRow = Integer.parseInt(rows.get(pkColumnName).toString().substring(STARTING_INDEX, LAST_INDEX));
            /**
             check whether if current year in the row is same as first year of admission.
             if it is add the row to current list and add the current lis to batch.
             if it is not same then update the year of admission and and pass the batch to
             repository and run it in another thread
             */
            if (currentYearInRow != yearOfAdmission) {
                yearOfAdmission = Integer.parseInt(rows.get(pkColumnName).toString().substring(STARTING_INDEX, LAST_INDEX));
                inputService.getTableCreationQuery();
                executor.submit(() -> repository.insertData(new ArrayList<>(batch), colNameStr));
                batch.clear();
                currentList.addAll(rows);
            } else {
                currentList.addAll(rows);
                batch.add(currentList);
            }
        }
    }
}
