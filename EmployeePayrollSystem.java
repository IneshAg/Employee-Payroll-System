import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class EmployeePayrollSystem extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private ArrayList<Employee> employees;
    private int nextId = 1;
    
    private JTextField txtId, txtName, txtDept, txtSalary;
    private JButton btnAdd, btnUpdate, btnDelete, btnExportCSV, btnExportText;
    
    private static final String DATA_FILE = "employees.dat";
    
    public EmployeePayrollSystem() {
        setTitle("Employee Payroll System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        employees = new ArrayList<>();
        loadEmployees();
        
        createUI();
        refreshTable();
        
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void createUI() {
        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Employee Details"));
        
        inputPanel.add(new JLabel("ID:"));
        txtId = new JTextField();
        txtId.setEditable(false);
        inputPanel.add(txtId);
        
        inputPanel.add(new JLabel("Name:"));
        txtName = new JTextField();
        inputPanel.add(txtName);
        
        inputPanel.add(new JLabel("Department:"));
        txtDept = new JTextField();
        inputPanel.add(txtDept);
        
        inputPanel.add(new JLabel("Salary:"));
        txtSalary = new JTextField();
        inputPanel.add(txtSalary);
        
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        
        btnAdd.addActionListener(e -> addEmployee());
        btnUpdate.addActionListener(e -> updateEmployee());
        btnDelete.addActionListener(e -> deleteEmployee());
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        inputPanel.add(btnPanel);
        
        // Table Panel
        String[] columns = {"ID", "Name", "Department", "Salary"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    txtId.setText(model.getValueAt(row, 0).toString());
                    txtName.setText(model.getValueAt(row, 1).toString());
                    txtDept.setText(model.getValueAt(row, 2).toString());
                    txtSalary.setText(model.getValueAt(row, 3).toString());
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        // Export Panel
        JPanel exportPanel = new JPanel(new FlowLayout());
        btnExportCSV = new JButton("Export to CSV");
        btnExportText = new JButton("Export to Text");
        
        btnExportCSV.addActionListener(e -> exportToCSV());
        btnExportText.addActionListener(e -> exportToText());
        
        exportPanel.add(btnExportCSV);
        exportPanel.add(btnExportText);
        
        // Add to Frame
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(exportPanel, BorderLayout.SOUTH);
    }
    
    private void loadEmployees() {
        try {
            File file = new File(DATA_FILE);
            if (!file.exists()) {
                return;
            }
            
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 4) {
                    Employee emp = new Employee();
                    emp.id = Integer.parseInt(parts[0]);
                    emp.name = parts[1];
                    emp.department = parts[2];
                    emp.salary = Double.parseDouble(parts[3]);
                    employees.add(emp);
                    
                    if (emp.id >= nextId) {
                        nextId = emp.id + 1;
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }
    
    private void saveEmployees() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE));
            
            for (Employee emp : employees) {
                writer.println(emp.id + "|" + emp.name + "|" + emp.department + "|" + emp.salary);
            }
            
            writer.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + e.getMessage());
        }
    }
    
    private void refreshTable() {
        model.setRowCount(0);
        for (Employee emp : employees) {
            Object[] row = {emp.id, emp.name, emp.department, emp.salary};
            model.addRow(row);
        }
    }
    
    private void addEmployee() {
        try {
            String name = txtName.getText().trim();
            String dept = txtDept.getText().trim();
            double salary = Double.parseDouble(txtSalary.getText().trim());
            
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty!");
                return;
            }
            
            Employee emp = new Employee();
            emp.id = nextId++;
            emp.name = name;
            emp.department = dept;
            emp.salary = salary;
            
            employees.add(emp);
            saveEmployees();
            
            JOptionPane.showMessageDialog(this, "Employee added successfully!");
            clearFields();
            refreshTable();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid salary format!");
        }
    }
    
    private void updateEmployee() {
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            String name = txtName.getText().trim();
            String dept = txtDept.getText().trim();
            double salary = Double.parseDouble(txtSalary.getText().trim());
            
            boolean found = false;
            for (Employee emp : employees) {
                if (emp.id == id) {
                    emp.name = name;
                    emp.department = dept;
                    emp.salary = salary;
                    found = true;
                    break;
                }
            }
            
            if (found) {
                saveEmployees();
                JOptionPane.showMessageDialog(this, "Employee updated successfully!");
                clearFields();
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Employee not found!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input format!");
        }
    }
    
    private void deleteEmployee() {
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this employee?", 
                "Confirm Delete", 
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                boolean removed = employees.removeIf(emp -> emp.id == id);
                
                if (removed) {
                    saveEmployees();
                    JOptionPane.showMessageDialog(this, "Employee deleted successfully!");
                    clearFields();
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Employee not found!");
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete!");
        }
    }
    
    private void exportToCSV() {
        try {
            FileWriter writer = new FileWriter("employees.csv");
            
            // Write header
            writer.append("ID,Name,Department,Salary\n");
            
            // Write data
            for (Employee emp : employees) {
                writer.append(emp.id + ",");
                writer.append("\"" + emp.name + "\",");
                writer.append("\"" + emp.department + "\",");
                writer.append(emp.salary + "\n");
            }
            
            writer.flush();
            writer.close();
            
            JOptionPane.showMessageDialog(this, "CSV file exported successfully!\nSaved as: employees.csv");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting to CSV: " + e.getMessage());
        }
    }
    
    private void exportToText() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("employees.txt"));
            
            // Write title
            writer.println("====================================");
            writer.println("    EMPLOYEE PAYROLL REPORT");
            writer.println("====================================");
            writer.println();
            
            // Write header
            writer.printf("%-10s %-25s %-20s %-15s\n", "ID", "Name", "Department", "Salary");
            writer.println("------------------------------------------------------------------------");
            
            // Write data
            double totalSalary = 0;
            int count = 0;
            
            for (Employee emp : employees) {
                writer.printf("%-10d %-25s %-20s $%-14.2f\n", 
                    emp.id, emp.name, emp.department, emp.salary);
                totalSalary += emp.salary;
                count++;
            }
            
            writer.println("------------------------------------------------------------------------");
            writer.printf("\nTotal Employees: %d\n", count);
            writer.printf("Total Salary: $%.2f\n", totalSalary);
            writer.printf("Average Salary: $%.2f\n", count > 0 ? totalSalary / count : 0);
            
            writer.println();
            writer.println("Report generated on: " + new java.util.Date());
            
            writer.close();
            
            JOptionPane.showMessageDialog(this, "Text file exported successfully!\nSaved as: employees.txt");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting to text: " + e.getMessage());
        }
    }
    
    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        txtDept.setText("");
        txtSalary.setText("");
    }
    
    class Employee {
        int id;
        String name;
        String department;
        double salary;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EmployeePayrollSystem());
    }
}