/**
 * Team 4: Gabriel Larot, Harleen Sandhu
 * Team Project - Cashier UI
 */

// Libraries
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// JSON-simple libraries
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*; 

/**
 * UI class that creates the UI cashier frame
 * Starts/ends a cashier's shift, loads and displays products, and
 * adds/removes/shows products
 */
public class UI extends JFrame
{
    // Class variables
    private JTextField firstTextField;
    private JTextField lastTextField;
    private JTextField startShiftTimeField;
    private JTextField endShiftTimeField;
    private JTextField productCodeTextField;
    private JTextField quantityTextField;
    private JTextField removeProductTextField;
    private Invoice invoiceFrame;
    private String cashierName = "";
    private boolean shiftStarted = false;

    // json data
    private JSONArray productList;
    private JSONArray storeInfo;

    final private int FRAMEWIDTH = 1000; // Adjust width to fit monitor
    final private int FRAMEHEIGHT = 1000;

    /**
     * UI constructor that creates the 3 different UI panels and
     * allows the cashier to add/remove products
     */
    public UI()
    {
        // Set the frame properties
        setTitle("Cashier UI");
        setSize(FRAMEWIDTH, FRAMEHEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 1));


        // Panel 1 - Cashier name and start/end shift times
        // Set this panel's properties/border color
        JPanel cashierShiftPanel = new JPanel(new FlowLayout());
        cashierShiftPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        // Set textfield width
        firstTextField = new JTextField(7);
        lastTextField = new JTextField(7);

        // Set start shift button properties
        JButton startShiftButton = new JButton("Start Shift");
        startShiftTimeField = new JTextField(12);
        startShiftTimeField.setEditable(false);

        // Set end shift button properties
        JButton endShiftButton = new JButton("End Shift");
        endShiftTimeField = new JTextField(12);
        endShiftTimeField.setEditable(false);

        // Add the buttons and text fields + add labels for them
        cashierShiftPanel.add(new JLabel("First Name:"));
        cashierShiftPanel.add(firstTextField);

        cashierShiftPanel.add(new JLabel("Last Name:"));
        cashierShiftPanel.add(lastTextField);

        cashierShiftPanel.add(startShiftButton);
        cashierShiftPanel.add(endShiftButton);

        cashierShiftPanel.add(new JLabel("Start Time:"));
        cashierShiftPanel.add(startShiftTimeField);

        cashierShiftPanel.add(new JLabel("End Time:"));
        cashierShiftPanel.add(endShiftTimeField);



        // Panel 2 - Load inventory, show all products and close the box
        // Set this panel's properties/border color
        JPanel inventoryPanel = new JPanel(new GridBagLayout());
        inventoryPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        // Create GridBagConstraints for layout control
        GridBagConstraints cp2 = new GridBagConstraints();

        // Instantiate the 3 buttons
        JButton loadInventoryButton = new JButton("Load Inventory");
        JButton showProductsButton = new JButton("Show Products");
        JButton closeButton = new JButton("Close");

        // Create a text area for products in the inventory panel
        JTextArea productsTextArea = new JTextArea(15, 40);
        productsTextArea.setEditable(false);

        // Add the text area to the scroll pane
        JScrollPane productScrollPane = new JScrollPane(productsTextArea);
        productScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        productScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Row 1 - Buttons
        // Load inventory button
        cp2.gridx = 0;
        cp2.gridy = 0;
        cp2.anchor = GridBagConstraints.LINE_START;
        cp2.insets = new Insets(5, 5, 5, 5);
        inventoryPanel.add(loadInventoryButton, cp2);

        // Show Products button
        cp2.gridx = 1;
        inventoryPanel.add(showProductsButton, cp2);

        // Close button
        cp2.gridx = 2;
        inventoryPanel.add(closeButton, cp2);

        // Row 2 - Show products scroll pane
        cp2.gridx = 0;
        cp2.gridy = 1;
        cp2.gridwidth = 3;
        cp2.weightx = 1.0;
        cp2.weighty = 1.0; 
        cp2.fill = GridBagConstraints.BOTH;
        inventoryPanel.add(productScrollPane, cp2);



        // Panel 3 - Add, remove, and show products
        // Set this panel's properties/border color
        JPanel productPanel = new JPanel(new GridBagLayout());
        productPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        // Create GridBagConstraints for layout control
        GridBagConstraints cp3 = new GridBagConstraints();

        // Instantiate the text fields and buttons
        productCodeTextField = new JTextField();
        quantityTextField = new JTextField();
        removeProductTextField = new JTextField();
        JButton addButton = new JButton("Add");
        JButton removeButton = new JButton("Remove");
        JButton showButton = new JButton("Show");

        // Create the text area for products
        JTextArea productNumTextArea = new JTextArea(15, 40);
        productNumTextArea.setEditable(false);

