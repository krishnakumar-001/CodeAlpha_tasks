import java.io.*;
import java.util.*;

/*
 * TASK 2: STOCK TRADING PLATFORM
 * Java OOP Project
 */

// =====================================================
// STOCK CLASS
// =====================================================

class Stock {

    private String symbol;
    private String companyName;
    private double price;
    private double previousPrice;

    public Stock(String symbol, String companyName, double price) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.price = price;
        this.previousPrice = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        previousPrice = this.price;
        this.price = price;
    }

    public double getPriceChange() {
        return price - previousPrice;
    }

    public double getChangePercentage() {
        if (previousPrice == 0) {
            return 0;
        }

        return ((price - previousPrice) / previousPrice) * 100;
    }

    public void displayStock() {
        System.out.printf(
                "%-10s %-20s ₹%-12.2f %+.2f%%\n",
                symbol,
                companyName,
                price,
                getChangePercentage()
        );
    }
}


// =====================================================
// TRANSACTION CLASS
// =====================================================

class Transaction {

    private String type;
    private String symbol;
    private int quantity;
    private double price;
    private String date;

    public Transaction(
            String type,
            String symbol,
            int quantity,
            double price
    ) {
        this.type = type;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;

        this.date = new Date().toString();
    }

    @Override
    public String toString() {

        return type +
                " | " +
                symbol +
                " | Quantity: " +
                quantity +
                " | Price: ₹" +
                String.format("%.2f", price) +
                " | " +
                date;
    }
}


// =====================================================
// PORTFOLIO CLASS
// =====================================================

class Portfolio {

    private Map<String, Integer> holdings;
    private double cash;
    private List<Transaction> transactions;

    public Portfolio(double initialCash) {

        cash = initialCash;

        holdings = new HashMap<>();

        transactions = new ArrayList<>();
    }

    // -------------------------------------------------
    // BUY STOCK
    // -------------------------------------------------

    public boolean buyStock(Stock stock, int quantity) {

        if (quantity <= 0) {
            System.out.println("Invalid quantity.");
            return false;
        }

        double totalCost = stock.getPrice() * quantity;

        if (totalCost > cash) {

            System.out.println(
                    "Insufficient funds!"
            );

            return false;
        }

        cash -= totalCost;

        holdings.put(
                stock.getSymbol(),
                holdings.getOrDefault(
                        stock.getSymbol(),
                        0
                ) + quantity
        );

        transactions.add(
                new Transaction(
                        "BUY",
                        stock.getSymbol(),
                        quantity,
                        stock.getPrice()
                )
        );

        System.out.println(
                "Successfully bought " +
                        quantity +
                        " shares of " +
                        stock.getSymbol()
        );

        return true;
    }

    // -------------------------------------------------
    // SELL STOCK
    // -------------------------------------------------

    public boolean sellStock(Stock stock, int quantity) {

        if (quantity <= 0) {

            System.out.println(
                    "Invalid quantity."
            );

            return false;
        }

        int owned =
                holdings.getOrDefault(
                        stock.getSymbol(),
                        0
                );

        if (owned < quantity) {

            System.out.println(
                    "You do not own enough shares."
            );

            return false;
        }

        double totalValue =
                stock.getPrice() * quantity;

        cash += totalValue;

        int remaining =
                owned - quantity;

        if (remaining == 0) {

            holdings.remove(
                    stock.getSymbol()
            );

        } else {

            holdings.put(
                    stock.getSymbol(),
                    remaining
            );
        }

        transactions.add(
                new Transaction(
                        "SELL",
                        stock.getSymbol(),
                        quantity,
                        stock.getPrice()
                )
        );

        System.out.println(
                "Successfully sold " +
                        quantity +
                        " shares of " +
                        stock.getSymbol()
        );

        return true;
    }

    // -------------------------------------------------
    // DISPLAY PORTFOLIO
    // -------------------------------------------------

