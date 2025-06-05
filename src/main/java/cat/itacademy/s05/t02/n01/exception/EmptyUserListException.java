package cat.itacademy.s05.t02.n01.exception;

public class EmptyUserListException extends RuntimeException {
    public EmptyUserListException(String message) {
        super(message);
    }
}
