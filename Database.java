import java.io.*;
import java.sql.*;
import java.util.Scanner;


public class Database {

    //This sets names of the file to open
    private final static String PERSON_FILE = "/Users/SantaMaria/Desktop/Java_Project/Person.data";
    private final static String ORDER_FILE = "/Users/SantaMaria/Desktop/Java_Project/Order.data";

    public static void main(String[] args) {

        try {
            //Set up connection to database
            Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sales", "root", "Password1");

            // Use prepared statement because I want to execute this many times and send my SQL statement right to the database
            PreparedStatement personInsertStatement = myConn.prepareStatement("INSERT INTO person (PERSON_ID, LAST_NAME, FIRST_NAME, STREET, CITY) values (?, ?, ?, ?, ?)");
            PreparedStatement orderInsertStatement = myConn.prepareStatement("INSERT INTO orders (ORDER_ID, ORDER_NO, PERSON_ID) values (?, ?, ?)");

            // Scanner scans the Person.data file and executes each line onto the console
            Scanner personScanner = new Scanner(new File(PERSON_FILE));
            personScanner.nextLine();

            // While statement to execute each next line in the Person document
            while (personScanner.hasNextLine()) {
                String line = personScanner.nextLine();

                // This if-statement will check if there is any empty spaces
                if (line.trim().length() > 0) {

                    // This will split each line at the ','
                    String[] data = line.split(",");
                    personInsertStatement.setInt(1, Integer.parseInt(data[0]));
                    personInsertStatement.setString(2, data[1]);
                    personInsertStatement.setString(3, data[2]);
                    personInsertStatement.setString(4, data[3]);
                    personInsertStatement.setString(5, data[4]);
                    personInsertStatement.executeUpdate();
                }
            }

            // Scanner scans the Order.data file and executes each line onto the console
            Scanner orderScanner = new Scanner(new File(ORDER_FILE));
            orderScanner.nextLine();

            // While statement to execute each next line in the Order document
            while (orderScanner.hasNextLine()) {
                String line = orderScanner.nextLine();

                // This if-statement will check if there is any empty spaces
                if (line.trim().length() > 0) {

                    // This will split each line at the '|'
                    String[] data = line.split("\\|");
                    orderInsertStatement.setInt(1, Integer.parseInt(data[0]));
                    orderInsertStatement.setInt(2, Integer.parseInt(data[1]));
                    orderInsertStatement.setInt(3, Integer.parseInt(data[2]));
                    orderInsertStatement.executeUpdate();
                }
            }

            // This will execute the query in the Persons table with a least one order
            Statement st = myConn.createStatement();
            ResultSet rs = st.executeQuery("SELECT PERSON_ID, LAST_NAME, FIRST_NAME, STREET, CITY FROM sales.person WHERE PERSON_ID IN (select distinct PERSON_ID from sales.orders)");
            while (rs.next()) {
                Integer PERSON_ID = rs.getInt(1);
                String LAST_NAME = rs.getString(2);
                String FIRST_NAME = rs.getString(3);
                String STREET = rs.getString(4);
                String CITY = rs.getString(5);
                System.out.format("%s %s %s %s %s%n", PERSON_ID, LAST_NAME, FIRST_NAME, STREET, CITY);
            }

            // Just wanted to add a clean line between each statement
            System.out.println("-----------------");

            // This will execute the query in the Persons table with a least one order
            Statement st2 =myConn.createStatement();
            ResultSet rs2 = st2.executeQuery("SELECT orders.ORDER_ID, orders.ORDER_NO, orders.PERSON_ID, person.FIRST_NAME FROM orders INNER JOIN person ON person.PERSON_ID = orders.PERSON_ID;");
            while (rs2.next()) {
                Integer ORDER_ID = rs2.getInt(1);
                Integer ORDER_NO = rs2.getInt(2);
                Integer PERSON_ID = rs2.getInt(3);
                String FIRST_NAME = rs2.getString(4);
                System.out.format("%s %s %s %s%n", ORDER_ID, ORDER_NO, PERSON_ID, FIRST_NAME);
            }
        }
        // If the file can't be found it will be caught here
        catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage() + "Couldn't open file");
        }
        //If there is an error in the file it will be caught here
        catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
