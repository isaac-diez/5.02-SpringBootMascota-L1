package cat.itacademy.s05.t02.n01.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncryptPassword {

    public static void main(String[] args) {

        String pass = "123";
        System.out.println("Password: " + pass);
        System.out.println("Encripted password: " + encriptPassword(pass));

    }

        public static String encriptPassword(String password) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            return encoder.encode(password);
        }

}
