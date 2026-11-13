import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class WakeUpCoffee {
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Beverage> menu = new ArrayList<>();
    private static final List<AddOn> addOns = new ArrayList<>();
    private static final String[] CATEGORIES = {"Coffee", "Non-Coffee", "Sparkling Yakult", "Fruit Soda"};

    public static void main(String[] args) {
        loadMenu();
        Order order = new Order(1);

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("            WAKE UP COFFEE         ");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        try {
            boolean running = true;
            while (running) {
                showMainMenu();
                int choice = readInt("Choose an option: ", 0, 7);
                switch (choice) {
                    case 1: addDrink(order); break;
                    case 2: editItem(order); break;
                    case 3: removeDrink(order); break;
                    case 4: order.displayOrder(); break;
                    case 5: order = confirmOrder(order); break;
                    case 6:
                        order.clearOrder();
                        System.out.println("Order cleared. Ready for the next customer.");
                        break;
                    case 7: showDrinkInfo(); break;
                    case 0: running = false; break;
                }
            }
        } catch (NoSuchElementException e) {
            
        }
        System.out.println("\nThank you for ordering at Wake Up Coffee!");
    }

    private static void loadMenu() {
        final double M = 49, L = 69; 

        String coffee = "Contains espresso (caffeine). May contain milk.";
        menu.add(new Coffee(101, "Spanish Latte", M, L, coffee));
        menu.add(new Coffee(102, "Caramel Macchiato", M, L, coffee));
        menu.add(new Coffee(103, "Matcha Espresso", M, L, coffee));
        menu.add(new Coffee(104, "Salted Caramel Latte", M, L, coffee));
        menu.add(new Coffee(105, "Hazelnut Latte", M, L, coffee + " Hazelnut flavor: ask about nut allergy."));
        menu.add(new Coffee(106, "White Chocolate Latte", M, L, coffee));
        menu.add(new Coffee(107, "Strawberry Espresso", M, L, coffee));

        menu.add(new NonCoffee(201, "White Strawberry", M, L, "Contains milk."));
        menu.add(new NonCoffee(202, "White Matcha", M, L, "Contains milk and caffeine (matcha)."));

        String yakult = "Contains Yakult (dairy). Carbonated.";
        menu.add(new SparklingYakult(301, "Lemon Yakult", M, L, yakult));
        menu.add(new SparklingYakult(302, "Strawberry Yakult", M, L, yakult));
        menu.add(new SparklingYakult(303, "Mango Yakult", M, L, yakult));
        menu.add(new SparklingYakult(304, "Lychee Yakult", M, L, yakult));
        menu.add(new SparklingYakult(305, "Green Apple Yakult", M, L, yakult));

        String soda = "Carbonated fruit drink.";
        menu.add(new FruitSoda(401, "Green Apple Soda", M, L, soda));
        menu.add(new FruitSoda(402, "Strawberry Soda", M, L, soda));
        menu.add(new FruitSoda(403, "Mango Soda", M, L, soda));
        menu.add(new FruitSoda(404, "Lychee Soda", M, L, soda));

        addOns.add(new AddOn(1, "Espresso Natta", 10));
        addOns.add(new AddOn(2, "Caramel Drizzle", 5));
        addOns.add(new AddOn(3, "Chocolate Drizzle", 5));
        addOns.add(new AddOn(4, "Strawberry Drizzle", 5));
    }

    private static List<Beverage> getBeveragesByCategory(String category) {
        List<Beverage> result = new ArrayList<>();
        for (Beverage b : menu) {
            if (b.getCategory().equals(category)) result.add(b);
        }
        return result;
    }

    private static void showMainMenu() {
        System.out.println("\n----------------- MAIN MENU -----------------");
        System.out.println("[1] Add a drink to the order");
        System.out.println("[2] Edit an item (qty, size, add-on, timpla)");
        System.out.println("[3] Remove an item");
        System.out.println("[4] View order summary");
        System.out.println("[5] Confirm order");
        System.out.println("[6] Clear / start a new order");
        System.out.println("[7] Drink info (caffeine, milk, allergens)");
        System.out.println("[0] Exit");
    }

    private static Beverage selectBeverage() {
        System.out.println("\n--- Beverage Categories ---");
        for (int i = 0; i < CATEGORIES.length; i++) {
            System.out.println("[" + (i + 1) + "] " + CATEGORIES[i]);
        }
        System.out.println("[0] Back");
        int c = readInt("Select category: ", 0, CATEGORIES.length);
        if (c == 0) return null;

        String category = CATEGORIES[c - 1];
        List<Beverage> options = getBeveragesByCategory(category);
        System.out.println("\n--- " + category.toUpperCase() + " ---");
        for (int i = 0; i < options.size(); i++) {
            System.out.printf("[%d] ", i + 1);
            options.get(i).displayBeverage();
        }
        int d = readInt("Select drink (0 to cancel): ", 0, options.size());
        return (d == 0) ? null : options.get(d - 1);
    }

    private static void addDrink(Order order) {
        Beverage drink = selectBeverage();
        if (drink == null) return;
        System.out.println("Info: " + drink.getInfoNote());

        Size size = selectSize(drink);
        int quantity = readInt("Enter quantity (1-99): ", 1, 99);
        Customization custom = askCustomization(drink);
        AddOn addOn = selectAddOn();

        order.addItem(new OrderItem(drink, size, addOn, custom, quantity));
        System.out.println("Added: " + quantity + " x " + drink.getBeverageName() + " (" + size.getLabel() + ")");
    }

    private static Customization askCustomization(Beverage drink) {
        System.out.println("\n=== Customize the drink (all cups in this item) ===");

        int sugar = pickOption("Sugar level", Customization.SUGAR_LABELS, 4);
        int ice = pickOption("Ice level", Customization.ICE_LEVELS, 2);

        String milk = null;
        if (drink.hasMilkOption()) {
            milk = Customization.MILK_LEVELS[pickOption("Milk level", Customization.MILK_LEVELS, 1)];
        }
        String fizz = null;
        if (drink.hasFizzOption()) {
            fizz = Customization.FIZZ_LEVELS[pickOption("Fizz level", Customization.FIZZ_LEVELS, 1)];
        }

        System.out.print("\nSpecial concern / allergy note (Enter to skip): ");
        String notes = scanner.nextLine().trim();

        return new Customization(Customization.SUGAR_PERCENT[sugar],
                Customization.ICE_LEVELS[ice], milk, fizz, notes);
    }

    private static Size selectSize(Beverage drink) {
        System.out.println("\n--- Size ---");
        Size[] sizes = Size.values();
        for (int i = 0; i < sizes.length; i++) {
            System.out.printf("[%d] %-7s PHP %.2f%n", i + 1, sizes[i].getLabel(), drink.getPrice(sizes[i]));
        }
        return sizes[readInt("Select size: ", 1, sizes.length) - 1];
    }

    private static AddOn selectAddOn() {
        System.out.println("\n--- Add-ons ---");
        System.out.println("[0] No add-on");
        for (int i = 0; i < addOns.size(); i++) {
            System.out.print("[" + (i + 1) + "] ");
            addOns.get(i).displayAddOn();
        }
        int a = readInt("Select add-on: ", 0, addOns.size());
        return (a == 0) ? null : addOns.get(a - 1);
    }

    private static void editItem(Order order) {
        if (order.isEmpty()) {
            System.out.println("The order is empty.");
            return;
        }
        order.displayOrder();
        int n = readInt("Item number to edit (0 to cancel): ", 0, order.getItemCount());
        if (n == 0) return;
        OrderItem item = order.getItem(n - 1);

        System.out.println("\nWhat do you want to change?");
        System.out.println("[1] Quantity");
        System.out.println("[2] Size");
        System.out.println("[3] Add-on (change or remove)");
        System.out.println("[4] Sugar / ice / milk / fizz / note");
        System.out.println("[0] Cancel");
        int what = readInt("Choose: ", 0, 4);
        switch (what) {
            case 1:
                item.updateQuantity(readInt("New quantity (1-99): ", 1, 99));
                break;
            case 2:
                item.setSize(selectSize(item.getBeverage()));
                break;
            case 3:
                System.out.println("Current add-on: " + item.getAddOnName());
                item.setAddOn(selectAddOn());   
                break;
            case 4:
                item.setCustom(askCustomization(item.getBeverage()));
                break;
            default:
                return;
        }
        order.calculateTotal();
        System.out.println("Item updated. Please check the order with the customer:");
        order.displayOrder();
    }

    private static void removeDrink(Order order) {
        if (order.isEmpty()) {
            System.out.println("The order is empty.");
            return;
        }
        order.displayOrder();
        int n = readInt("Item number to remove (0 to cancel): ", 0, order.getItemCount());
        if (n == 0) return;
        order.removeItem(n - 1);
        System.out.println("Item removed.");
    }

    private static Order confirmOrder(Order order) {
        if (order.isEmpty()) {
            System.out.println("Cannot confirm an empty order.");
            return order;
        }
        order.displayOrder();
        int ok = readInt("Confirm this order? [1] Yes  [0] No: ", 0, 1);
        if (ok == 0) {
            System.out.println("Order not confirmed. You can still edit it.");
            return order;
        }
        System.out.println("\nOrder #" + order.getOrderID() + " CONFIRMED. Thank you!");
        return new Order(order.getOrderID() + 1); 
    }

    private static void showDrinkInfo() {
        Beverage drink = selectBeverage();
        if (drink == null) return;
        System.out.println("\n" + drink.getBeverageName() + " (" + drink.getCategory() + ")");
        System.out.println("Info: " + drink.getInfoNote());
    }

    private static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) return value;
            } catch (NumberFormatException e) {
            }
            System.out.println("Invalid input. Enter a number from " + min + " to " + max + ".");
        }
    }

    private static int pickOption(String title, String[] options, int defaultIndex) {
        System.out.println("\n" + title + ":");
        for (int i = 0; i < options.length; i++) {
            System.out.println("  [" + (i + 1) + "] " + options[i] + (i == defaultIndex ? "  <- standard" : ""));
        }
        while (true) {
            System.out.print("Select (Enter = standard): ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) return defaultIndex;
            try {
                int value = Integer.parseInt(line);
                if (value >= 1 && value <= options.length) return value - 1;
            } catch (NumberFormatException e) {
            }
            System.out.println("Invalid input. Enter a number from 1 to " + options.length + ".");
        }
    }
}

