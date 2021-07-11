package App.BusinessLogic.UserManagement;

public class UserManager {
    private App.BusinessLogic.UserManagement.User currentUser;

    public void fakeLogin(String username) //TODO: bisogna implementare il login vero!
    {
        this.currentUser = App.BusinessLogic.UserManagement.User.loadUser(username);
    }

    public App.BusinessLogic.UserManagement.User getCurrentUser() {
        return this.currentUser;
    }
}
