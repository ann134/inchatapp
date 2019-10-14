package ru.sigmadigital.inchat.models.realm;

import io.realm.RealmList;
import io.realm.RealmObject;

public class ChatRealm extends RealmObject {

    long chatId;
    RealmList<ChatMessageRealm> messages;




}
