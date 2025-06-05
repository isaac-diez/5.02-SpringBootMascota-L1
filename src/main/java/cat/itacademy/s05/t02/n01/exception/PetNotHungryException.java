package cat.itacademy.s05.t02.n01.exception;

public class PetNotHungryException extends RuntimeException {
    public PetNotHungryException(String message) {
        super(message);
    }
}
