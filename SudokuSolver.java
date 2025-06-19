import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;

public class SudokuSolver extends JFrame {
    private final JTextField[][] cells = new JTextField[9][9];
    private final int[][] board = {
        {9, 2, 0, 0, 3, 0, 0, 0, 0},
        {6, 0, 0, 5, 7, 4, 0, 0, 0},
        {0, 7, 4, 0, 0, 0, 0, 6, 0},
        {7, 0, 0, 0, 2, 0, 0, 0, 5},
        {8, 0, 0, 4, 0, 3, 0, 0, 6},
        {1, 0, 0, 0, 9, 0, 0, 0, 2},
        {0, 8, 0, 0, 0, 0, 6, 1, 0},
        {0, 0, 0, 2, 6, 7, 0, 0, 8},
        {0, 0, 0, 0, 4, 0, 0, 2, 9}
    };

    public SudokuSolver() {
        setTitle("Sudoku Checker");
        setSize(500, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel grid = new JPanel(new GridLayout(9, 9));
        Font font = new Font("Arial", Font.BOLD, 20);

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                JTextField field = new JTextField();
                ((AbstractDocument) field.getDocument()).setDocumentFilter(new DigitFilter());
                field.setHorizontalAlignment(JTextField.CENTER);
                field.setFont(font);

                if (board[i][j] != 0) {
                    field.setText(String.valueOf(board[i][j]));
                    field.setEditable(false);
                    field.setBackground(new Color(220, 220, 220));
                }

                final int row = i;
                final int col = j;

                field.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        switch (e.getKeyCode()) {
                            case KeyEvent.VK_UP -> {
                                if (row > 0) cells[row - 1][col].requestFocus();
                            }
                            case KeyEvent.VK_DOWN -> {
                                if (row < 8) cells[row + 1][col].requestFocus();
                            }
                            case KeyEvent.VK_LEFT -> {
                                if (col > 0) cells[row][col - 1].requestFocus();
                            }
                            case KeyEvent.VK_RIGHT -> {
                                if (col < 8) cells[row][col + 1].requestFocus();
                            }
                        }
                    }
                });

                cells[i][j] = field;
                grid.add(field);
            }
        }

        JButton solveButton = new JButton("Solve");
        solveButton.setFont(new Font("Arial", Font.BOLD, 18));
        
        solveButton.addActionListener(_ -> {
            if (readBoard()) {
                boolean isFull = isBoardFull(board);
                boolean isValid = isValidInitialBoard(board);

                if (isFull && isValid) {
                    JOptionPane.showMessageDialog(this, "Sudoku puzzle solved!");
                } else if (!isValid) {
                    JOptionPane.showMessageDialog(this, "Invalid Puzzle: Duplicate entries found.");
                } else {
                    JOptionPane.showMessageDialog(this, "So far correct. Keep solving!");
                }
            }
        });

        add(grid, BorderLayout.CENTER);
        add(solveButton, BorderLayout.SOUTH);
        setVisible(true);
    }

    private boolean readBoard() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (cells[i][j].isEditable()) {
                    String text = cells[i][j].getText().trim();
                    if (text.isEmpty()) {
                        board[i][j] = 0;
                    } else {
                        try {
                            int val = Integer.parseInt(text);
                            if (val < 1 || val > 9) throw new NumberFormatException();
                            board[i][j] = val;
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(this, "Invalid input at (" + (i + 1) + "," + (j + 1) + ")");
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean isBoardFull(int[][] board) {
        for (int[] row : board)
            for (int val : row)
                if (val == 0) return false;
        return true;
    }

    private boolean isValidInitialBoard(int[][] board) {
        for (int i = 0; i < 9; i++) {
            boolean[] rowCheck = new boolean[10];
            boolean[] colCheck = new boolean[10];
            for (int j = 0; j < 9; j++) {
                int rowVal = board[i][j];
                int colVal = board[j][i];
                if (rowVal != 0) {
                    if (rowCheck[rowVal]) return false;
                    rowCheck[rowVal] = true;
                }
                if (colVal != 0) {
                    if (colCheck[colVal]) return false;
                    colCheck[colVal] = true;
                }
            }
        }

        for (int boxRow = 0; boxRow < 3; boxRow++) {
            for (int boxCol = 0; boxCol < 3; boxCol++) {
                boolean[] boxCheck = new boolean[10];
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        int val = board[boxRow * 3 + i][boxCol * 3 + j];
                        if (val != 0) {
                            if (boxCheck[val]) return false;
                            boxCheck[val] = true;
                        }
                    }
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SudokuSolver::new);
    }
}

// Only allows a single digit between 1–9
class DigitFilter extends DocumentFilter {
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (isValidInput(fb, string)) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (isValidInput(fb, text)) {
            super.replace(fb, offset, length, text, attrs);
        }
    }

    private boolean isValidInput(FilterBypass fb, String text) throws BadLocationException {
        Document doc = fb.getDocument();
        String newText = doc.getText(0, doc.getLength()) + text;
        return newText.length() <= 1 && newText.matches("[1-9]?");
    }
}
