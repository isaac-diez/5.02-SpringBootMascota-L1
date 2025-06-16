package cat.itacademy.s05.t02.n01.exception;

public class EmptyPetListException extends RuntimeException {
    public EmptyPetListException(String message) {
        super(message);
    }
}
