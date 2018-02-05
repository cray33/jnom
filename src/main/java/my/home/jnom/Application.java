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
        if (args.length > 0) {
            if ("load_full".equals(args[0]) || "load_pre_data".equals(args[0])) {
               loadPreDataService.loadPreData(true, args[1]);
            }


            LoadDataService loadDataService = context.getBean(LoadDataService.class);
            if ("load_full".equals(args[0]) || "load_data".equals(args[0])) {
                loadDataService.handleData();
            }
        }
    }

}