package Model;

import java.util.Objects;
import java.util.UUID;

public abstract class User {

    private String userId;
    private String name;
    private String email;
    private String password;
    private String role;
    private boolean isLoggedIn;

    public User(String name, String email, String password, String role) {
        this.userId = UUID.randomUUID().toString().substring(0, 8);
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.isLoggedIn = false;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isLoggedIn() { return isLoggedIn; }
    public void setLoggedIn(boolean loggedIn) { isLoggedIn = loggedIn; }

    public abstract void displayDashboard();

    public boolean login(String email, String password) {
        if (this.email.equals(email) && this.password.equals(password)) {
            this.isLoggedIn = true;
            return true;
        }
        return false;
    }

    public void logout() {
        this.isLoggedIn = false;
    }

    @Override
    public String toString() {
        return String.format("User{id='%s', name='%s', email='%s', role='%s'}",
                userId, name, email, role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}