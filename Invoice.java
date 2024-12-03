import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Invoice class that creates the Invoice frame
 * Contains added products, calculates various prices, and 
 * prints transaction receipt
 */
public class Invoice extends JFrame
{
    // Class variables
    private JTextArea itemsTextField;
    private JTextField salesTaxField;
    private JTextField discountField;
    private JTextField preTaxField;
    private JTextField taxedField;
    private JTextField discountedField;
    private JTextField grandTotalField;
    private JCheckBox applyDiscountCheckBox;
    private JButton printReceiptButton;
    final private int FRAMEWIDTH = 1000;
    final private int FRAMEHEIGHT = 800;

    // Data storage for receipt
    private String storeInfo;
    private String cashierName = "";
    private ArrayList<String> itemList = new ArrayList<>();
    private double totalBeforeTax = 0.0;

    public Invoice(String cashierName)
    {
        // Set the cashier name
        this.cashierName = cashierName;

        // Set the frame properties
        setTitle("Invoice");
        setBounds(FRAMEWIDTH, 0, FRAMEWIDTH, FRAMEHEIGHT);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(3, 1));

        
        
        // itemListPanel.add(itemsTextField);

        // Panel 1 - List items that have been added (name, quantity and price)
        // Set this panel's properties/border color
        JPanel itemListPanel = new JPanel();
        itemListPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        itemListPanel.setLayout(new BorderLayout());

        // Set text field properties
        itemsTextField = new JTextArea();
        itemsTextField.setEditable(false);

        // Add the scroll bar pane that has the added products
        JScrollPane scrollPane = new JScrollPane(itemsTextField);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Add the JScrollPane to the panel
        itemListPanel.add(scrollPane, BorderLayout.CENTER);



        // Panel 2 - Price calculations and receipt printing
        // Set this panel's properties/border color
        // Contains 3 flowlayout panels for structure
        JPanel panel2 = new JPanel(new GridLayout(3, 1));
        panel2.setBorder(BorderFactory.createLineBorder(Color.black));

        // Sales tax, discount, and checkbox row
        JPanel salesDiscountPanel = new JPanel(new FlowLayout());

        // Set sales tax, dicount, and checkbox properties
        salesTaxField = new JTextField(15);
        salesTaxField.setEditable(false);
        discountField = new JTextField(5);
        applyDiscountCheckBox = new JCheckBox("Apply Discount");

        // Add the fields and checkbox to the panel
        salesDiscountPanel.add(new JLabel("Sales Tax:"));
        salesDiscountPanel.add(salesTaxField);
        salesDiscountPanel.add(new JLabel("Discount:"));
        salesDiscountPanel.add(discountField);
        salesDiscountPanel.add(applyDiscountCheckBox);

        // Total prices row
        JPanel totalsPanel = new JPanel(new FlowLayout());

        // Set the total field's properties
        preTaxField = new JTextField(10);
        preTaxField.setEditable(false);
        taxedField = new JTextField(10);
        taxedField.setEditable(false);
        discountedField = new JTextField(10);
        discountedField.setEditable(false);

        // Add the fields to the panel
        totalsPanel.add(new JLabel("Total Before Taxes:"));
        totalsPanel.add(preTaxField);
        totalsPanel.add(new JLabel("Total After Taxes:"));
        totalsPanel.add(taxedField);
        totalsPanel.add(new JLabel("Total After Discount:"));
        totalsPanel.add(discountedField);

        // Grand total and receipt printing row
        JPanel grandTotReceiptPanel = new JPanel(new FlowLayout());

        // Set the grand total field and receipt button properties
        grandTotalField = new JTextField(10);
        grandTotalField.setEditable(false);
        printReceiptButton = new JButton("Print Receipt");

        // Add the grand total field to the panel
        grandTotReceiptPanel.add(new JLabel("Grand Total:"));
        grandTotReceiptPanel.add(grandTotalField);

        // Add the 3 panels in panel 2 to panel 2
        panel2.add(salesDiscountPanel);
        panel2.add(totalsPanel);
        panel2.add(grandTotReceiptPanel);



