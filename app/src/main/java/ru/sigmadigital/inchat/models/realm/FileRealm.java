package ru.sigmadigital.inchat.models.realm;

import io.realm.RealmObject;
import ru.sigmadigital.inchat.models.FileResponse;

public class FileRealm  extends RealmObject {

    int id;
    String name;

    public FileRealm() {
    }

    public FileRealm(FileResponse response) {
        this.id = response.getId();
        this.name = response.getName();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
