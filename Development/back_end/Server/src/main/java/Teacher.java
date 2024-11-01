public class Teacher extends User {

    // Constructor
    public Teacher() {
        super();
    }

    public Teacher(String name, String userID, Avatar avatar) {
        super(name, userID, avatar);
    }

    public ScienceClass createClass() {
        return new ScienceClass(this);
    }

}
