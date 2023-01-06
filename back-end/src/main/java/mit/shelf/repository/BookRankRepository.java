package mit.shelf.repository;

import mit.shelf.domain.BookRank;

import java.util.Optional;

public interface BookRankRepository {

    BookRank save(BookRank bookRank);
    Optional<BookRank> findById(Long id);

}
