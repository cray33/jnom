package my.home.jnom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws SQLException {
        ApplicationContext context = SpringApplication.run(Application.class, args);
        LoadPreDataService loadPreDataService = context.getBean(LoadPreDataService.class);
        if (args[0].equals("load_full") || args[0].equals("load_pre_data")) {
           loadPreDataService.loadPreData(true, args[1]);
        }


        LoadDataService loadDataService = context.getBean(LoadDataService.class);
        if (args[0].equals("load_full") || args[0].equals("load_data")) {
            loadDataService.handleData();
        }
    }

}