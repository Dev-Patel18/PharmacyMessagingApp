import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Pharmacy implements Serializable {
    // Pharmacy properties
    private String name;
    private User pharmacist;

    // List to pharmacy products in the pharmacy
    private List<Product> products = new ArrayList<Product>();

    // Constructor to create a pharmacy with a name and a pharmacist
    public Pharmacy(String name, User pharmacist) {
        this.name = name;
        this.pharmacist = pharmacist;
    }

    // Getter method for the pharmacist user object
    public User getPharmacist() {
        return pharmacist;
    }

    // Method to find a product in the pharmacy by its name
    public Product findProductByName(String productName) {
        for (Product product : products) {
            if (product.getProductName().equals(productName)) {
                return product;
            }
        }
        // Return null if the product is not found
        return null;
    }

    // Getter method for the pharmacy name
    public String getName() {
        return name;
    }

    // Getter method for the list of products in the pharmacy
    public List<Product> getProducts() {
        return products;
    }

    // Method to add a new product to the pharmacy
    public void addProduct(Product newProduct) {
        products.add(newProduct);
    }

    // Method to check if the pharmacy has a specific product
    public boolean hasProduct(Product product) {
        return products.contains(product);
    }
}
