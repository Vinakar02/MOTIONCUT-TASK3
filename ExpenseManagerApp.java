import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

// Model classes
class User implements Serializable {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

class Expense implements Serializable {
    private String category;
    private double amount;
    private LocalDate date;

    public Expense(String category, double amount, LocalDate date) {
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Category: " + category + ", Amount: $" + amount + ", Date: " + date;
    }
}

// Service classes
class UserService {
    private Map<String, User> users = new HashMap<>();

    public boolean registerUser(String username, String password) {
        if (users.containsKey(username)) return false;
        users.put(username, new User(username, password));
        return true;
    }

    public boolean authenticate(String username, String password) {
        User user = users.get(username);
        return user != null && user.getPassword().equals(password);
    }
}

class ExpenseService {
    private List<Expense> expenses = new ArrayList<>();

    public void addExpense(String category, double amount, LocalDate date) {
        expenses.add(new Expense(category, amount, date));
    }

    public List<Expense> getAllExpenses() {
        return expenses;
    }

    public List<Expense> getExpensesSortedByDate() {
        return expenses.stream()
                .sorted(Comparator.comparing(Expense::getDate))
                .collect(Collectors.toList());
    }

    public List<Expense> filterExpensesByCategory(String category) {
        return expenses.stream()
                .filter(e -> e.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public double getTotalByCategory(String category) {
        return expenses.stream()
                .filter(e -> e.getCategory().equalsIgnoreCase(category))
                .mapToDouble(Expense::getAmount)
                .sum();
    }
}

// Utility classes
class FileUtil {
    public static void saveExpensesToFile(List<Expense> expenses, String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(expenses);
        }
    }

    public static List<Expense> loadExpensesFromFile(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (List<Expense>) ois.readObject();
        }
    }
}

// Console-based UI
public class ExpenseManagerApp {
    private static UserService userService = new UserService();
    private static ExpenseService expenseService = new ExpenseService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            System.out.println("\nExpense Manager Application");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    handleRegister();
                    break;
                case 2:
                    if (handleLogin()) {
                        manageExpenses();
                    }
                    break;
                case 3:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void handleRegister() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (userService.registerUser(username, password)) {
            System.out.println("Registration successful!");
        } else {
            System.out.println("Username already exists. Try a different one.");
        }
    }

    private static boolean handleLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (userService.authenticate(username, password)) {
            System.out.println("Login successful!");
            return true;
        } else {
            System.out.println("Invalid credentials. Please try again.");
            return false;
        }
    }

    private static void manageExpenses() {
        boolean managing = true;

        while (managing) {
            System.out.println("\nExpense Management");
            System.out.println("1. Add Expense");
            System.out.println("2. View All Expenses");
            System.out.println("3. View Expenses by Category");
            System.out.println("4. View Total by Category");
            System.out.println("5. Save Expenses");
            System.out.println("6. Load Expenses");
            System.out.println("7. Logout");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    handleAddExpense();
                    break;
                case 2:
                    handleViewAllExpenses();
                    break;
                case 3:
                    handleViewExpensesByCategory();
                    break;
                case 4:
                    handleViewTotalByCategory();
                    break;
                case 5:
                    handleSaveExpenses();
                    break;
                case 6:
                    handleLoadExpenses();
                    break;
                case 7:
                    managing = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void handleAddExpense() {
        System.out.print("Enter category: ");
        String category = scanner.nextLine();
        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        LocalDate date = LocalDate.now();

        expenseService.addExpense(category, amount, date);
        System.out.println("Expense added successfully!");
    }

    private static void handleViewAllExpenses() {
        List<Expense> expenses = expenseService.getAllExpenses();
        System.out.println("\nAll Expenses:");
        expenses.forEach(System.out::println);
    }

    private static void handleViewExpensesByCategory() {
        System.out.print("Enter category: ");
        String category = scanner.nextLine();
        List<Expense> expenses = expenseService.filterExpensesByCategory(category);
        System.out.println("\nExpenses in category " + category + ":");
        expenses.forEach(System.out::println);
    }

    private static void handleViewTotalByCategory() {
        System.out.print("Enter category: ");
        String category = scanner.nextLine();
        double total = expenseService.getTotalByCategory(category);
        System.out.println("Total expenses in category " + category + ": $" + total);
    }

    private static void handleSaveExpenses() {
        System.out.print("Enter filename to save: ");
        String filename = scanner.nextLine();

        try {
            FileUtil.saveExpensesToFile(expenseService.getAllExpenses(), filename);
            System.out.println("Expenses saved to " + filename);
        } catch (IOException e) {
            System.out.println("Error saving expenses: " + e.getMessage());
        }
    }

    private static void handleLoadExpenses() {
        System.out.print("Enter filename to load: ");
        String filename = scanner.nextLine();

        try {
            List<Expense> expenses = FileUtil.loadExpensesFromFile(filename);
            expenseService.getAllExpenses().clear();
            expenseService.getAllExpenses().addAll(expenses);
            System.out.println("Expenses loaded from " + filename);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading expenses: " + e.getMessage());
        }
    }
}


