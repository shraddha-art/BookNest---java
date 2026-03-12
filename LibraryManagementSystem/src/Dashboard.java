import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Dashboard extends JFrame {

    private static final Color BG         = new Color(200, 191, 176);
    private static final Color CARD       = new Color(255, 255, 255);
    private static final Color TEXT_DARK  = new Color(61, 43, 31);
    private static final Color FIELD_BG   = new Color(252, 250, 248);
    private static final Color FIELD_BOR  = new Color(210, 200, 190);
    private static final Color BTN_ADD    = new Color(107, 80, 72);
    private static final Color BTN_EDIT   = new Color(143, 168, 168);
    private static final Color BTN_DEL    = new Color(179, 100, 90);
    private static final Color BTN_CANCEL = new Color(180, 170, 160);
    private static final Color BTN_CALC   = new Color(143, 168, 168);

    private JTextField txtReaderID, txtReaderName;
    private JTable readersTable;
    private DefaultTableModel readersModel;

    private JTextField txtBookID, txtBookName;
    private JTable booksTable;
    private DefaultTableModel booksModel;

    private JComboBox<String> cmbIssueReader, cmbIssueBook;
    private JSpinner spinnerStartDate;
    private JTable issueTable;
    private DefaultTableModel issueModel;

    private JComboBox<String> cmbEndReader, cmbEndBook;
    private JSpinner spinnerEndDate;
    private JLabel lblDaysTaken;
    private JTable returnsTable;
    private DefaultTableModel returnsModel;

    public Dashboard() {
        setTitle("BookNest");
        setSize(960, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(null);

        JButton btnLogout = makeBtn("Logout", BTN_DEL);
        btnLogout.setBounds(860, 10, 80, 28);
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out?",
                "Logout",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginForm();
                this.dispose();
            }
        });
        add(btnLogout);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(BG);
        tabbedPane.setForeground(TEXT_DARK);
        tabbedPane.setFont(new Font("Georgia", Font.BOLD, 13));
        tabbedPane.setBounds(0, 45, 960, 635);

        tabbedPane.addTab("Readers", createReadersPanel());
        tabbedPane.addTab("Books",   createBooksPanel());
        tabbedPane.addTab("Start",   createIssuePanel());
        tabbedPane.addTab("End",     createReturnsPanel());

        add(tabbedPane);
        setVisible(true);

        loadReaders(); loadBooks(); loadIssues(); loadReturns();
        refreshDropdowns();
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        l.setForeground(TEXT_DARK);
        return l;
    }

    private JTextField makeField() {
        JTextField f = new JTextField();
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.setBackground(FIELD_BG);
        f.setForeground(TEXT_DARK);
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(FIELD_BOR, 1, true),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return f;
    }

    private JComboBox<String> makeCombo() {
        JComboBox<String> c = new JComboBox<>();
        c.setFont(new Font("SansSerif", Font.PLAIN, 13));
        c.setBackground(FIELD_BG);
        c.setForeground(TEXT_DARK);
        return c;
    }

    private JSpinner makeSpinner() {
        JSpinner s = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor ed = new JSpinner.DateEditor(s, "yyyy-MM-dd");
        s.setEditor(ed);
        s.setFont(new Font("SansSerif", Font.PLAIN, 13));
        s.setBorder(new LineBorder(FIELD_BOR, 1, true));
        return s;
    }

    private JButton makeBtn(String text, Color bg) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JTable makeTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setFont(new Font("SansSerif", Font.PLAIN, 12));
        t.setForeground(TEXT_DARK);
        t.setBackground(CARD);
        t.setGridColor(FIELD_BOR);
        t.getTableHeader().setBackground(new Color(220, 210, 200));
        t.getTableHeader().setForeground(TEXT_DARK);
        t.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        t.setRowHeight(24);
        t.setSelectionBackground(new Color(210, 195, 180));
        t.setSelectionForeground(TEXT_DARK);
        return t;
    }

    private JPanel makeCard() {
        JPanel p = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 18, 18));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }

    private JPanel createReadersPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(BG);

        JPanel card = makeCard();
        card.setBounds(20, 20, 340, 300);

        JLabel title = new JLabel("Readers");
        title.setFont(new Font("Georgia", Font.BOLD, 18));
        title.setForeground(TEXT_DARK);
        title.setBounds(25, 20, 200, 28);

        JLabel lblID = makeLabel("Reader ID:");
        lblID.setBounds(25, 60, 100, 22);
        txtReaderID = makeField();
        txtReaderID.setBounds(130, 60, 180, 30);

        JLabel lblName = makeLabel("Reader Name:");
        lblName.setBounds(25, 102, 110, 22);
        txtReaderName = makeField();
        txtReaderName.setBounds(130, 102, 180, 30);

        JButton btnAdd    = makeBtn("Add",    BTN_ADD);    btnAdd.setBounds(25, 155, 70, 30);
        JButton btnEdit   = makeBtn("Edit",   BTN_EDIT);   btnEdit.setBounds(103, 155, 70, 30);
        JButton btnDelete = makeBtn("Delete", BTN_DEL);    btnDelete.setBounds(181, 155, 70, 30);
        JButton btnCancel = makeBtn("Cancel", BTN_CANCEL); btnCancel.setBounds(259, 155, 70, 30);

        card.add(title); card.add(lblID); card.add(txtReaderID);
        card.add(lblName); card.add(txtReaderName);
        card.add(btnAdd); card.add(btnEdit); card.add(btnDelete); card.add(btnCancel);

        readersModel = new DefaultTableModel(new String[]{"Reader ID", "Reader Name", "Books Read"}, 0);
        readersTable = makeTable(readersModel);
        JScrollPane scroll = new JScrollPane(readersTable);
        scroll.setBounds(380, 20, 550, 560);
        scroll.setBorder(new LineBorder(FIELD_BOR, 1, true));

        panel.add(card); panel.add(scroll);

        btnAdd.addActionListener(e -> addReader());
        btnEdit.addActionListener(e -> editReader());
        btnDelete.addActionListener(e -> deleteReader());
        btnCancel.addActionListener(e -> clearReaderFields());

        readersTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = readersTable.getSelectedRow();
                txtReaderID.setText(readersModel.getValueAt(row, 0).toString());
                txtReaderName.setText(readersModel.getValueAt(row, 1).toString());
            }
        });
        return panel;
    }

    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(BG);

        JPanel card = makeCard();
        card.setBounds(20, 20, 340, 300);

        JLabel title = new JLabel("Books");
        title.setFont(new Font("Georgia", Font.BOLD, 18));
        title.setForeground(TEXT_DARK);
        title.setBounds(25, 20, 200, 28);

        JLabel lblID = makeLabel("Book ID:");
        lblID.setBounds(25, 60, 100, 22);
        txtBookID = makeField();
        txtBookID.setBounds(130, 60, 180, 30);

        JLabel lblName = makeLabel("Book Name:");
        lblName.setBounds(25, 102, 110, 22);
        txtBookName = makeField();
        txtBookName.setBounds(130, 102, 180, 30);

        JButton btnAdd    = makeBtn("Add",    BTN_ADD);    btnAdd.setBounds(25, 155, 70, 30);
        JButton btnEdit   = makeBtn("Edit",   BTN_EDIT);   btnEdit.setBounds(103, 155, 70, 30);
        JButton btnDelete = makeBtn("Delete", BTN_DEL);    btnDelete.setBounds(181, 155, 70, 30);
        JButton btnCancel = makeBtn("Cancel", BTN_CANCEL); btnCancel.setBounds(259, 155, 70, 30);

        card.add(title); card.add(lblID); card.add(txtBookID);
        card.add(lblName); card.add(txtBookName);
        card.add(btnAdd); card.add(btnEdit); card.add(btnDelete); card.add(btnCancel);

        booksModel = new DefaultTableModel(new String[]{"Book ID", "Book Name"}, 0);
        booksTable = makeTable(booksModel);
        JScrollPane scroll = new JScrollPane(booksTable);
        scroll.setBounds(380, 20, 550, 560);
        scroll.setBorder(new LineBorder(FIELD_BOR, 1, true));

        panel.add(card); panel.add(scroll);

        btnAdd.addActionListener(e -> addBook());
        btnEdit.addActionListener(e -> editBook());
        btnDelete.addActionListener(e -> deleteBook());
        btnCancel.addActionListener(e -> clearBookFields());

        booksTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = booksTable.getSelectedRow();
                txtBookID.setText(booksModel.getValueAt(row, 0).toString());
                txtBookName.setText(booksModel.getValueAt(row, 1).toString());
            }
        });
        return panel;
    }

    private JPanel createIssuePanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(BG);

        JPanel card = makeCard();
        card.setBounds(20, 20, 340, 300);

        JLabel title = new JLabel("Start");
        title.setFont(new Font("Georgia", Font.BOLD, 18));
        title.setForeground(TEXT_DARK);
        title.setBounds(25, 20, 200, 28);

        JLabel lblReader = makeLabel("Reader Name:");
        lblReader.setBounds(25, 60, 110, 22);
        cmbIssueReader = makeCombo();
        cmbIssueReader.setBounds(130, 60, 185, 30);

        JLabel lblBook = makeLabel("Book Name:");
        lblBook.setBounds(25, 102, 110, 22);
        cmbIssueBook = makeCombo();
        cmbIssueBook.setBounds(130, 102, 185, 30);

        JLabel lblDate = makeLabel("Start Date:");
        lblDate.setBounds(25, 144, 100, 22);
        spinnerStartDate = makeSpinner();
        spinnerStartDate.setBounds(130, 144, 150, 30);

        JButton btnAdd    = makeBtn("Add",    BTN_ADD);    btnAdd.setBounds(25, 197, 70, 30);
        JButton btnDelete = makeBtn("Delete", BTN_DEL);    btnDelete.setBounds(103, 197, 70, 30);
        JButton btnCancel = makeBtn("Cancel", BTN_CANCEL); btnCancel.setBounds(181, 197, 70, 30);

        card.add(title); card.add(lblReader); card.add(cmbIssueReader);
        card.add(lblBook); card.add(cmbIssueBook);
        card.add(lblDate); card.add(spinnerStartDate);
        card.add(btnAdd); card.add(btnDelete); card.add(btnCancel);

        issueModel = new DefaultTableModel(new String[]{"Issue ID", "Reader Name", "Book Name", "Start Date"}, 0);
        issueTable = makeTable(issueModel);
        JScrollPane scroll = new JScrollPane(issueTable);
        scroll.setBounds(380, 20, 550, 560);
        scroll.setBorder(new LineBorder(FIELD_BOR, 1, true));

        panel.add(card); panel.add(scroll);

        btnAdd.addActionListener(e -> addIssue());
        btnDelete.addActionListener(e -> deleteIssue());
        btnCancel.addActionListener(e -> clearIssueFields());
        return panel;
    }

    private JPanel createReturnsPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(BG);

        JPanel card = makeCard();
        card.setBounds(20, 20, 340, 370); // smaller card now

        JLabel title = new JLabel("End");
        title.setFont(new Font("Georgia", Font.BOLD, 18));
        title.setForeground(TEXT_DARK);
        title.setBounds(25, 20, 200, 28);

        // Return ID removed from card
        JLabel lblReader = makeLabel("Reader Name:");
        lblReader.setBounds(25, 60, 110, 22);
        cmbEndReader = makeCombo();
        cmbEndReader.setBounds(130, 60, 185, 30);

        JLabel lblBook = makeLabel("Book Name:");
        lblBook.setBounds(25, 102, 110, 22);
        cmbEndBook = makeCombo();
        cmbEndBook.setBounds(130, 102, 185, 30);

        JLabel lblEndDate = makeLabel("End Date:");
        lblEndDate.setBounds(25, 144, 100, 22);
        spinnerEndDate = makeSpinner();
        spinnerEndDate.setBounds(130, 144, 150, 30);

        JLabel lblDaysLbl = makeLabel("Days Taken:");
        lblDaysLbl.setBounds(25, 186, 100, 22);
        lblDaysTaken = new JLabel("-");
        lblDaysTaken.setBounds(130, 186, 180, 22);
        lblDaysTaken.setFont(new Font("Georgia", Font.BOLD, 14));
        lblDaysTaken.setForeground(BTN_ADD);

        JButton btnCalc   = makeBtn("Calculate", BTN_CALC); btnCalc.setBounds(25, 228, 100, 30);
        JButton btnAdd    = makeBtn("Add",    BTN_ADD);     btnAdd.setBounds(133, 228, 80, 30);
        JButton btnDelete = makeBtn("Delete", BTN_DEL);     btnDelete.setBounds(25, 270, 80, 30);
        JButton btnCancel = makeBtn("Cancel", BTN_CANCEL);  btnCancel.setBounds(113, 270, 80, 30);

        card.add(title);
        card.add(lblReader); card.add(cmbEndReader);
        card.add(lblBook); card.add(cmbEndBook);
        card.add(lblEndDate); card.add(spinnerEndDate);
        card.add(lblDaysLbl); card.add(lblDaysTaken);
        card.add(btnCalc); card.add(btnAdd);
        card.add(btnDelete); card.add(btnCancel);

        returnsModel = new DefaultTableModel(new String[]{"Return ID", "Reader Name", "Book Name", "End Date", "Days Taken"}, 0);
        returnsTable = makeTable(returnsModel);
        JScrollPane scroll = new JScrollPane(returnsTable);
        scroll.setBounds(380, 20, 550, 560);
        scroll.setBorder(new LineBorder(FIELD_BOR, 1, true));

        panel.add(card); panel.add(scroll);

        btnCalc.addActionListener(e -> calculateDays());
        btnAdd.addActionListener(e -> addReturn());
        btnDelete.addActionListener(e -> deleteReturn());
        btnCancel.addActionListener(e -> clearReturnFields());

        returnsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = returnsTable.getSelectedRow();
                lblDaysTaken.setText(returnsModel.getValueAt(row, 4).toString() + " days");
            }
        });
        return panel;
    }

    private void refreshDropdowns() {
        try (Connection conn = DBConnection.getConnection()) {
            if (cmbIssueReader != null) cmbIssueReader.removeAllItems();
            if (cmbEndReader   != null) cmbEndReader.removeAllItems();
            ResultSet rs = conn.createStatement().executeQuery("SELECT reader_id, name FROM readers");
            while (rs.next()) {
                String item = rs.getInt("reader_id") + " - " + rs.getString("name");
                if (cmbIssueReader != null) cmbIssueReader.addItem(item);
                if (cmbEndReader   != null) cmbEndReader.addItem(item);
            }
            if (cmbIssueBook != null) cmbIssueBook.removeAllItems();
            if (cmbEndBook   != null) cmbEndBook.removeAllItems();
            ResultSet rs2 = conn.createStatement().executeQuery("SELECT book_id, title FROM books");
            while (rs2.next()) {
                String item = rs2.getInt("book_id") + " - " + rs2.getString("title");
                if (cmbIssueBook != null) cmbIssueBook.addItem(item);
                if (cmbEndBook   != null) cmbEndBook.addItem(item);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void calculateDays() {
        try {
            String readerItem = (String) cmbEndReader.getSelectedItem();
            String bookItem   = (String) cmbEndBook.getSelectedItem();
            if (readerItem == null || bookItem == null) return;
            int readerID = Integer.parseInt(readerItem.split(" - ")[0]);
            int bookID   = Integer.parseInt(bookItem.split(" - ")[0]);
            String endDateStr = new SimpleDateFormat("yyyy-MM-dd").format((Date) spinnerEndDate.getValue());
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT issue_date FROM issue WHERE reader_id=? AND book_id=? ORDER BY issue_id DESC LIMIT 1"
            );
            ps.setInt(1, readerID); ps.setInt(2, bookID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                LocalDate start = rs.getDate("issue_date").toLocalDate();
                LocalDate end   = LocalDate.parse(endDateStr);
                lblDaysTaken.setText(ChronoUnit.DAYS.between(start, end) + " days");
            } else {
                lblDaysTaken.setText("No start date found!");
            }
            conn.close();
        } catch (Exception e) { lblDaysTaken.setText("Error"); }
    }

    private void loadReaders() {
        readersModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT r.reader_id, r.name, COUNT(rt.return_id) AS books_read " +
                           "FROM readers r LEFT JOIN issue i ON r.reader_id = i.reader_id " +
                           "LEFT JOIN returns rt ON i.issue_id = rt.issue_id " +
                           "GROUP BY r.reader_id, r.name";
            ResultSet rs = conn.createStatement().executeQuery(query);
            while (rs.next())
                readersModel.addRow(new Object[]{
                    rs.getInt("reader_id"),
                    rs.getString("name"),
                    rs.getInt("books_read")
                });
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void addReader() {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO readers (name) VALUES (?)");
            ps.setString(1, txtReaderName.getText()); ps.executeUpdate();
            loadReaders(); clearReaderFields(); refreshDropdowns();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void editReader() {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE readers SET name=? WHERE reader_id=?");
            ps.setString(1, txtReaderName.getText());
            ps.setInt(2, Integer.parseInt(txtReaderID.getText())); ps.executeUpdate();
            loadReaders(); clearReaderFields(); refreshDropdowns();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void deleteReader() {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM readers WHERE reader_id=?");
            ps.setInt(1, Integer.parseInt(txtReaderID.getText())); ps.executeUpdate();
            conn.createStatement().executeUpdate("ALTER TABLE readers AUTO_INCREMENT = 1");
            loadReaders(); clearReaderFields(); refreshDropdowns();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void clearReaderFields() { txtReaderID.setText(""); txtReaderName.setText(""); }

    private void loadBooks() {
        booksModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM books");
            while (rs.next())
                booksModel.addRow(new Object[]{rs.getInt("book_id"), rs.getString("title")});
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void addBook() {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO books (title) VALUES (?)");
            ps.setString(1, txtBookName.getText()); ps.executeUpdate();
            loadBooks(); clearBookFields(); refreshDropdowns();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void editBook() {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE books SET title=? WHERE book_id=?");
            ps.setString(1, txtBookName.getText());
            ps.setInt(2, Integer.parseInt(txtBookID.getText())); ps.executeUpdate();
            loadBooks(); clearBookFields(); refreshDropdowns();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void deleteBook() {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM books WHERE book_id=?");
            ps.setInt(1, Integer.parseInt(txtBookID.getText())); ps.executeUpdate();
            conn.createStatement().executeUpdate("ALTER TABLE books AUTO_INCREMENT = 1");
            loadBooks(); clearBookFields(); refreshDropdowns();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void clearBookFields() { txtBookID.setText(""); txtBookName.setText(""); }

    private void loadIssues() {
        issueModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT i.issue_id, r.name, b.title, i.issue_date FROM issue i " +
                "JOIN readers r ON i.reader_id=r.reader_id JOIN books b ON i.book_id=b.book_id"
            );
            while (rs.next())
                issueModel.addRow(new Object[]{rs.getInt("issue_id"), rs.getString("name"), rs.getString("title"), rs.getDate("issue_date")});
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void addIssue() {
        try (Connection conn = DBConnection.getConnection()) {
            String readerItem = (String) cmbIssueReader.getSelectedItem();
            String bookItem   = (String) cmbIssueBook.getSelectedItem();
            if (readerItem == null || bookItem == null) return;
            int readerID = Integer.parseInt(readerItem.split(" - ")[0]);
            int bookID   = Integer.parseInt(bookItem.split(" - ")[0]);
            String dateStr = new SimpleDateFormat("yyyy-MM-dd").format((Date) spinnerStartDate.getValue());
            PreparedStatement ps = conn.prepareStatement("INSERT INTO issue (reader_id, book_id, issue_date) VALUES (?, ?, ?)");
            ps.setInt(1, readerID); ps.setInt(2, bookID);
            ps.setDate(3, java.sql.Date.valueOf(dateStr)); ps.executeUpdate();
            loadIssues();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void deleteIssue() {
        try {
            int row = issueTable.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a row to delete!"); return; }
            int issueID = (int) issueModel.getValueAt(row, 0);
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM issue WHERE issue_id=?");
            ps.setInt(1, issueID); ps.executeUpdate();
            conn.createStatement().executeUpdate("ALTER TABLE issue AUTO_INCREMENT = 1");
            conn.close(); loadIssues();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void clearIssueFields() {
        if (cmbIssueReader.getItemCount() > 0) cmbIssueReader.setSelectedIndex(0);
        if (cmbIssueBook.getItemCount()   > 0) cmbIssueBook.setSelectedIndex(0);
    }

    private void loadReturns() {
        returnsModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT rt.return_id, r.name, b.title, rt.end_date, rt.days_taken FROM returns rt " +
                "JOIN issue i ON rt.issue_id=i.issue_id " +
                "JOIN readers r ON i.reader_id=r.reader_id JOIN books b ON i.book_id=b.book_id"
            );
            while (rs.next())
                returnsModel.addRow(new Object[]{rs.getInt("return_id"), rs.getString("name"), rs.getString("title"), rs.getDate("end_date"), rs.getInt("days_taken")});
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void addReturn() {
        try {
            String readerItem = (String) cmbEndReader.getSelectedItem();
            String bookItem   = (String) cmbEndBook.getSelectedItem();
            if (readerItem == null || bookItem == null) return;
            int readerID = Integer.parseInt(readerItem.split(" - ")[0]);
            int bookID   = Integer.parseInt(bookItem.split(" - ")[0]);
            String endDateStr = new SimpleDateFormat("yyyy-MM-dd").format((Date) spinnerEndDate.getValue());
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps2 = conn.prepareStatement(
                "SELECT issue_id, issue_date FROM issue WHERE reader_id=? AND book_id=? ORDER BY issue_id DESC LIMIT 1"
            );
            ps2.setInt(1, readerID); ps2.setInt(2, bookID);
            ResultSet rs = ps2.executeQuery();
            if (rs.next()) {
                int issueID = rs.getInt("issue_id");
                LocalDate start = rs.getDate("issue_date").toLocalDate();
                LocalDate end   = LocalDate.parse(endDateStr);
                int days = (int) ChronoUnit.DAYS.between(start, end);
                PreparedStatement ps = conn.prepareStatement("INSERT INTO returns (issue_id, end_date, days_taken) VALUES (?, ?, ?)");
                ps.setInt(1, issueID); ps.setDate(2, java.sql.Date.valueOf(endDateStr)); ps.setInt(3, days);
                ps.executeUpdate();
                lblDaysTaken.setText(days + " days");
                loadReturns(); loadReaders();
                clearReturnFields();
            } else {
                JOptionPane.showMessageDialog(this, "No start date found for this reader and book!");
            }
            conn.close();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void deleteReturn() {
        try {
            int row = returnsTable.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a row to delete!"); return; }
            int returnID = (int) returnsModel.getValueAt(row, 0);
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM returns WHERE return_id=?");
            ps.setInt(1, returnID); ps.executeUpdate();
            conn.createStatement().executeUpdate("ALTER TABLE returns AUTO_INCREMENT = 1");
            conn.close(); loadReturns(); loadReaders();
            clearReturnFields();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void clearReturnFields() {
        lblDaysTaken.setText("-");
        if (cmbEndReader.getItemCount() > 0) cmbEndReader.setSelectedIndex(0);
        if (cmbEndBook.getItemCount()   > 0) cmbEndBook.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Dashboard());
    }
}