    public void displayPortfolio(
            Map<String, Stock> market
    ) {

        System.out.println("\n====================================");
        System.out.println("           MY PORTFOLIO");
        System.out.println("====================================");

        System.out.printf(
                "Cash Available: ₹%.2f\n",
                cash
        );

        double stockValue = 0;

        if (holdings.isEmpty()) {

            System.out.println(
                    "No stocks owned."
            );

        } else {

            System.out.printf(
                    "%-10s %-10s %-15s\n",
                    "Symbol",
                    "Quantity",
                    "Value"
            );

            for (Map.Entry<String, Integer> entry :
                    holdings.entrySet()) {

                String symbol = entry.getKey();

                int quantity = entry.getValue();

                Stock stock = market.get(symbol);

                if (stock != null) {

                    double value =
                            stock.getPrice() * quantity;

                    stockValue += value;

                    System.out.printf(
                            "%-10s %-10d ₹%-15.2f\n",
                            symbol,
                            quantity,
                            value
                    );
                }
            }
        }

        double totalValue =
                cash + stockValue;

        System.out.println("------------------------------------");

        System.out.printf(
                "Stock Value: ₹%.2f\n",
                stockValue
        );

        System.out.printf(
                "Total Portfolio Value: ₹%.2f\n",
                totalValue
        );
    }

    // -------------------------------------------------
    // TRANSACTION HISTORY
    // -------------------------------------------------

    public void displayTransactions() {

        System.out.println("\n====================================");
        System.out.println("       TRANSACTION HISTORY");
        System.out.println("====================================");

        if (transactions.isEmpty()) {

            System.out.println(
                    "No transactions yet."
            );

            return;
        }

        for (Transaction transaction :
                transactions) {

            System.out.println(transaction);
        }
    }

    // -------------------------------------------------
    // SAVE PORTFOLIO
    // -------------------------------------------------

    public void savePortfolio(
            String filename
    ) {

        try {

            PrintWriter writer =
                    new PrintWriter(
                            new FileWriter(filename)
                    );

            writer.println("Cash=" + cash);

            for (Map.Entry<String, Integer> entry :
                    holdings.entrySet()) {

                writer.println(
                        entry.getKey() +
                                "=" +
                                entry.getValue()
                );
            }

            writer.close();

            System.out.println(
                    "Portfolio saved successfully."
            );

        } catch (IOException e) {

            System.out.println(
                    "Error saving portfolio: " +
                            e.getMessage()
            );
        }
    }

    public double getCash() {
        return cash;
    }
}


// =====================================================
// STOCK MARKET CLASS
// =====================================================

class StockMarket {

    private Map<String, Stock> stocks;

    public StockMarket() {

        stocks = new LinkedHashMap<>();

        addDefaultStocks();
    }

    private void addDefaultStocks() {

        stocks.put(
                "TCS",
                new Stock(
                        "TCS",
                        "Tata Consultancy Services",
                        3500
                )
        );

        stocks.put(
                "INFY",
                new Stock(
                        "INFY",
                        "Infosys",
                        1500
                )
        );

        stocks.put(
                "RELIANCE",
                new Stock(
                        "RELIANCE",
                        "Reliance Industries",
                        2900
                )
        );

        stocks.put(
                "HDFCBANK",
                new Stock(
                        "HDFCBANK",
                        "HDFC Bank",
                        1700
                )
        );

        stocks.put(
                "ITC",
                new Stock(
                        "ITC",
                        "ITC Limited",
                        450
                )
        );
    }

    public void displayMarket() {

        System.out.println("\n==============================================");
        System.out.println("              STOCK MARKET");
        System.out.println("==============================================");

        System.out.printf(
                "%-10s %-20s %-13s %s\n",
                "Symbol",
                "Company",
                "Price",
                "Change"
        );

        System.out.println("----------------------------------------------");

        for (Stock stock : stocks.values()) {

            stock.displayStock();
        }
    }

    public Stock getStock(String symbol) {

        return stocks.get(
                symbol.toUpperCase()
        );
    }

    public Map<String, Stock> getStocks() {
        return stocks;
    }

    // Simulate market price changes
    public void updateMarketPrices() {

        Random random = new Random();

        for (Stock stock : stocks.values()) {

            double currentPrice =
                    stock.getPrice();

            double change =
                    (random.nextDouble() * 0.10) - 0.05;

            double newPrice =
                    currentPrice +
                            (currentPrice * change);

            stock.setPrice(
                    Math.max(newPrice, 1)
            );
        }

        System.out.println(
                "\nMarket prices updated!"
        );
    }
}