enum Size {
    MEDIUM("Medium"), LARGE("Large");

    private final String label;
    Size(String label) { this.label = label; }
    public String getLabel() { return label; }
}

abstract class Beverage {
    private final int beverageID;
    private final String beverageName;
    private final double mediumPrice;
    private final double largePrice;
    private final String infoNote;

    public Beverage(int beverageID, String beverageName, double mediumPrice, double largePrice, String infoNote) {
        this.beverageID = beverageID;
        this.beverageName = beverageName;
        this.mediumPrice = mediumPrice;
        this.largePrice = largePrice;
        this.infoNote = infoNote;
    }

    public int getBeverageID()      { return beverageID; }
    public String getBeverageName() { return beverageName; }
    public String getInfoNote()     { return infoNote; }

    public double getPrice(Size size) {
        return (size == Size.LARGE) ? largePrice : mediumPrice;
    }

    public abstract String getCategory();              
    public boolean hasMilkOption() { return false; }   
    public boolean hasFizzOption() { return false; }

    public void displayBeverage() {
        System.out.printf("%-22s Medium %.2f | Large %.2f%n", beverageName, mediumPrice, largePrice);
    }
}

class Coffee extends Beverage {
    public Coffee(int id, String name, double m, double l, String note) { super(id, name, m, l, note); }
    @Override public String getCategory()    { return "Coffee"; }
    @Override public boolean hasMilkOption() { return true; }
}