        // Add the text area to the scroll pane
        JScrollPane productNumScrollPane = new JScrollPane(productNumTextArea);
        productNumScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        productNumScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Row 1 - Product code, quantity, and add button
        // Product Code label
        cp3.gridx = 0;
        cp3.gridy = 0;
        cp3.anchor = GridBagConstraints.LINE_END;
        cp3.insets = new Insets(5, 5, 5, 5);
        productPanel.add(new JLabel("Product Code: "), cp3);

        // Product Code text field
        cp3.gridx = 1;
        cp3.weightx = 1;
        cp3.anchor = GridBagConstraints.LINE_START;
        cp3.fill = GridBagConstraints.HORIZONTAL;
        productPanel.add(productCodeTextField, cp3);

        // Quantity label
        cp3.gridx = 2;
        cp3.weightx = 0;
        cp3.fill = GridBagConstraints.NONE;
        cp3.anchor = GridBagConstraints.LINE_END;
        productPanel.add(new JLabel("Quantity: "), cp3);

        // Quantity text field
        cp3.gridx = 3;
        cp3.weightx = 1;
        cp3.fill = GridBagConstraints.HORIZONTAL;
        cp3.anchor = GridBagConstraints.LINE_START;
        productPanel.add(quantityTextField, cp3);

        // Add button
        cp3.gridx = 4;
        cp3.weightx = 0;
        cp3.anchor = GridBagConstraints.CENTER;
        cp3.fill = GridBagConstraints.NONE;
        productPanel.add(addButton, cp3);

        // Show button
        cp3.gridx = 5;
        productPanel.add(showButton, cp3);

        // Row 2 - Remove product text field and button
        // Remove line item label
        cp3.gridx = 0;
        cp3.gridy = 1;
        cp3.anchor = GridBagConstraints.LINE_END;
        productPanel.add(new JLabel("Remove Line Item: "), cp3);

        // Remove line item text field
        cp3.gridx = 1;
        cp3.weightx = 1;
        cp3.anchor = GridBagConstraints.LINE_START;
        cp3.fill = GridBagConstraints.HORIZONTAL;
        productPanel.add(removeProductTextField, cp3);

        // Remove button
        cp3.gridx = 2;
        cp3.weightx = 0;
        cp3.anchor = GridBagConstraints.LINE_END;
        cp3.fill = GridBagConstraints.NONE;
        productPanel.add(removeButton, cp3);

        // Row 3 - Scroll pane
        cp3.gridx = 0;
        cp3.gridy = 2;
        cp3.gridwidth = 6;
        cp3.weightx = 1.0;
        cp3.weighty = 1.0;
        cp3.fill = GridBagConstraints.BOTH;
        productPanel.add(productNumScrollPane, cp3);


        // Add the 3 panels to the frame
        add(cashierShiftPanel);
        add(inventoryPanel);
        add(productPanel);

        // Create the frame for Invoice class
        invoiceFrame = new Invoice("");
        invoiceFrame.setVisible(true);


