package app.user;

/**
 * A registration form. Used as a request body.
 */
class RegistrationForm {
    private final String username;
    private final String password;

    public RegistrationForm(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