// =====================================================
// USER CLASS
// =====================================================

class User {

    private String username;
    private Portfolio portfolio;

    public User(
            String username,
            double initialBalance
    ) {

        this.username = username;

        portfolio =
                new Portfolio(initialBalance);
    }

    public String getUsername() {
        return username;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }
}


// =====================================================
// MAIN CLASS
// =====================================================

public class StockTradingPlatform {

    private static Scanner scanner =
            new Scanner(System.in);

    private static StockMarket market =
            new StockMarket();

    private static User user =
            new User(
                    "Trader",
                    100000
            );

    public static void main(String[] args) {

        System.out.println(
                "=========================================="
        );

        System.out.println(
                "       JAVA STOCK TRADING PLATFORM"
        );

        System.out.println(
                "=========================================="
        );

        boolean running = true;

        while (running) {

            displayMenu();

            int choice =
                    getIntegerInput(
                            "Enter your choice: "
                    );

            switch (choice) {

                case 1:
                    market.displayMarket();
                    break;

                case 2:
                    buyStock();
                    break;

                case 3:
                    sellStock();
                    break;

                case 4:
                    user.getPortfolio()
                            .displayPortfolio(
                                    market.getStocks()
                            );
                    break;

                case 5:
                    user.getPortfolio()
                            .displayTransactions();
                    break;

                case 6:
                    market.updateMarketPrices();
                    break;

                case 7:
                    user.getPortfolio()
                            .savePortfolio(
                                    "portfolio.txt"
                            );
                    break;

                case 8:
                    running = false;

                    System.out.println(
                            "Thank you for using the platform!"
                    );

                    break;

                default:
                    System.out.println(
                            "Invalid choice."
                    );
            }
        }

        scanner.close();
    }

    // -------------------------------------------------
    // MENU
    // -------------------------------------------------

    private static void displayMenu() {

        System.out.println("\n");
        System.out.println(
                "============== MENU =============="
        );

        System.out.println(
                "1. View Market Data"
        );

        System.out.println(
                "2. Buy Stock"
        );

        System.out.println(
                "3. Sell Stock"
        );

        System.out.println(
                "4. View Portfolio"
        );

        System.out.println(
                "5. Transaction History"
        );

        System.out.println(
                "6. Update Market Prices"
        );

        System.out.println(
                "7. Save Portfolio"
        );

        System.out.println(
                "8. Exit"
        );

        System.out.println(
                "=================================="
        );
    }

    // -------------------------------------------------
    // BUY
    // -------------------------------------------------

    private static void buyStock() {

        System.out.print(
                "Enter stock symbol: "
        );

        String symbol =
                scanner.nextLine()
                        .toUpperCase();

        Stock stock =
                market.getStock(symbol);

        if (stock == null) {

            System.out.println(
                    "Stock not found."
            );

            return;
        }

        int quantity =
                getIntegerInput(
                        "Enter quantity: "
                );

        user.getPortfolio()
                .buyStock(
                        stock,
                        quantity
                );
    }

    // -------------------------------------------------
    // SELL
    // -------------------------------------------------

    private static void sellStock() {

        System.out.print(
                "Enter stock symbol: "
        );

        String symbol =
                scanner.nextLine()
                        .toUpperCase();

        Stock stock =
                market.getStock(symbol);

        if (stock == null) {

            System.out.println(
                    "Stock not found."
            );

            return;
        }

        int quantity =
                getIntegerInput(
                        "Enter quantity: "
                );

        user.getPortfolio()
                .sellStock(
                        stock,
                        quantity
                );
    }

    // -------------------------------------------------
    // INTEGER INPUT
    // -------------------------------------------------

    private static int getIntegerInput(
            String message
    ) {

        while (true) {

            try {

                System.out.print(message);

                return Integer.parseInt(
                        scanner.nextLine()
                );

            } catch (NumberFormatException e) {

                System.out.println(
                        "Please enter a valid number."
                );
            }
        }
    }
}
