package cat.itacademy.s05.t02.n01.exception;

public class UsernameAlreadyInDataBaseException extends RuntimeException {
    public UsernameAlreadyInDataBaseException(String message) {
        super(message);
    }
}
