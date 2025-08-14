package it.denzosoft.prolog;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;


public class PrologGUI extends JFrame {
    private JTextArea factsArea;
    private JTextArea queryArea;
    private JTextArea resultArea;
    private JButton loadButton;
    private JButton executeButton;
    private JButton clearButton;
    private JButton debugButton;
    private JButton traceButton;
    private JCheckBox debugCheckBox;
    private JCheckBox traceCheckBox;
    private File currentFile;

    public PrologGUI() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupWindowProperties();
    }

    private void initializeComponents() {
        setTitle("Prolog Interpreter");
        
        factsArea = new JTextArea(15, 50);
        factsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        factsArea.setEditable(true);
        
        queryArea = new JTextArea(3, 50);
        queryArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        queryArea.setEditable(true);
        
        resultArea = new JTextArea(10, 50);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        resultArea.setEditable(false);
        resultArea.setBackground(Color.LIGHT_GRAY);
        
        loadButton = new JButton("Load Prolog File");
        executeButton = new JButton("Execute Query");
        clearButton = new JButton("Clear Results");
        
        // Debug and trace controls
        debugCheckBox = new JCheckBox("Debug", PrologConfig.isDebugEnabled());
        traceCheckBox = new JCheckBox("Trace", PrologConfig.isTraceEnabled());
        
        // Add enter key listener for query execution
        queryArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isControlDown()) {
                    executeQuery();
                }
            }
        });
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Top panel for file loading and debug/trace controls
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(loadButton);
        topPanel.add(new JLabel("      Current File: "));
        JLabel fileLabel = new JLabel("None");
        topPanel.add(fileLabel);
        
        // Debug/trace controls
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(new JLabel("Debug/Trace: "));
        topPanel.add(debugCheckBox);
        topPanel.add(traceCheckBox);
        
        // Center panel for facts area
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Prolog Facts and Rules"));
        JScrollPane factsScrollPane = new JScrollPane(factsArea);
        centerPanel.add(factsScrollPane, BorderLayout.CENTER);
        
        // Query panel
        JPanel queryPanel = new JPanel(new BorderLayout());
        queryPanel.setBorder(BorderFactory.createTitledBorder("Query"));
        JScrollPane queryScrollPane = new JScrollPane(queryArea);
        queryPanel.add(queryScrollPane, BorderLayout.CENTER);
        queryPanel.add(executeButton, BorderLayout.EAST);
        
        // Results panel
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Results"));
        JScrollPane resultsScrollPane = new JScrollPane(resultArea);
        resultsPanel.add(resultsScrollPane, BorderLayout.CENTER);
        resultsPanel.add(clearButton, BorderLayout.SOUTH);
        
        // Combine query and results
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(queryPanel, BorderLayout.NORTH);
        bottomPanel.add(resultsPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Update file label when currentFile changes
        loadButton.addActionListener(e -> {
            if (currentFile != null) {
                fileLabel.setText(currentFile.getName());
            } else {
                fileLabel.setText("None");
            }
        });
    }

    private void setupEventHandlers() {
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPrologFile();
            }
        });
        
        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Update debug/trace settings
                PrologConfig.setDebugEnabled(debugCheckBox.isSelected());
                PrologConfig.setTraceEnabled(traceCheckBox.isSelected());
                executeQuery();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resultArea.setText("");
            }
        });
        
        // Debug and trace checkbox listeners
        debugCheckBox.addActionListener(e -> {
            PrologConfig.setDebugEnabled(debugCheckBox.isSelected());
            appendToResult("Debug " + (debugCheckBox.isSelected() ? "enabled" : "disabled") + "\n");
        });
        
        traceCheckBox.addActionListener(e -> {
            PrologConfig.setTraceEnabled(traceCheckBox.isSelected());
            appendToResult("Trace " + (traceCheckBox.isSelected() ? "enabled" : "disabled") + "\n");
        });
    }

    private void setupWindowProperties() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);
    }

    private void loadPrologFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Prolog Files", "pl", "pro"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(currentFile))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                factsArea.setText(content.toString());
                appendToResult("Loaded: " + currentFile.getName() + "\n");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + e.getMessage(), 
                                            "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void executeQuery() {
        String query = queryArea.getText().trim();
        if (query.isEmpty()) {
            return;
        }
        
        appendToResult("?- " + query + "\n");
        
        // Simple mock execution - in a real implementation, this would interact with a Prolog engine
        String result = executeMockQuery(query);
        appendToResult(result + "\n");
        
        // Clear query area
        queryArea.setText("");
    }

    private String executeMockQuery(String query) {
        // This is a mock implementation - in a real application, this would connect to a Prolog interpreter
        // For demonstration purposes, we'll just simulate some responses
        
        if (query.toLowerCase().contains("help")) {
            return "Available commands: help, listing, halt, debug on, debug off, trace on, trace off.";
        } else if (query.toLowerCase().contains("listing")) {
            String facts = factsArea.getText().trim();
            return facts.isEmpty() ? "No facts loaded." : facts;
        } else if (query.toLowerCase().contains("halt")) {
            System.exit(0);
            return "";
        } else if (query.toLowerCase().startsWith("debug on")) {
            PrologConfig.setDebugEnabled(true);
            debugCheckBox.setSelected(true);
            return "Debug mode enabled.";
        } else if (query.toLowerCase().startsWith("debug off")) {
            PrologConfig.setDebugEnabled(false);
            debugCheckBox.setSelected(false);
            return "Debug mode disabled.";
        } else if (query.toLowerCase().startsWith("trace on")) {
            PrologConfig.setTraceEnabled(true);
            traceCheckBox.setSelected(true);
            return "Trace mode enabled.";
        } else if (query.toLowerCase().startsWith("trace off")) {
            PrologConfig.setTraceEnabled(false);
            traceCheckBox.setSelected(false);
            return "Trace mode disabled.";
        } else if (query.endsWith(".")) {
            return "Fact asserted.";
        } else if (query.endsWith("?")) {
            return "true.";
        } else {
            return "Syntax error: Queries should end with '?' or '.'";
        }
    }

    private void appendToResult(String text) {
        resultArea.append(text);
        resultArea.setCaretPosition(resultArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new PrologGUI().setVisible(true);
            }
        });
    }
}
