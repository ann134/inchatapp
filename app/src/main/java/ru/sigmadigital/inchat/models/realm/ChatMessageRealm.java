package ru.sigmadigital.inchat.models.realm;

import android.text.format.DateFormat;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ru.sigmadigital.inchat.models.ChatMessageResponse;
import ru.sigmadigital.inchat.models.FileResponse;
import ru.sigmadigital.inchat.models.UserResponse;

public class ChatMessageRealm extends RealmObject {


    long chatId;
    @PrimaryKey
    long id;
    String message;
    UserRealm from;
    RealmList<FileRealm> files;
    long createdAt;
    long createdAtLong;
    String date;
    boolean readed;

    public ChatMessageRealm() {
    }

    public ChatMessageRealm(ChatMessageResponse response, long chatId) {
        this.chatId = chatId;
        this.id = response.getId();
        this.message = response.getMessage();
        this.from = new UserRealm(response.getFrom());
        this.files = new RealmList<>();
        for (FileResponse fileResponse : response.getFiles()){
            files.add(new FileRealm(fileResponse));
        }
        this.createdAt = response.getCreatedAt();
        this.readed = response.isReaded();



        date = DateFormat.format("yyyy-MM-dd' 'HH:mm:ss", new Date(createdAtLong)).toString();
    }

    public long getChatId() {
        return chatId;
    }

    public long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public UserRealm getFrom() {
        return from;
    }

    public RealmList<FileRealm> getFiles() {
        return files;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getDate() {
        return date;
    }

    public boolean isReaded() {
        return readed;
    }

    public long getCreatedAtLong() {
        return createdAtLong;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