        // Panel 1 action listeners
        // Start shift button action listener
        startShiftButton.addActionListener(new ActionListener()
        {
            /**
             * Sets the start shift text field and updates frame title
             * @param e - The event triggering the action listener (Start Shift button clicked)
             */
            public void actionPerformed(ActionEvent e)
            {
                // Check if the cashier has not started their shift yet
                if (!shiftStarted)
                {
                    // Get first and last name
                    String firstName = firstTextField.getText().trim();
                    String lastName = lastTextField.getText().trim();
            
                    // Make sure the cashier entered names in each field
                    if (!firstName.isEmpty() && !lastName.isEmpty())
                    {
                        // Change names to title case
                        String fnTitle = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
                        String lnTitle = lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();
                        cashierName = fnTitle + " " + lnTitle;

                        // Get the date and time when the shift started
                        String startTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
                        
                        // Update the time shift fields and change frame title
                        startShiftTimeField.setText(startTime);
                        endShiftTimeField.setText("");
                        setTitle("Cashier UI: " + cashierName + " logged in");

                        // Update the cashier name in Invoice class
                        invoiceFrame.setCashierName(cashierName);

                        shiftStarted = true;

                        invoiceFrame.startCashierShift(shiftStarted);
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null, "Must enter full name");
                    }
                }
                // Cashier has already started their shift - They must end their shift first
                else
                {
                    JOptionPane.showMessageDialog(null, "Cashier already started shift");
                }
            }
        });

        // End shift button action listener
        endShiftButton.addActionListener(new ActionListener()
        {
            /**
             * Sets the end shift text field, updates frame title, and clears many fields
             * @param e - The event triggering the action listener (End Shift button clicked)
             */
            public void actionPerformed(ActionEvent e)
            {
                // Check if the cashier has started their shift
                if (shiftStarted)
                {
                    // Get the date and time when the shift started
                    String endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
                    
                    // Update the time shift fields and change frame title
                    endShiftTimeField.setText(endTime);
                    startShiftTimeField.setText("");
                    setTitle("Cashier UI");

                    // Clear other fields
                    // Clear UI panels 1, 2, and 3 data
                    firstTextField.setText("");
                    lastTextField.setText("");
                    productsTextArea.setText(""); // Panel 2
                    productCodeTextField.setText("");
                    quantityTextField.setText("");
                    removeProductTextField.setText("");
                    productNumTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                    productNumTextArea.setText("");

                    // Clear Invoice panels 1, 2 and 3 data
                    invoiceFrame.clearDisplay();

                    shiftStarted = false;
                }
                // Cashier has not started their shift - show error msg
                else
                {
                    JOptionPane.showMessageDialog(null, "No cashier has started their shift");
      
                }
            }
        });


        // Panel 2 action listeners
        // Load Inventory button action listener - incomplete
        loadInventoryButton.addActionListener(new ActionListener()
        {
            /**
             * Loads the json file
             * @param e - The event triggering the action listener (Load Inventory button clicked)
             */
            public void actionPerformed(ActionEvent e)
            {
                // Check if the cashier has started their shift
                if (shiftStarted)
                {
                    try
                    {
                        // Load the json file and typecast Object to JSONObject
                        Object obj = new JSONParser().parse(new FileReader("assets/info.json"));
                        JSONObject jo = (JSONObject) obj;
            
                        // Get the productInfo and storeInfo array data
                        productList = (JSONArray) jo.get("productInfo");
                        storeInfo = (JSONArray) jo.get("storeInfo");

                        // Get the first (and only) store from storeInfo
                        JSONObject store = (JSONObject) storeInfo.get(0);

                        // Get all the store details from the store
                        String storeName = (String) store.get("storeName");
                        String phoneNumber = (String) store.get("phoneNumber");
                        String city = (String) store.get("city");
                        String state = (String) store.get("state");
                        double taxPercentage = Double.parseDouble(store.get("cityTaxPercentage").toString());

                        invoiceFrame.setStoreInfo(storeName, phoneNumber, city, state, taxPercentage);
            
                        // Show confirmation box
                        JOptionPane.showMessageDialog(null, "Inventory loaded.");
                    

                        
                    }
                    // Show that some error occurred related to the json file
                    catch (Exception ex)
                    {
                        JOptionPane.showMessageDialog(null, "Error with JSON file.");
                    }
                }
                // Cashier has not starter their shift - show error msg
                else
                {
                    JOptionPane.showMessageDialog(null, "No cashier has started their shift");
                }
            }
        });

        // Show Products button action listener
        showProductsButton.addActionListener(new ActionListener()
        {
            /**
             * Lists all the products in the json file
             * @param e - The event triggering the action listener (Show Products button clicked)
             */
            public void actionPerformed(ActionEvent e)
            {
                // Check if the cashier has started their shift
                if (shiftStarted)
                {
                    // Check if inventory has been loaded - do not permit, if not
                    if (productList == null || productList.isEmpty())
                    {
                        JOptionPane.showMessageDialog(null, "Inventory hasn't been loaded.");
                        return;
                    }

                    // Set the font to have consistent width (formatting)
                    productsTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            
                    // Output formatting for list of all products
                    StringBuilder allProducts = new StringBuilder();
                    allProducts.append(String.format("%-8s %-25s %-11s %-30s\n\n", "CODE", "PRODUCT NAME", "PRICE", "DESCRIPTION"));

                    // Iterate over productList to get the productInfo
                    for (Object obj : productList) {
                        JSONObject product = (JSONObject) obj;
                        String code = (String) product.get("productCode");
                        String name = (String) product.get("productName");
                        double price = (Double) product.get("price");
                        String description = (String) product.get("description");

                        allProducts.append(String.format("%-8s %-25s $%-10.2f %-30s\n", code, name, price, description));
                    }

                    // Add the data to the text area
                    productsTextArea.setText(allProducts.toString());
                }
                // Cashier has not started their shift - show error msg
                else
                {
                    JOptionPane.showMessageDialog(null, "No cashier has started their shift");
                }
            }
        });

        // Close button action listener
        closeButton.addActionListener(new ActionListener()
        {
            /**
             * Closes the text field list of products
             * @param e - The event triggering the action listener (Close button clicked)
             */
            public void actionPerformed(ActionEvent e)
            {
                // Check if the cashier has started their shift
                if (shiftStarted)
                {
                    productsTextArea.setText("");
                }
                // Cashier has not started their shift - show error msg
                else
                {
                    JOptionPane.showMessageDialog(null, "No cashier has started their shift");
                }
            }
        });


        // Panel 3 action listeners
        // Add button action listener
        addButton.addActionListener(new ActionListener()
        {
            /**
             * Adds an item to the Invoice product list (panel 1) based on product code number
             * @param e - The event triggering the action listener (Add button clicked)
             */
            public void actionPerformed(ActionEvent e)
            {
                // Check if the cashier has started their shift
                if (shiftStarted)
                {
                    String productCode = productCodeTextField.getText().trim();
                    String quantityText = quantityTextField.getText().trim();

                    // Check for missing values
                    if (productCode.isEmpty() || quantityText.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Product code and quantity can't be empty");
                        return;
                    }

                    try {
                        int quantity = Integer.parseInt(quantityText);

                        // Find the product number in the JSON list by iterating over productList
                        JSONObject selectedProduct = null;
                        for (Object obj : productList)
                        {
                            JSONObject product = (JSONObject) obj;
                            if (productCode.equals(product.get("productCode")))
                            {
                                selectedProduct = product;
                                break;
                            }
                        }

                        // Incorrect product code entered
                        if (selectedProduct == null)
                        {
                            JOptionPane.showMessageDialog(null, "The product code entered does not exist");
                            return;
                        }

                        // Get product details
                        String productName = (String) selectedProduct.get("productName");
                        double price = (Double) selectedProduct.get("price");

                        // Add the product to the invoice
                        invoiceFrame.addItem(productName, quantity, price);
                    }
                    // Show error msg related to invalid quantity field
                    catch (NumberFormatException ex)
                    {
                        JOptionPane.showMessageDialog(null, "Invalid quantity format");
                    }
                }
                // Cashier has not started their shift - show error msg
                else
                {
                    JOptionPane.showMessageDialog(null, "No cashier has started their shift");
                }
            }
        });

        // Remove button action listener
        removeButton.addActionListener(new ActionListener()
        {
            /**
             * Closes the text field list of products
             * @param e - The event triggering the action listener (Remove button clicked)
             */
            public void actionPerformed(ActionEvent e)
            {
                // Check if the cashier has started their shift
                if (shiftStarted)
                {
                    invoiceFrame.removeItem(removeProductTextField.getText().trim());
                }
                // Cashier has not started their shift - show error msg
                else
                {
                    JOptionPane.showMessageDialog(null, "No cashier has started their shift");
                }
            }
        });

        // Show button action listener
        showButton.addActionListener(new ActionListener()
        {
            /**
             * Shows all products associated with the product code
             * @param e - The event triggering the action listener (Show button clicked)
             */
            public void actionPerformed(ActionEvent e)
            {
                // Check if the cashier has started their shift
                if (shiftStarted)
                {
                    // Get the product code from the text field
                    String inputCode = productCodeTextField.getText().trim();

                    // Check if the inputCode is empty
                    if (inputCode.isEmpty())
                    {
                        JOptionPane.showMessageDialog(null, "No product code entered");
                        return;
                    }

                    boolean hasAsterisk = false;
                    // Check if a partial product code was entered and had a *
                    if (inputCode.endsWith("*"))
                    {
                        // Trim the asterik and flag it
                        hasAsterisk = true;
                        inputCode = inputCode.substring(0, inputCode.length() - 1);
                    }

                    StringBuilder matchingProducts = new StringBuilder();

                    String header = String.format("%-8s %-25s %-12s %-30s\n\n", "CODE", "PRODUCT NAME", "PRICE", "DESCRIPTION");
                    matchingProducts.append(header);

                    // Iterate through  productList
                    for (Object obj : productList)
                    {
                        JSONObject product = (JSONObject) obj;
                        String productCode = (String) product.get("productCode");

                        // Check if given product code matches the input code
                        if (hasAsterisk)
                        {
                            if (productCode.startsWith(inputCode))
                            {
                                // Add all product details that match or partially match
                                matchingProducts.append(String.format("%-8s %-25s $%-11s %-30s\n",
                                    product.get("productCode"),
                                    product.get("productName"),
                                    product.get("price"),
                                    product.get("description")
                                ));
                            }
                        }
                        // Code was partial and did not contain a * - show all products
                        else if (inputCode.length() < 5)
                        {
                            // Print all product details, if the code matches or partially matches
                            matchingProducts.append(String.format("%-8s %-25s $%-11s %-30s\n",
                                product.get("productCode"),
                                product.get("productName"),
                                product.get("price"),
                                product.get("description")
                            ));
                        }
                    }

                    // Display the results in the text area
                    if (matchingProducts.length() - header.length() > 0)
                    {
                        productNumTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                        productNumTextArea.setText(matchingProducts.toString());
                    }
                    // Did not find any matching product code numbers
                    else
                    {
                        productNumTextArea.setText("No matches found");
                    }
                }
                // Cashier has not started their shift - show error msg
                else
                {
                    JOptionPane.showMessageDialog(null, "No cashier has started their shift");
                }
            }
        });
    }
}