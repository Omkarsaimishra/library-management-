import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LibSysGUI {
    private List<Book> books;
    private List<Member> members;
    private JFrame frame;
    private JTextArea textArea;
    private JTextField titleField, authorField, memberNameField, borrowTitleField, returnTitleField;

    public LibSysGUI() {
        books = new ArrayList<>();
        members = new ArrayList<>();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Library Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2));

        // Add Book
        titleField = new JTextField();
        authorField = new JTextField();
        JButton addBookButton = new JButton("Add Book");
        addBookButton.addActionListener(e -> addBook());

        panel.add(new JLabel("Book Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Book Author:"));
        panel.add(authorField);
        panel.add(addBookButton);

        // Add Member
        memberNameField = new JTextField();
        JButton addMemberButton = new JButton("Add Member");
        addMemberButton.addActionListener(e -> addMember());

        panel.add(new JLabel("Member Name:"));
        panel.add(memberNameField);
        panel.add(addMemberButton);

        // Borrow Book
        borrowTitleField = new JTextField();
        JTextField borrowMemberField = new JTextField();
        JButton borrowBookButton = new JButton("Borrow Book");
        borrowBookButton.addActionListener(e -> borrowBook(borrowTitleField.getText(), borrowMemberField.getText()));

        panel.add(new JLabel("Borrow Book Title:"));
        panel.add(borrowTitleField);
        panel.add(new JLabel("Borrow Member Name:"));
        panel.add(borrowMemberField);
        panel.add(borrowBookButton);

        // Return Book
        returnTitleField = new JTextField();
        JTextField returnMemberField = new JTextField();
        JButton returnBookButton = new JButton("Return Book");
        returnBookButton.addActionListener(e -> returnBook(returnTitleField.getText(), returnMemberField.getText()));

        panel.add(new JLabel("Return Book Title:"));
        panel.add(returnTitleField);
        panel.add(new JLabel("Return Member Name:"));
        panel.add(returnMemberField);
        panel.add(returnBookButton);

        // List Books and Members
        JButton listBooksButton = new JButton("List All Books");
        listBooksButton.addActionListener(e -> listBooks());

        JButton listMembersButton = new JButton("List All Members");
        listMembersButton.addActionListener(e -> listMembers());

        panel.add(listBooksButton);
        panel.add(listMembersButton);

        frame.add(panel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private void addBook() {
        String title = titleField.getText();
        String author = authorField.getText();
        if (!title.isEmpty() && !author.isEmpty()) {
            books.add(new Book(title, author));
            textArea.append("Book added: " + title + " by " + author + "\n");
            titleField.setText("");
            authorField.setText("");
        } else {
            JOptionPane.showMessageDialog(frame, "Please enter both title and author.");
        }
    }

    private void addMember() {
        String name = memberNameField.getText();
        if (!name.isEmpty()) {
            members.add(new Member(name));
            textArea.append("Member registered: " + name + "\n");
            memberNameField.setText("");
        } else {
            JOptionPane.showMessageDialog(frame, "Please enter a member name.");
        }
    }

    private void borrowBook(String title, String memberName) {
        Book book = findBook(title);
        Member member = findMember(memberName);
        if (book != null && member != null) {
            if (!book.isBorrowed()) {
                book.setBorrowed(true);
                member.borrowBook(book);
                textArea.append("Book borrowed: " + title + " by " + memberName + "\n");
            } else {
                JOptionPane.showMessageDialog(frame, "Book is already borrowed.");
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Book or Member not found.");
        }
    }

    private void returnBook(String title, String memberName) {
        Book book = findBook(title);
        Member member = findMember(memberName);
        if (book != null && member != null) {
            if (member.getBorrowedBooks().contains(book)) {
                book.setBorrowed(false);
                member.returnBook(book);
                textArea.append("Book returned: " + title + " by " + memberName + "\n");
            } else {
                JOptionPane.showMessageDialog(frame, "Member did not borrow this book.");
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Book or Member not found.");
        }
    }

    private void listBooks() {
        textArea.append("List of Books:\n");
        for (Book book : books) {
            textArea.append("Title: " + book.getTitle() + ", Author: " + book.getAuthor() + ", Borrowed: " + (book.isBorrowed() ? "Yes" : "No") + "\n");
        }
    }

    private void listMembers() {
        textArea.append("List of Members:\n");
        for (Member member : members) {
            textArea.append("Name: " + member.getName() + ", Borrowed Books: " + member.getBorrowedBooks().size() + "\n");
        }
    }

    private Book findBook(String title) {
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                return book;
            }
        }
        return null;
    }

    private Member findMember(String name) {
        for (Member member : members) {
            if (member.getName().equalsIgnoreCase(name)) {
                return member;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LibSysGUI::new);
    }
}

class Book {
    private String title;
    private String author;
    private boolean isBorrowed;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
        this.isBorrowed = false;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isBorrowed() {
        return isBorrowed;
    }

    public void setBorrowed(boolean isBorrowed) {
        this.isBorrowed = isBorrowed;
    }
}

class Member {
    private String name;
    private List<Book> borrowedBooks;

    public Member(String name) {
        this.name = name;
        this.borrowedBooks = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void borrowBook(Book book) {
        borrowedBooks.add(book);
    }

    public void returnBook(Book book) {
        borrowedBooks.remove(book);
    }
}