package mit.shelf.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.ApiOperation;
import mit.shelf.domain.Book;
import mit.shelf.domain.User;
import mit.shelf.repository.LibUserRepository;
import mit.shelf.repository.BookRepository;
import mit.shelf.repository.UserRepository;
import mit.shelf.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class robotApiController {
//https://ggomi.github.io/boot-react/
// https://velog.io/@u-nij/Spring-Boot-React.js-%EA%B0%9C%EB%B0%9C%ED%99%98%EA%B2%BD-%EC%84%B8%ED%8C%85

    @Autowired
    BookService bookService;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    LibUserRepository libUserRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/hello")
    public String testApi() {
        return "Hello, world!";
    }

    @ApiOperation(value = "기부받은 책을 입력한다.(필수: name, donor)")
    @PostMapping(value = "/book/donate")
    public Map<String,String> donateBook(Book form){
        Book book = new Book();
        book.setName(form.getName());
        book.setDonor(form.getDonor());
        bookService.join(book);
        Map<String,String> result = new HashMap<>();
        result.put("result","success");
        return result;
    }

    @ApiOperation(value = "모든 책 정보")
    @GetMapping(value = "books/error")
    public ArrayList<Map<String, String>> errorBookAPI(){
        List<Book> books = bookService.findMembers();
        ArrayList<Map<String,String>> list=new ArrayList<>();

        for (Book book : books) {
            Map<String, String> result = new HashMap<>();
            result.put("id", String.valueOf(book.getId()));
            result.put("name", String.valueOf(book.getName()));
            result.put("cmp", String.valueOf(book.getBookCmp()));
            result.put("floor", String.valueOf(book.getBookFloor()));
            list.add(result);
        }
        return list;
    }

    @ApiOperation(value = "책 반납")
    @RequestMapping(value = "/book/return/{smartUid}", method = RequestMethod.GET)
    public Map<String, String> findUUid(@PathVariable String smartUid) {

        Optional<Book> user = bookRepository.findBySmartUid(smartUid);
        Optional<User> userN = libUserRepository.findByUUid(smartUid);
        Map<String, String> list = new HashMap<>();

        if (user.isPresent() && userN.isPresent()) {
            Book userIId = user.get();
            User borrowUser = userN.get();
            userIId.setBorrower("X");

            String tmp1 = borrowUser.getBorrow1();
            String tmp2 = borrowUser.getBorrow2();
            String tmp3 = borrowUser.getBorrow3();

            if (tmp1.equals(smartUid)) {
                borrowUser.setBorrow1("X");
            }
            else if (tmp2.equals(smartUid)) {
                borrowUser.setBorrow2("X");
            }
            else if (tmp3.equals(smartUid)) {
                borrowUser.setBorrow3("X");
            }
            bookRepository.save(userIId);
            libUserRepository.save(borrowUser);
            list.put("book", String.valueOf(userIId.getBookNum()));
        } else {
            list.put("book", "error");
        }

        return list;
    }

    @ApiOperation(value = "책 대여한 회원명 조회")
    @RequestMapping(value = "/book/return/check/{smartUid}/{userUid}", method = RequestMethod.GET)
    public Map<String, String> findBookName(@PathVariable String smartUid, @PathVariable String userUid) {
        Optional<Book> member = bookRepository.findBySmartUid(smartUid);
        Optional<User> user = libUserRepository.findByUidU(userUid);
        Map<String, String> list = new HashMap<>();

        if (member.isPresent()&& user.isPresent()) {
            Book userIId = member.get();
            User userId = user.get();
            if (userIId.getBorrower().equals(userId.getName())) {
                list.put("book", String.valueOf(userIId.getName()));
            }
            else {
                list.put("book","cant");
            }
        } else {
            list.put("book", "error");
        }
        return list;
    }

    @ApiOperation(value = "책 대여 가능여부")
    @RequestMapping(value = "/book/lend/check/{smartUid}/{userUid}", method = RequestMethod.GET)
    public Map<String, String> findBookName2(@PathVariable String smartUid, @PathVariable String userUid) {
        Optional<Book> member = bookRepository.findBySmartUid(smartUid);
        Optional<User> user = libUserRepository.findByUidU(userUid);
        Map<String, String> list = new HashMap<>();

        if (member.isPresent() && user.isPresent()) {
            Book userIId = member.get();
            User userId = user.get();
            if (!userId.getBorrow1().equals("X") && !userId.getBorrow2().equals("X") && !userId.getBorrow3().equals("X")) {
                list.put("book","full");
            }
            else {
                if (userIId.getBorrower().equals("X")) {
                    list.put("book", String.valueOf(userIId.getName()));
                } else {
                    list.put("book", "cant");
                }
            }
        } else {
            list.put("book", "error");
        }
        return list;
    }

    @ApiOperation(value = "책 대여 정보(도서 제목, 대여 회원명)")
    @RequestMapping(value = "/book/lendList/{smartUid}", method = RequestMethod.GET)
    public Map<String, String> findBorrower(@PathVariable String smartUid) {
        Optional<Book> user = bookRepository.findBySmartUid(smartUid);
        Map<String, String> list = new HashMap<>();

        if (user.isPresent()) {
            Book userIId = user.get();
            list.put("cateNum", String.valueOf(userIId.getBookNum()));
            list.put("bookName", String.valueOf(userIId.getName()));

        } else {
            list.put("Lend", "error");
        }
        return list;
    }

    @ApiOperation(value = "책 여러권 대여")
    @RequestMapping(value = "/book/lend/{userUid}/{smartUid}", method = RequestMethod.GET)
    public Map<String, String> lendBook(@PathVariable String userUid,@PathVariable String smartUid) {
        Optional<Book> book = bookRepository.findBySmartUid(smartUid);
        Optional<User> user = libUserRepository.findByUidU(userUid);
        Map<String, String> list = new HashMap<>();

        if (book.isPresent() && user.isPresent()) {
            Book bookId = book.get();
            bookId.setBorrower(user.get().getName());
            bookId.setCount(bookId.getCount()+1);
            bookRepository.save(bookId);
            User userId = user.get();
            if (userId.getBorrow1().equals("X") || (userId.getBorrow1().equals("대여가능"))) {
                userId.setBorrow1(smartUid);
                libUserRepository.save(userId);
                list.put("result", "success");
                list.put("book", String.valueOf(bookId.getName()));
                list.put("user",String.valueOf(user.get().getName()));
            }
            else if (userId.getBorrow2().equals("X") || (userId.getBorrow2().equals("대여가능"))) {
                userId.setBorrow2(smartUid);
                libUserRepository.save(userId);
                list.put("result", "success");
                list.put("book", String.valueOf(bookId.getName()));
                list.put("user",String.valueOf(user.get().getName()));
            }
            else if (userId.getBorrow3().equals("X") || (userId.getBorrow3().equals("대여가능"))) {
                userId.setBorrow3(smartUid);
                libUserRepository.save(userId);
                list.put("result", "success");
                list.put("book", String.valueOf(bookId.getName()));
                list.put("user", String.valueOf(user.get().getName()));
            }
        } else {
            list.put("result", "error");
        }
        return list;
    }

    @ApiOperation(value = "책 1권 대여")
    @RequestMapping(value = "/book/borrow/{bookUid}/{userUid}", method = RequestMethod.GET)
    public Map<String, String> borrowBook(@PathVariable String bookUid,@PathVariable Long userUid) {
        Optional<Book> user = bookRepository.findByUid(bookUid);
        Optional<User> userName = libUserRepository.findById(userUid);
        Map<String, String> list = new HashMap<>();

        if (user.isPresent() && userName.isPresent()) {
            Book userIId = user.get();
            User borrowUser = userName.get();
            userIId.setBorrower(borrowUser.getName());
            borrowUser.setBorrow1(userIId.getName());
            bookRepository.save(userIId);
            libUserRepository.save(borrowUser);
            list.put("book", String.valueOf(userIId.getBookNum()));
        } else {
            list.put("book", "error");
        }

        return list;
    }

    @ApiOperation(value = "책 uid 처음 입력용(1층만)")
    @PostMapping("/books/uid")
    public Map<String, String> update(@RequestBody ArrayList<ArrayList<String>> robot) throws JsonProcessingException {
        for (int k = 0; k < robot.size(); k++) {
            ArrayList<String> uidList = robot.get(k);
            Integer countBook = userRepository.countAll();
            int roof = Math.min(countBook, uidList.size());
            List<Book> books = bookRepository.findAllByBookFloor(k+1);
            for (int i = 0; i < roof; i++) {
                books.get(i).setUid(uidList.get(i));
                books.get(i).setBookFloor(k +1);
                bookRepository.save(books.get(i));
            }
        }
        Map<String, String> list = new HashMap<>();
        list.put("result", "True");
        return list;
    }

    @ApiOperation(value = "책 uid 처음 입력용(2층까지)")
    @PostMapping("/books/uidTest")
    public ArrayList<String> updateTest(@RequestBody ArrayList<ArrayList<String>> robot) throws JsonProcessingException {
        ArrayList<String> errorB = new ArrayList<>();
        Map<String, String> list = new HashMap<>();
        for (int k = 0; k < robot.size(); k++) {
            ArrayList<String> robotUids = robot.get(k);
            List<Book> books = bookRepository.findAllByBookFloor(k+1);
            int roof = Math.min(robotUids.size(), books.size());
            for (int i = 0; i < roof; i++) {
                String memberUid = books.get(i).getUid();
                if (!(memberUid).equals(robotUids.get(i))) {
                    Optional<Book> member = bookRepository.findByUid(memberUid);
                    errorB.add(member.get().getUid());
                }
            }
            if (insertErrorBook(errorB)) {
                list.put("result", "true");
            } else {
                list.put("result", "false");
            }
        }
        return errorB;
    }

    @ApiOperation(value = "잘못된 책 확인")
    @PostMapping("/books/check")
    public Map<String, String> listCheck(@RequestBody Map<String, ArrayList<String>> robotUid) throws JsonProcessingException {
        ArrayList<String> uidList;
        uidList = robotUid.get("id");

        List<String> bookAllUid = userRepository.selectAllUid();
        ArrayList<String> errorB = new ArrayList<>();
        Map<String, String> list = new HashMap<>();
        Integer countBook = userRepository.countAll();

        for (int i = 0; i < countBook; i++) {
            int finalI = i;
            ArrayList<String> finalUidList = uidList;
            if (!(finalUidList.get(finalI).equals(bookAllUid.get(finalI)))) {
                Optional<Book> member = bookRepository.findByUid(bookAllUid.get(finalI));
                errorB.add(member.get().getUid());
            }
        }
        if (insertErrorBook(errorB)) {
            list.put("result", "true");
        } else {
            list.put("result", "false");
        }
        return list;
    }

    public Boolean insertErrorBook(ArrayList<String> eb) {
        for (int i = 0; i <= eb.size() - 1; i++) {
            Optional<Book> updateUser = bookRepository.findByUid(eb.get(i));
            updateUser.ifPresent(selectUser -> {
                selectUser.setBookCmp(Long.valueOf(1));
                bookRepository.save(selectUser);
            });
        }
        return true;
    }

    @ApiOperation(value = "책 1권 대여(구버전)")
    @RequestMapping(value = "/book/rent", method = RequestMethod.GET)
    public Map<String, String> rentBook(@RequestParam(value = "bookUid") String bookUid,
                                        @RequestParam(value = "userUid") String userUid) {
        Optional<Book> book = bookRepository.findByUid(bookUid);
        Optional<User> user = libUserRepository.findByUid(userUid);
        Map<String, String> list = new HashMap<>();

        if (book.isPresent() && user.isPresent()) {
            Book bookId = book.get();
            bookId.setBorrower(user.get().getName());
            bookRepository.save(bookId);
            list.put("result", "success");
            list.put("book", String.valueOf(bookId.getName()));
            list.put("user",String.valueOf(user.get().getName()));
        } else {
            list.put("result", "error");
        }
        return list;
    }

    @RequestMapping(value = "/member/{uid}", method = RequestMethod.GET)
    public Map<String, String> search(@PathVariable String uid) {
        Optional<User> users = libUserRepository.findByUidU(uid);
        Map<String, String> list = new HashMap<>();
        if (users.isPresent()) {
            String  userName = users.get().getName();
            list.put("Name", userName);
        } else {
            list.put("Name", "error");
        }
        return list;
    }
}