class NonCoffee extends Beverage {
    public NonCoffee(int id, String name, double m, double l, String note) { super(id, name, m, l, note); }
    @Override public String getCategory()    { return "Non-Coffee"; }
    @Override public boolean hasMilkOption() { return true; }
}

class SparklingYakult extends Beverage {
    public SparklingYakult(int id, String name, double m, double l, String note) { super(id, name, m, l, note); }
    @Override public String getCategory()    { return "Sparkling Yakult"; }
    @Override public boolean hasFizzOption() { return true; }
}

class FruitSoda extends Beverage {
    public FruitSoda(int id, String name, double m, double l, String note) { super(id, name, m, l, note); }
    @Override public String getCategory()    { return "Fruit Soda"; }
    @Override public boolean hasFizzOption() { return true; }
}

class AddOn {
    private final int addOnID;
    private final String addOnName;
    private final double price;

    public AddOn(int addOnID, String addOnName, double price) {
        this.addOnID = addOnID;
        this.addOnName = addOnName;
        this.price = price;
    }

    public int getAddOnID()      { return addOnID; }
    public String getAddOnName() { return addOnName; }
    public double getPrice()     { return price; }

    public void displayAddOn() {
        System.out.printf("%-20s + PHP %.2f%n", addOnName, price);
    }
}