        // Panel 3 - Receipt/final sale info
        JPanel receiptPanel = new JPanel();
        panel2.setBorder(BorderFactory.createLineBorder(Color.black));
        receiptPanel.setLayout(new BorderLayout());

        // Add all 3 panels to the main frame
        add(itemListPanel);
        add(panel2);
        add(receiptPanel);

        // Checkbox button ActionListener
        applyDiscountCheckBox.addActionListener(new ActionListener()
        {
            /**
             * Updates the price field when the checkbox is clicked/unclicked
             * @param e - The clicked button
             */
            public void actionPerformed(ActionEvent e)
            {
                updatePriceFields();
            }
        });


        // Receipt button ActionListener
        printReceiptButton.addActionListener(new ActionListener()
        {
            /**
             * Prints all sales info on button click
             * @param e - The clicked button
             */
            public void actionPerformed(ActionEvent e)
            {
                generateReceipt(receiptPanel);
            }
        });

        grandTotReceiptPanel.add(printReceiptButton);
    }

    /**
     * Add items to the invoice list of products
     * @param itemName - Name of the product
     * @param quantity - Number of products added
     * @param price - Price for the products
     */
    public void addItem(String itemName, int quantity, double price)
    {
        // Calculate the total price 
        price *= quantity;

        // Format the text in the text area for each product description
        String item = String.format("Name: %-17s Quantity: %-11s Price: $%.2f", itemName, quantity, price);   
        // Add the item to the list of products
        itemList.add(item);
        
        // Calculate raw total
        totalBeforeTax += price * quantity;
        
        // Update other fields
        updateitemsTextField();
        updatePriceFields();
    }

    /**
     * Remove an item from the invoice list of products
     * @param lineItem - Position of item in list (starting at 1)
     */
    public boolean removeItem(String lineItem)
    {
        try
        {
            // Parse the line item passed from UI class
            int itemPos = Integer.parseInt(lineItem);

            // Check if the item number is valid
            if (itemPos >= 1 && itemPos <= itemList.size())
            {
                // Get the item we want to delete from the list and remove it
                String item = itemList.get(itemPos - 1);
                itemList.remove(itemPos - 1);
        
                // Get quantity and price form the item by splitting the item by colons
                String[] parts = item.split(":");
                System.out.print(parts.length);
                
                // 3rd part in the split is the quantity, trim it, split by spaces bc it include "Price"
                // Select the first value in this split
                String quantityPart = parts[2].trim().split(" ")[0];
                System.out.print(quantityPart);
                // 4th part in the split is the price - remove the $ and trim
                String pricePart = parts[3].replace("$", "").trim();
        
                // Parse the quantity and price
                int quantity = Integer.parseInt(quantityPart);
                double price = Double.parseDouble(pricePart);
        
                // Recalculate total before tax
                totalBeforeTax -= price * quantity;

                // Update other fields
                updateitemsTextField();
                updatePriceFields();
            }
            // If the line item isn't valid, open a dialog box to say its invalid and return false
            else
            {
                JOptionPane.showMessageDialog(this, "Invalid line item position");
                return false;
            }
        }
        // Catch all thrown errors
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(this, "An error occurred");
        }

        return true;
    }

    /**
     * Update the invoice items text field
     */
    private void updateitemsTextField()
    {
        // Set monospace font and add the item to the text field
        itemsTextField.setFont(new Font("Monospaced", Font.PLAIN, 12));
        itemsTextField.setText(String.join("\n", itemList));
    }

    // Update price fields based on current totals
    /**
     * Update panel2 price fields
     */
    private void updatePriceFields()
    {
        double taxRate = 0.1005; // Will get this from json file
        double taxAmount = totalBeforeTax * taxRate;
        double discountedAmount = 0.0; // Should stay 0 at the start - cashier enters it

        // Check if discount checkbox
        if (applyDiscountCheckBox.isSelected())
        {
            try {
                double discountRate = Double.parseDouble(discountField.getText()) * .01;
                discountedAmount = totalBeforeTax * discountRate;
            }
            // If discount can't be converted to double, print error msg
            catch (NumberFormatException e)
            {
                JOptionPane.showMessageDialog(this, "Invalid discount value");
            }
        }

        // Calculate price with tax, and price with discount
        double totalAfterTax = totalBeforeTax + taxAmount;
        double totalWithDiscount = totalAfterTax - discountedAmount;

        // Update total price text fields
        preTaxField.setText(String.format("%.2f", totalBeforeTax));
        taxedField.setText(String.format("%.2f", totalAfterTax));
        discountedField.setText(String.format("%.2f", totalWithDiscount));
        grandTotalField.setText(String.format("%.2f", totalWithDiscount));
    }

    /**
     * Create the final transaction receipt when print receipt button is clicked
     * @param receiptPanel - JPanel that has the receipt JButton
     */
    private void generateReceipt(JPanel receiptPanel)
    {
        // Use StringBuilder to create the receipt information
        StringBuilder receipt = new StringBuilder();

        // Add the store info
        receipt.append("Store Information: ").append(storeInfo).append("\n");


        receipt.append("Date of Transaction: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))).append("\n\n");

        // FIXME: we need to add product code
        // Add the list of products that were added
        for (String item : itemList) {
            receipt.append(item).append("\n");
        }

        // Add all the total prices
        receipt.append("\nTotal Before Taxes: $").append(preTaxField.getText());
        receipt.append("\nTotal After Taxes: $").append(taxedField.getText());

        // Check if discount checkbox has been checked
        if (applyDiscountCheckBox.isSelected())
        {
            try {
                double discountRate = Double.parseDouble(discountField.getText()) * .01;
                double discountedAmount = totalBeforeTax * discountRate;
                double totalWithDiscount = Double.parseDouble(taxedField.getText()) - discountedAmount;
                receipt.append("\nTotal After Discount: $").append(String.format("%.2f", totalWithDiscount));
            }
            // If discount can't be converted to double, print error msg
            catch (NumberFormatException e)
            {
                JOptionPane.showMessageDialog(this, "Invalid discount value");
            }
        }
        else
        {
            receipt.append("\nTotal After Discount: $").append(discountedField.getText());
        }

        receipt.append("\nGrand Total: $").append(grandTotalField.getText());

        // Add the message and cashier name + thank you message
        receipt.append("\n\nYour cashier serving you today is ").append(cashierName).append("\n");
        receipt.append("Thank you!");

        // Replace panel with receipt info
        receiptPanel.removeAll();

        // Instantiate a new text area with the receipt info
        JTextArea receiptArea = new JTextArea(receipt.toString());
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));


        // Add a scroll bar in case the receipt is long and update panel to show receipt
        receiptPanel.add(new JScrollPane(receiptArea), BorderLayout.CENTER);
        receiptPanel.revalidate();
        receiptPanel.repaint();
    }

    /**
     * Clear all the items in the frame
     */
    public void clearDisplay()
    {
        itemList.clear();
        totalBeforeTax = 0.0;
        updateitemsTextField();
        updatePriceFields();
    }

    /**
     * Set the cashier name
     * @param cashierName - The name of the cashier
     */
    public void setCashierName(String cashierName)
    {
        this.cashierName = cashierName;
    }
    
    /**
     * Sets the store information
     * @param storeName - Name of the store
     * @param phoneNumber - Store's phone number
     * @param city - City the store resides in
     * @param state - State the store resides in
     * @param taxPercentage - Tax percentage of the city
     */
    public void setStoreInfo(String storeName, String phoneNumber, String city, String state, double taxPercentage)
    {
        // Set the store information
        storeInfo = String.format("%-17s %-17s %-10s %-5s $%.2f", storeName, phoneNumber, city, state, taxPercentage);

        // Update the Sales Tax text field
        String salesTaxText = taxPercentage + "% (" + city + ", " + state + ")";
        salesTaxField.setText(salesTaxText);
    }
}
