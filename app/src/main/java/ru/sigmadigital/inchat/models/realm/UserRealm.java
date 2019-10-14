package ru.sigmadigital.inchat.models.realm;

import io.realm.RealmObject;
import ru.sigmadigital.inchat.models.FileResponse;
import ru.sigmadigital.inchat.models.UserResponse;

public class UserRealm  extends RealmObject {

    long id;
    String name;

    public UserRealm() {
    }

    public UserRealm(UserResponse response) {
        this.id = response.getId();
        this.name = response.getName();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
