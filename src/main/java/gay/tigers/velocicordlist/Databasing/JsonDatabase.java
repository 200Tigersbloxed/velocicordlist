package gay.tigers.velocicordlist.Databasing;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class JsonDatabase implements IDatabase {
    private Logger logger;
    private Optional<File> optionalFile = Optional.empty();
    private Optional<JSONObject> optionalJsonObject = Optional.empty();

    public JsonDatabase(Logger logger){
        this.logger = logger;
    }

    private void save(){
        if(optionalFile.isEmpty() || optionalJsonObject.isEmpty()){
            return;
        }
        File file = optionalFile.get();
        JSONObject jsonObject = optionalJsonObject.get();
        try(FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(jsonObject.toString());
        } catch (IOException e) {
            logger.error("Failed to write JSON to File!", e);
        }
    }

    @Override
    public boolean connect(){
        final String dirTo = "plugins/velocicordlist";
        final String fileTo = dirTo + "/database.json";
        if(!new File(dirTo).exists()){
            new File((dirTo)).mkdirs();
        }
        StringBuilder text = new StringBuilder();
        Optional<Scanner> scannerOptional = Optional.empty();
        try{
            File file = new File(fileTo);
            optionalFile = Optional.of(file);
            if(!file.exists()){
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write("{}");
                fileWriter.close();
                text.append("{}");
            }
            else{
                Scanner scanner = new Scanner(file);
                scannerOptional = Optional.of(scanner);
                try{
                    String line = scanner.nextLine();
                    while (line != null) {
                        text.append(line).append('\n');
                        line = scanner.nextLine();
                    }
                }catch(Exception ignored){}
            }
        }catch (Exception e){
            logger.error("Failed to read file from " + fileTo, e);
        } finally {
            scannerOptional.ifPresent(Scanner::close);
        }
        String fileText = text.toString();
        if(fileText.equals("")){
            return false;
        }
        optionalJsonObject = Optional.of(new JSONObject(fileText));
        return true;
    }

    @Override
    public Optional<String[]> GetWhitelistedUsers() {
        if(optionalJsonObject.isEmpty()){
            return Optional.of(new String[0]);
        }
        JSONObject jsonObject = optionalJsonObject.get();
        JSONArray jsonArray = jsonObject.getJSONArray("allowed");
        ArrayList<String> newClone = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++){
            String v = jsonArray.getString(i);
            newClone.add(v);
        }
        return Optional.of(newClone.toArray(new String[0]));
    }

    @Override
    public void SetWhitelistedUsers(String[] users) {
        if(optionalJsonObject.isEmpty()){
            return;
        }
        JSONObject njo = optionalJsonObject.get();
        njo.remove("allowed");
        njo.put("allowed", List.of(users));
        optionalJsonObject = Optional.of(njo);
        save();
    }
}
