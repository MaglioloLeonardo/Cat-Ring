package App.BusinessLogic.RecipeManagement;

public abstract class Mixture  {
    protected int id = -1;
    protected String name;

    public Mixture(String name){this.name = name;}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString(){
        return name;
    }

}
