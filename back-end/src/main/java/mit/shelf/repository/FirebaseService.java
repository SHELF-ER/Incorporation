package mit.shelf.repository;

import mit.shelf.domain.Book;

public interface FirebaseService {

    public String insertBook(Book book) throws Exception;

    public Book getBookDetail(Long id) throws Exception;

    public String updateBook(Book book) throws Exception;

    public String deleteBook(Long id) throws Exception;
}
