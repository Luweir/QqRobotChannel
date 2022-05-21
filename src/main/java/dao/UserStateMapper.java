package dao;

import pojo.UserState;

import java.util.HashMap;

public interface UserStateMapper {
    UserState getUserStateById(String id);

    int addUserState(HashMap<String, Object> hashMap);

    int updateUserState(HashMap<String, Object> hashMap);
}
