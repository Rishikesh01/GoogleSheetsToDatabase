package Testing;

public class User {
        private int id;
        private String name;
        private String email;
        private String gender;
        private String sport;

    public User(int id, String name, String email, String gender, String sport) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.sport = sport;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String Gender) {
        gender = Gender;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String Sport) {
        sport = Sport;
    }
}

