package net.practice.practice.storage.file;

import lombok.Getter;
import net.practice.practice.util.file.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BuycraftFile extends Configuration {

    @Getter private List<Integer> completed;

    public BuycraftFile() {
        super("buycraft");
    }

    public void load() {
        if(!getConfig().contains("completed"))
            getConfig().set("completed", Collections.emptyList());

        completed = new ArrayList<>();

        completed.addAll(getConfig().getIntegerList("completed"));
    }

    public boolean isCompleted(int id) {
        return completed.contains(id);
    }

    public boolean notCompleted(int id) {
        return !isCompleted(id);
    }

    public void addCompleeted(int id) {
        completed.add(id);
    }

    public void saveAll() {
        getConfig().set("completed", completed);

        load();
    }
}
