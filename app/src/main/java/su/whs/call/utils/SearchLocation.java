package su.whs.call.utils;

public class SearchLocation {

    private static SearchLocation instance = null;

    private String chosenCity = "Orel";

    public static SearchLocation getInstance() {
        if (instance == null)
            instance = new SearchLocation();

        return instance;
    }

    public String getChosenCity() {
        return chosenCity;
    }

    public void choseCity(String city) {
        chosenCity = city;
    }

}
