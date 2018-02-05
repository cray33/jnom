package my.home.jnom.entity;

import java.util.ArrayList;
import java.util.List;

public class Border {
    private List<List<Coords>> outers = new ArrayList<>();
    private List<List<Coords>> inners = new ArrayList<>();
    private Coords adminCentre;

    public List<List<Coords>> getOuters() {
        return outers;
    }

    public void setOuters(List<List<Coords>> outers) {
        this.outers = outers;
    }

    public List<List<Coords>> getInners() {
        return inners;
    }

    public void setInners(List<List<Coords>> inners) {
        this.inners = inners;
    }

    public Coords getAdminCentre() {
        return adminCentre;
    }

    public void setAdminCentre(Coords adminCentre) {
        this.adminCentre = adminCentre;
    }
}