class Customization {
    public static final int[] SUGAR_PERCENT = {0, 25, 50, 75, 100};
    public static final String[] SUGAR_LABELS = {
        "0% (no sugar)", "25% (less sweet)", "50% (half sweet)", "75%", "100%"};
    public static final String[] ICE_LEVELS  = {"No ice", "Less ice", "Regular ice", "Extra ice"};
    public static final String[] MILK_LEVELS = {"Less milk", "Regular milk", "Extra milk"};
    public static final String[] FIZZ_LEVELS = {"Less fizz", "Regular fizz", "Extra fizz"};

    private final int sugarPercent;
    private final String iceLevel;
    private final String milkLevel;  
    private final String fizzLevel;  
    private final String notes;

    public Customization(int sugarPercent, String iceLevel, String milkLevel, String fizzLevel, String notes) {
        this.sugarPercent = sugarPercent;
        this.iceLevel = iceLevel;
        this.milkLevel = milkLevel;
        this.fizzLevel = fizzLevel;
        this.notes = notes;
    }

    public String getNotes() { return notes; }

    public String describe() {
        StringBuilder sb = new StringBuilder("Sugar " + sugarPercent + "% | " + iceLevel);
        if (milkLevel != null) sb.append(" | ").append(milkLevel);
        if (fizzLevel != null) sb.append(" | ").append(fizzLevel);
        return sb.toString();
    }
}

class OrderItem {
    private final Beverage beverage;
    private Size size;
    private AddOn addOn;               
    private Customization custom;
    private int quantity;

    public OrderItem(Beverage beverage, Size size, AddOn addOn, Customization custom, int quantity) {
        this.beverage = beverage;
        this.size = size;
        this.addOn = addOn;
        this.custom = custom;
        this.quantity = quantity;
    }

    public Beverage getBeverage()      { return beverage; }
    public Size getSize()              { return size; }
    public Customization getCustom()   { return custom; }
    public int getQuantity()           { return quantity; }

    public void updateQuantity(int newQuantity) {
        if (newQuantity > 0) this.quantity = newQuantity;
    }

    public void setSize(Size size)             { this.size = size; }
    public void setAddOn(AddOn addOn)          { this.addOn = addOn; }      
    public void setCustom(Customization c)     { this.custom = c; }

    
    public double calculateSubtotal() {
        double unitPrice = beverage.getPrice(size);
        if (addOn != null) unitPrice += addOn.getPrice();
        return unitPrice * quantity;
    }

    public String getAddOnName() {
        return (addOn == null) ? "-" : addOn.getAddOnName();
    }
}

class Order {
    private final int orderID;
    private final List<OrderItem> orderItems = new ArrayList<>();
    private double totalAmount;

    public Order(int orderID) { this.orderID = orderID; }

    public int getOrderID()         { return orderID; }
    public int getItemCount()       { return orderItems.size(); }
    public boolean isEmpty()        { return orderItems.isEmpty(); }
    public OrderItem getItem(int i) { return orderItems.get(i); }

    public void addItem(OrderItem item) {
        orderItems.add(item);
        calculateTotal();
    }

    public boolean removeItem(int index) {
        if (index < 0 || index >= orderItems.size()) return false;
        orderItems.remove(index);
        calculateTotal();
        return true;
    }

    public double calculateTotal() {
        totalAmount = 0;
        for (OrderItem item : orderItems) totalAmount += item.calculateSubtotal();
        return totalAmount;
    }

    public double getTotalAmount() { return totalAmount; }

    public void displayOrder() {
        System.out.println("\n========================= ORDER SUMMARY =========================");
        System.out.println("Order #" + orderID);
        if (orderItems.isEmpty()) {
            System.out.println("(No items yet)");
        } else {
            System.out.printf("%-3s %-32s %-18s %3s %10s%n", "#", "Drink", "Add-on", "Qty", "Subtotal");
            System.out.println("----------------------------------------------------------------------");
            for (int i = 0; i < orderItems.size(); i++) {
                OrderItem item = orderItems.get(i);
                String drink = item.getBeverage().getBeverageName() + " (" + item.getSize().getLabel() + ")";
                System.out.printf("%-3d %-32s %-18s %3d %10.2f%n",
                        i + 1, drink, item.getAddOnName(), item.getQuantity(), item.calculateSubtotal());
                System.out.println("    " + item.getCustom().describe());
                if (!item.getCustom().getNotes().isEmpty()) {
                    System.out.println("    Note: " + item.getCustom().getNotes());
                }
            }
        }
        System.out.println("----------------------------------------------------------------------");
        System.out.printf("TOTAL AMOUNT: PHP %.2f%n", totalAmount);
        System.out.println("======================================================================");
    }

    public void clearOrder() {
        orderItems.clear();
        totalAmount = 0;
    }
                               }
