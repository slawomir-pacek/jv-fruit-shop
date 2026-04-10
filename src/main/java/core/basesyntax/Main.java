package core.basesyntax;

import core.basesyntax.io.*;
import core.basesyntax.model.FruitTransaction;
import core.basesyntax.service.*;
import core.basesyntax.service.impl.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        CsvFileReader reader = new CsvFileReaderImpl();
        FileWriter writer = new FileWriterImpl();

        List<String> input =
                reader.read("src/main/resources/reportToRead.csv");

        DataConverter converter = new DataConverterImpl();
        List<FruitTransaction> transactions =
                converter.convertToTransaction(input);

        Map<FruitTransaction.Operation, OperationHandler> handlers = new HashMap<>();
        handlers.put(FruitTransaction.Operation.BALANCE, new BalanceOperation());
        handlers.put(FruitTransaction.Operation.SUPPLY, new SupplyOperation());
        handlers.put(FruitTransaction.Operation.PURCHASE, new PurchaseOperation());
        handlers.put(FruitTransaction.Operation.RETURN, new ReturnOperation());

        OperationStrategy strategy = new OperationStrategyImpl(handlers);

        ShopService shopService = new ShopServiceImpl(strategy);
        shopService.process(transactions);

        ReportGenerator reportGenerator =
                new ReportGeneratorImpl(shopService.getStorage());

        String report = reportGenerator.getReport();

        writer.write(report, "src/main/resources/finalReport.csv");
    }
}
