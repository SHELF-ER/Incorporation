package mit.shelf.Controller;

import mit.shelf.domain.Book;
import mit.shelf.repository.book.RecommendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class RecommendController {
    @Autowired
    RecommendRepository recommendRepository;

    @GetMapping(value = "/bookRecommend") public String recommend(Model model) {
        List<Book> result = recommendRepository.recommendBook();
        model.addAttribute("recommends", result);
        return "/books/recommendList";
    }
}
