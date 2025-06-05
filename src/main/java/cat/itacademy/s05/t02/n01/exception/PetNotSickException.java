package cat.itacademy.s05.t02.n01.exception;

public class PetNotSickException extends RuntimeException {
    public PetNotSickException(String message) {
        super(message);
    }
}
