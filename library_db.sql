#create database
CREATE DATABASE library_db;
USE library_db;

#create tables
CREATE TABLE readers (
    reader_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    books_read INT DEFAULT 0
);

CREATE TABLE books (
    book_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100),
    author VARCHAR(100)
);

CREATE TABLE issue (
    issue_id INT PRIMARY KEY AUTO_INCREMENT,
    reader_id INT,
    book_id INT,
    issue_date DATE,
    FOREIGN KEY (reader_id) REFERENCES readers(reader_id),
    FOREIGN KEY (book_id) REFERENCES books(book_id)
);

CREATE TABLE returns (
    return_id INT PRIMARY KEY AUTO_INCREMENT,
    issue_id INT,
    return_date DATE,
    start_date DATE,
    end_date DATE,
    FOREIGN KEY (issue_id) REFERENCES issue(issue_id)
);

SHOW TABLES;