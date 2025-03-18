import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

public class libgui {
    private LibSys library;
    private JFrame frame;
    private JTextArea displayArea;

    public libgui() {
        library = new LibSys();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Library Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Create buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(7, 1, 5, 5));
        
        String[] buttonLabels = {
            "Add Book", "Add Member", "Borrow Book",
            "Return Book", "List Books", "List Members", "Exit"
        };

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.addActionListener(new ButtonClickListener());
            buttonPanel.add(button);
        }

        // Create display area
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);

        // Add components to frame
        frame.getContentPane().add(buttonPanel, BorderLayout.WEST);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private static boolean showLoginDialog() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        int option = JOptionPane.showConfirmDialog(
            null, 
            panel, 
            "Login", 
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            return "admin".equals(username) && "admin".equals(password);
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            if (showLoginDialog()) {
                new libgui();
            } else {
                JOptionPane.showMessageDialog(null, "Invalid credentials. Exiting.");
                System.exit(0);
            }
        });
    }

    // Inner classes for library functionality
    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            
            switch (command) {
                case "Add Book": addBook(); break;
                case "Add Member": addMember(); break;
                case "Borrow Book": borrowBook(); break;
                case "Return Book": returnBook(); break;
                case "List Books": listBooks(); break;
                case "List Members": listMembers(); break;
                case "Exit": System.exit(0); break;
            }
        }
    }

    private void addBook() {
        String title = JOptionPane.showInputDialog("Enter book title:");
        String author = JOptionPane.showInputDialog("Enter book author:");
        if (title != null && author != null && !title.isEmpty() && !author.isEmpty()) {
            library.addBook(new Book(title, author));
            displayArea.append("Book added: " + title + " by " + author + "\n");
        }
    }

    private void addMember() {
        String name = JOptionPane.showInputDialog("Enter member name:");
        if (name != null && !name.isEmpty()) {
            library.registerMember(new Member(name));
            displayArea.append("Member added: " + name + "\n");
        }
    }

    private void borrowBook() {
        // Get list of available books and members
        List<Book> availableBooks = library.listBooks().stream()
                .filter(book -> !book.isBorrowed())
                .collect(Collectors.toList());
        List<Member> members = library.listMembers();

        if (availableBooks.isEmpty()) {
            displayArea.append("No books available for borrowing.\n");
            return;
        }
        if (members.isEmpty()) {
            displayArea.append("No registered members.\n");
            return;
        }

        // Create book selection dialog
        String[] bookOptions = availableBooks.stream()
                .map(book -> book.getTitle() + " by " + book.getAuthor())
                .toArray(String[]::new);
        String selectedBook = (String) JOptionPane.showInputDialog(
                frame,
                "Select a book:",
                "Borrow Book",
                JOptionPane.QUESTION_MESSAGE,
                null,
                bookOptions,
                bookOptions[0]);

        if (selectedBook == null) return;

        // Create member selection dialog
        String[] memberOptions = members.stream()
                .map(Member::getName)
                .toArray(String[]::new);
        String selectedMember = (String) JOptionPane.showInputDialog(
                frame,
                "Select a member:",
                "Borrow Book",
                JOptionPane.QUESTION_MESSAGE,
                null,
                memberOptions,
                memberOptions[0]);

        if (selectedMember == null) return;

        // Find selected book and member objects
        Book book = availableBooks.get(Arrays.asList(bookOptions).indexOf(selectedBook));
        Member member = members.get(Arrays.asList(memberOptions).indexOf(selectedMember));

        // Borrow the book
        if (library.borrowBook(book, member)) {
            displayArea.append("Book borrowed: " + book.getTitle() + " by " + book.getAuthor() + " to " + member.getName() + "\n");
        } else {
            displayArea.append("Failed to borrow book: " + book.getTitle() + " by " + book.getAuthor() + "\n");
        }
    }

    private void returnBook() {
        // Implementation for returning a book
        displayArea.append("Return book functionality to be implemented\n");
    }

    private void listBooks() {
        displayArea.append("\nBook List:\n");
        for (Book book : library.listBooks()) {
            displayArea.append(book.getTitle() + " by " + book.getAuthor() + 
                             (book.isBorrowed() ? " (Borrowed)" : " (Available)") + "\n");
        }
    }

    private void listMembers() {
        displayArea.append("\nMember List:\n");
        for (Member member : library.listMembers()) {
            displayArea.append(member.getName() + "\n");
        }
    }

    // Library system classes
    static class LibSys {
        private List<Book> books = new ArrayList<>();
        private List<Member> members = new ArrayList<>();

        public void addBook(Book book) {
            books.add(book);
        }

        public void registerMember(Member member) {
            members.add(member);
        }

        public boolean borrowBook(Book book, Member member) {
            if (!book.isBorrowed()) {
                book.setBorrowed(true);
                member.borrowBook(book);
                return true;
            }
            return false;
        }

        public boolean returnBook(Book book, Member member) {
            if (member.getBorrowedBooks().contains(book)) {
                book.setBorrowed(false);
                member.returnBook(book);
                return true;
            }
            return false;
        }

        public List<Book> listBooks() {
            return books;
        }

        public List<Member> listMembers() {
            return members;
        }
    }

    static class Book {
        private String title;
        private String author;
        private boolean borrowed;

        public Book(String title, String author) {
            this.title = title;
            this.author = author;
            this.borrowed = false;
        }

        public String getTitle() { return title; }
        public String getAuthor() { return author; }
        public boolean isBorrowed() { return borrowed; }
        public void setBorrowed(boolean status) { borrowed = status; }
    }

    static class Member {
        private String name;
        private List<Book> borrowedBooks = new ArrayList<>();

        public Member(String name) {
            this.name = name;
        }

        public String getName() { return name; }
        public List<Book> getBorrowedBooks() { return borrowedBooks; }
        
        public void borrowBook(Book book) {
            borrowedBooks.add(book);
        }

        public void returnBook(Book book) {
            borrowedBooks.remove(book);
        }
    }
}