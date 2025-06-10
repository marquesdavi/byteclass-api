package br.com.marques.byteclass.feature.util;

import br.com.marques.byteclass.feature.user.port.dto.LoginRequest;
import br.com.marques.byteclass.feature.user.port.dto.UserRequest;

public class UserTestUtils {
    public static class UserRequestBuilder {
        private String name = "John Doe";
        private String email = "john.doe@example.com";
        private String password = "S3cr3tP@ss";

        public UserRequestBuilder name(String n) { this.name = n; return this; }
        public UserRequestBuilder email(String e) { this.email = e; return this; }
        public UserRequestBuilder password(String p) { this.password = p; return this; }

        public UserRequest build() {
            return new UserRequest(name, email, password);
        }
    }

    public static class LoginRequestBuilder {
        private String email = "user@example.com";
        private String password = "P@ssw0rd";

        public LoginRequestBuilder email(String e) { this.email = e; return this; }
        public LoginRequestBuilder password(String p) { this.password = p; return this; }

        public LoginRequest build() {
            return new LoginRequest(email, password);
        }
    }
}
