package dao;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import pojo.UserState;
import utils.MyBatisUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class UserStateTest {
    @Test
    public void testSelect() {
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        try {
            UserStateMapper mapper = sqlSession.getMapper(UserStateMapper.class);
            UserState userState = mapper.getUserStateById("1111111");
            if (userState != null)
                System.out.println(userState.toString());
            else
                System.out.println("null");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testInsert() {
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        try {
            UserStateMapper mapper = sqlSession.getMapper(UserStateMapper.class);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("id", "1111111");
            hashMap.put("state", 0);
            hashMap.put("lastModificationDate", new java.sql.Date(new Date().getTime()));
            hashMap.put("lastFinishDate", new java.sql.Date(0));
            hashMap.put("contClockDaysCount", 23);
            int ret = mapper.addUserState(hashMap);
            sqlSession.commit();
            UserState userState = mapper.getUserStateById((String) hashMap.get("id"));
            System.out.println(userState.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testDateDiff() {
        DateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date parse = new java.sql.Date(new Date().getTime());
            Date parse1 = simpleFormat.parse("2022-05-19");
            System.out.println((int) ((parse.getTime() - parse1.getTime()) / (1000 * 3600 * 24)));
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }
}
