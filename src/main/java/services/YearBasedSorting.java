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
public class YearBasedSorting implements SortingService {
    private static final Logger logger = LoggerFactory.getLogger(YearBasedSorting.class);

    private final UserInputService inputService;
    private final AdmissionYearListService yearListService;
    private final SheetReadingService readingService;
    private final DatabaseRepository repository;

    @Override
    public void sortAndBatch() {
        /*
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

        ExecutorService executor = Executors.newFixedThreadPool(5);
        int pkColumnName = repository.getPrimaryKey(tableName);

        //get list of years present and make list called batch
        AdmissionYear year = (AdmissionYear) yearListService.getListOfYear(values, pkColumnName).toArray()[0];
        int yearOfAdmission = year.getYear();
        List<List<Object>> batch = new ArrayList<>();
        /*
        start iterating rows and make new list at the beginning  of iteration and add elements.
        Parse the column which is supposed to be primary key and its starting  2 letters are year in which student
        took admission to integer
         */
        for (List<Object> rows : values.subList(1, values.size())) {
            List<Object> currentList = new ArrayList<>();
            int currentYearInRow = Integer.parseInt(rows.get(pkColumnName).toString().substring(0, 2));
            /*
            check whether if current year in the row is same as first year of admission.
            if it is add the row to current list and add the current lis to batch.
            if it is not same then update the year of admission and and pass the batch to
            repository and run it in another thread
             */
            if (currentYearInRow != yearOfAdmission) {
                yearOfAdmission = Integer.parseInt(rows.get(pkColumnName).toString().substring(0, 2));
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
