package cat.itacademy.s05.t02.n01.exception;

public class UserIdNotFoundException extends RuntimeException {
    public UserIdNotFoundException(int id) {
        super("User with ID " + id + "not found in DB");
    }
}
