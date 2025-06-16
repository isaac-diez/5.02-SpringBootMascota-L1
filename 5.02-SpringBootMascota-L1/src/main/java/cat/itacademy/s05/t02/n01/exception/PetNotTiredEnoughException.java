package cat.itacademy.s05.t02.n01.exception;

public class PetNotTiredEnoughException extends RuntimeException {
    public PetNotTiredEnoughException(String message) {
        super(message);
    }
}
