import java.io.Serializable;

public class Product implements Serializable {
    // Property to set quantity (Note: This should be a method, not a property)
    public int setQuantity;

    // Private properties to pharmacy product information
    private String productName;
    private int quantity;

    // Constructor to create a product with a name and an initial quantity
    public Product(String productName, int quantity) {
        this.productName = productName;
        this.quantity = quantity;
    }

    // Method to set the quantity of the product
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Getter method to retrieve the product name
    public String getProductName() {
        return productName;
    }

    // Getter method to retrieve the quantity of the product
    public int getQuantity() {
        return quantity;
    }
}
