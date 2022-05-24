package websocker;

import common.ApiManager;
import common.User;
import dao.UserStateMapper;
import org.apache.ibatis.session.SqlSession;
import pojo.UserState;
import utils.MyBatisUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class IEventHandler extends EventHandler {
    private final ApiManager api;

    public IEventHandler(ApiManager api) {
        this.api = api;
    }

    // 处理错误
    @Override
    public void onError(Exception t) {

    }

    // 对 艾特 信息进行回复
    // 这里是健康打卡  包括：7杯水喝水打卡；运动打卡；视力打卡；午睡打卡； 3+1+1+1 6位搞定
    public void onAtMessage(AtMessageEvent atMessageEvent) {
        Message message = atMessageEvent.getMessage();
        String content = message.getContent();
        User author = message.getAuthor();
        switch (content) {
            case "查询状态":
                queryState(message);
                break;
            case "打卡规则":
                instructions(message);
                break;
            default:
                healthPunch(message);
        }
    }

    public void instructions(Message message) {
        String channelId = message.getChannelId();
        String messageId = message.getId();
        User author = message.getAuthor();
        String replyContent = "<@!" + author.getId() + ">";
        replyContent += "打卡规则：每日须进行7次喝水打卡，1次运动打卡，1次视力打卡，1次午睡打卡，@本机器人并发送相应打卡指令即可完成打卡。\n比如：@xxx 喝水打卡\n注意：不允许同一时间打卡多项！";
        api.getMessageApi().sendMessage(channelId, replyContent, messageId);
    }

    /**
     * 查询用户打卡状态
     *
     * @param message
     */
    public void queryState(Message message) {
        String channelId = message.getChannelId();
        String messageId = message.getId();
        User author = message.getAuthor();
        String replyContent = "<@!" + author.getId() + ">";
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        UserStateMapper mapper = sqlSession.getMapper(UserStateMapper.class);
        UserState userState = mapper.getUserStateById(author.getId());
        // 如果是新用户，则创建
        if (userState == null) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("id", author.getId());
            hashMap.put("state", 0);
            hashMap.put("lastModificationDate", new java.sql.Date(0));
            hashMap.put("lastFinishDate", new java.sql.Date(0));
            hashMap.put("contClockDaysCount", 0);
            mapper.addUserState(hashMap);
            sqlSession.commit();
            userState = mapper.getUserStateById(author.getId());
        }
        if (userState.getState() == 63) {
            replyContent += "\n恭喜！您今天的健康打卡全部完成。";
        } else {
            replyContent += "\n您当日还剩";
            if (((userState.getState() >> 3) & 7) < 7) {
                replyContent += (7 - (userState.getState() >> 3)) + "次喝水打卡 ";
            }
            if (((userState.getState() >> 2) & 1) == 0) {
                replyContent += "1次运动打卡 ";
            }
            if (((userState.getState() >> 1) & 1) == 0) {
                replyContent += "1次视力打卡 ";
            }
            if ((userState.getState() & 1) == 0) {
                replyContent += "1次午睡打卡 ";
            }
        }
        replyContent += "\n您已累计健康打卡" + userState.getContClockDaysCount() + "天！";
        api.getMessageApi().sendMessage(channelId, replyContent, messageId);
    }

    /**
     * 健康打卡
     *
     * @param message
     */
    public void healthPunch(Message message) {
        String channelId = message.getChannelId();
        String content = message.getContent();
        String messageId = message.getId();
        User author = message.getAuthor();
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        try {
            int deltaState = 0;
            String replyContent = "<@!" + author.getId() + ">";
            if (content.contains("喝水打卡")) {
                content="喝水打卡";
                deltaState = (1 << 3);
            } else if (content.contains("运动打卡")) {
                content="运动打卡";
                deltaState = (1 << 2);
            } else if (content.contains("视力打卡")) {
                content="视力打卡";
                deltaState = (1 << 1);
            } else if (content.contains("午睡打卡")) {
                content="午睡打卡";
                deltaState = 1;
            } else {
                deltaState = 0;
            }
//            switch (content) {
//                case "喝水打卡":
//                    deltaState = (1 << 3);
//                    break;
//                case "运动打卡":
//                    deltaState = (1 << 2);
//                    break;
//                case "视力打卡":
//                    deltaState = (1 << 1);
//                    break;
//                case "午睡打卡":
//                    deltaState = 1;
//                    break;
//                default:
//                    deltaState = 0;
//            }
            if (deltaState == 0) {
                replyContent += "输入有误，请重新输入指令！";
                api.getMessageApi().sendMessage(channelId, replyContent, messageId);
                return;
            }
            UserStateMapper mapper = sqlSession.getMapper(UserStateMapper.class);
            UserState userState = mapper.getUserStateById(author.getId());
            java.sql.Date todayDate = new java.sql.Date(new Date().getTime());
            if (userState == null) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", author.getId());
                hashMap.put("state", 0);
                hashMap.put("lastModificationDate", new java.sql.Date(0));
                hashMap.put("lastFinishDate", new java.sql.Date(0));
                hashMap.put("contClockDaysCount", 0);
                mapper.addUserState(hashMap);
                sqlSession.commit();
                userState = mapper.getUserStateById(author.getId());
            }
            // 还是以前的打卡状态 更新为今天的状态 即0
            if (userState.getLastModificationDate().toString().compareTo(todayDate.toString()) < 0)
                userState.setState(0);
            int preState = userState.getState();
            // 该项打卡今日是否已满
            if ((((preState >> 3) & 7) == 7 && deltaState == 8) || (((preState >> 2) & 1) == 1 && deltaState == 4)
                    || (((preState >> 1) & 1) == 1 && deltaState == 2) || ((preState & 1) == 1 && deltaState == 1)) {
                replyContent += "您今天的" + content + "已全部完成，请不要重复打卡！";
                api.getMessageApi().sendMessage(channelId, replyContent, messageId);
                return;
            }
            userState.setState(userState.getState() + deltaState);
            userState.setLastModificationDate(todayDate);
            replyContent += content + "成功！";
            // 今天打满
            if (userState.getState() == 63) {
                DateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date lastFinishDate = simpleFormat.parse(userState.getLastFinishDate().toString());
                if ((int) ((todayDate.getTime() - lastFinishDate.getTime()) / (1000 * 3600 * 24)) == 1) {
                    userState.setContClockDaysCount(userState.getContClockDaysCount() + 1);
                } else {
                    userState.setContClockDaysCount(1);
                }
                userState.setLastFinishDate(todayDate);
                replyContent += "\n恭喜！您今天的健康打卡全部完成；\n 您已累计健康打卡" + userState.getContClockDaysCount() + "天！";
//                System.out.println(replyContent);
            } else {
                replyContent += "\n您当日还剩";
                if (((userState.getState() >> 3) & 7) < 7) {
                    replyContent += (7 - (userState.getState() >> 3)) + "次喝水打卡 ";
                }
                if (((userState.getState() >> 2) & 1) == 0) {
                    replyContent += "1次运动打卡 ";
                }
                if (((userState.getState() >> 1) & 1) == 0) {
                    replyContent += "1次视力打卡 ";
                }
                if ((userState.getState() & 1) == 0) {
                    replyContent += "1次午睡打卡 ";
                }
            }
            mapper.updateUserState(userState.toMap());
            sqlSession.commit();
//            System.out.println("更新成功");
            api.getMessageApi().sendMessage(channelId, replyContent, messageId);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("发生异常");
        } finally {
            sqlSession.close();
        }
    }
}