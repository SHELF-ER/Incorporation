import axios from "axios";

const USER_API_BASE_URL = "http://localhost:8080/api/user";
const BOOK_API_BASE_URL = "http://localhost:8080/api/book";

class ApiService {
  // -----------------------------USER---------------------------------
  //userList
  fetchUsers() {
    return axios.get(USER_API_BASE_URL + "s");
  }

  //user 한명 정보
  fetchUserByID(userID) {
    return axios.get(USER_API_BASE_URL + "/" + userID);
  }

  //user 한명 삭제
  deleteUser(userID) {
    return axios.delete(USER_API_BASE_URL + "/" + userID);
  }

  //user 한명 추가
  addUser(user) {
    return axios.post(USER_API_BASE_URL, "/" +user);
  }

  //user 한명 수정
  editUser(user) {
    return axios.put(USER_API_BASE_URL + "/" + user.id, user);
  }

  // -----------------------------BOOK---------------------------------
  fetchBooks() {
    return axios.get(BOOK_API_BASE_URL + "s");
  }

  fetchBookByID(bookID) {
    return axios.get(BOOK_API_BASE_URL + "/" + bookID);
  }

  deleteBook(bookID) {
    return axios.delete(BOOK_API_BASE_URL + "/" + bookID);
  }

  addBook(book) {
    return axios.post(BOOK_API_BASE_URL, "/" +book);
  }

  editBook(book) {
    return axios.put(BOOK_API_BASE_URL + "/" + book.id, book);
  }

}

export default new ApiService();
