# QqRobotChannel
Java for qqRobotChannel
使用说明文档——频道健康打卡机器人开发测验
# 一、简介
健康打卡机器人是基于 QQ频道 的一款简单打卡工具，它主要采用 Java-WebSocket、MyBatis 以及 MySQL 等技术，支持用户查看打卡规则、查询打卡状态，执行某一类型的打卡操作以及查看持续打卡天数；打卡内容包括：7次喝水打卡、1次运动打卡、1次视力打卡、1次午睡打卡，用户须全部完成上述 10 次打卡即视为当日健康打卡完成！

注：Java 中 对频道机器人 API 的使用参考 → https://github.com/xiaoye-bot/qqbot-sdk

# 二、开发流程
从注册申请机器人到最后部署验收，整体流程如下所示：
![image](https://user-images.githubusercontent.com/45732577/169640723-066edbc4-302e-4bd0-b5f9-85a8d099a04d.png)


主要分为三个大的模块：
1. common 模块：根据频道机器人的接口文档以及各实例的结构信息，将其映射为公用的实体类，以便其它模块调用和处理；
2. websocket 模块：该模块通过依赖包org.java_websocket连接频道服务器，与服务器进行数据的传输，包括连接到 GateWay、鉴权、心跳等操作；同时将传输数据时所需要的多种结构体信息映射为实体类，方便模块内部的调用以及模块间的使用；
3. mybatis 模块：定义了用户的健康打卡状态，通过 MyBatis 对 MySQL 中的 user_state 表进行操作，表中字段的设置与定义在后续中会提到；

整个通信流程可以理解为：通过 Java-Websocket 层与 QQ频道服务器 建立连接，当事先设定的事件触发时会进行事件响应，响应动作通过 mybatis 模块与数据库打交道，全程所必须用到的实体类都包含在 common 模块和 websocket 模块中，包括 AccessInfo、User、Message、GateWay、Payload等；

## 2.1 common 模块
common 模块主要包含各种公共的实体类；
![image](https://user-images.githubusercontent.com/45732577/169640728-afc58214-1bc5-42e7-8a88-057eb9c2f11a.png)


common 模块主要用于定义所需要的公共实体类 以及 基本方法实现；
## 2.2 websocket 模块
![image](https://user-images.githubusercontent.com/45732577/169640677-21351012-bd25-4bff-ae95-2015c14e4fae.png)


websocket 模块主要用于与 websocket 服务端进行通信，同时设置响应事件和事件触发响应动作，健康打卡机器人的核心内容在自定义的 IEventHandler 中；
## 2.3 mybatis 模块
![image](https://user-images.githubusercontent.com/45732577/169640734-e8eb80aa-f6f7-4b4a-8439-00c41afebcfe.png)


mybatis 模块主要与 数据库 打交道，包括获取用户状态信息、添加用户状态、更新用户状态等操作；
### 2.3.1 数据库表的创建
```sql
CREATE DATABASE `robot`;

USE `robot`;

CREATE TABLE `user_state`(
        `id` VARCHAR(20) PRIMARY KEY,
        `state` INT(3),
        `last_modification_date` DATE,
        `last_finish_date` DATE,
        `cont_clock_days_count` INT(10)
)ENGINE=INNODB DEFAULT CHARSET=utf8;
```
### 2.3.2 MyBatis 配置文件
```xml
<?xml version="1.0" encoding="UTF8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<!--核心配置文件-->
<configuration>
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <!--jdbc 驱动-->
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url"
                          value="jdbc:mysql://localhost:3306?useSSL=true&amp;useUnicode=true&amp;characterEncoding=UTF-8"/>
                <property name="username" value="root"/>
                <property name="password" value="yourPassword"/>
            </dataSource>
        </environment>
    </environments>
    <!--每一个 Mapper.xml 都需要在mybatis的核心配置文件中注册-->
    <mappers>
        <mapper resource="dao/UserStateMapper.xml"/>
    </mappers>
</configuration>
```
### 2.3.3 pojo—UserState
UserState 实体类用于与 user_state 表中的记录结构进行映射；
```java
public class UserState {
    public String id;
    public int state;
    public Date lastModificationDate;
    public Date lastFinishDate;
    public int contClockDaysCount;
    
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", this.id);
        hashMap.put("state", this.state);
        hashMap.put("lastModificationDate", this.lastModificationDate);
        hashMap.put("lastFinishDate", this.lastFinishDate);
        hashMap.put("contClockDaysCount", this.contClockDaysCount);
        return hashMap;
    }
}
```
### 2.3.4 dao 层
dao 层主要包括 UserStateMapper.java 以及 UserStateMapper.xml，前者定义了 操作 UserState 的一些接口，后者为 MyBatis 框架中最重要的组成之一，其将 UserStateMapper.java 中接口的实现用配置文件完成；
UserStateMapper.java 如下所示：
```java
public interface UserStateMapper {
    UserState getUserStateById(String id);

    int addUserState(HashMap<String, Object> hashMap);

    int updateUserState(HashMap<String, Object> hashMap);
}
```
UserStateMapper.xml 如下所示：
```java
<mapper namespace="dao.UserStateMapper">
    <!--处理 数据库字段 与 JavaBean 的映射-->
    <resultMap id="userStateResultMap" type="pojo.UserState">
        <result property="lastModificationDate" column="last_modification_date"/>
        <result property="lastFinishDate" column="last_finish_date"/>
        <result property="contClockDaysCount" column="cont_clock_days_count"/>
    </resultMap>
    <select id="getUserStateById" parameterType="String" resultMap="userStateResultMap">
        select * from robot.user_state where id = #{id};
    </select>
    <insert id="addUserState" parameterType="map">
        insert into robot.user_state(id, state, last_modification_date, 
            last_finish_date, cont_clock_days_count) 
        VALUE (#{id}, #{state}, #{lastModificationDate}, #{lastFinishDate}, 
            #{contClockDaysCount})
    </insert>
    <update id="updateUserState" parameterType="map">
        update robot.user_state
        set state=#{state}, last_modification_date=#{lastModificationDate}, 
            last_finish_date=#{lastFinishDate},
            cont_clock_days_count=#{contClockDaysCount}
        where id = #{id}
    </update>
</mapper>
```
## 2.4 环境配置
![image](https://user-images.githubusercontent.com/45732577/169640745-c512b91c-c50f-45d8-8579-0c5c8f4ba75a.png)


- MySQL：数据库（version=5.5.54）；
- IDEA：Java 开发集成环境；
- JDK：version=1.8；
- MyBatis：持久层框架（version=3.5.9）；
- Git：代码管理与版本控制；
- Maven：依赖包管理（version=3.6.1）；

# 三、简单使用
```java
public class TestDemo {
    public static void main(String[] args) {
        AccessInfo accessInfo = new AccessInfo();
        accessInfo.setBotAppId(yourAppId); // 创建robot时生成的appId
        accessInfo.setBotToken(yourToken); // 创建robot时生成的Token
        BotCore bot = new BotCore(accessInfo);
        // 获得API管理器并注册消息事件
        ApiManager api = bot.getApiManager();
        bot.registerAtMessageEvent();
        // 设置消息事件响应处理
        IEventHandler handler = new IEventHandler(api);
        bot.setEventHandler(handler);
        // 启动机器人
        bot.start();
    }
}
```
其中，IEventHandler 为自定义的事件处理器，当注册的事件触发时，会对其进行处理，此处为对服务端发来的用户@机器人的消息进行处理，根据消息内容分成三个子回复，具体实现见下一节；
# 四、核心功能
健康打卡机器人的核心功能主要在 websocket 模块的 事件触发响应动作 中进行实现，即IEventHandler.java 文件；
## 4.1 事件触发响应动作
健康打卡机器人中的时间出发响应动作主要是对用户@机器人的信息进行回复，对应的 opcode=0;intents=AT_MESSAGE_CREATE(1<<30)；
```java
public class IEventHandler extends EventHandler {
    private final ApiManager api;

    public IEventHandler(ApiManager api) {
        this.api = api;
    }
    // 对 艾特 信息进行回复
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
}
```
## 4.2 数据库表字段定义
数据库 robot 中的 user_state 表的字段定义如下：
```sql
USE `robot`;

CREATE TABLE `user_state`(
        `id` VARCHAR(20) PRIMARY KEY,
        `state` INT(3),
        `last_modification_date` DATE,
        `last_finish_date` DATE,
        `cont_clock_days_count` INT(10)
)ENGINE=INNODB DEFAULT CHARSET=utf8;
```
其中：
1）id：为用户的 appId；
2）state：用户今日健康打卡状态，基于位的操作实现；
3）last_modification_date：用户状态最近修改日期；
4）last_finish_date：用户最近健康打卡完成日期；
5）cont_clock_days_count：用户连续完成健康打卡天数；
### 4.2.1 state 位操作
state 主要为 6 位 000000前三位表示 7 次喝水打卡，第四位表示运动打卡，第五位表示视力打卡、第六位表示午睡打卡，当日健康打卡全部完成时，state 值为111111，即最大 state=63；同时，根据state 值的不同可得知当前用户所处的打卡状态！

若当前信息为打卡指令：
![image](https://user-images.githubusercontent.com/45732577/169640753-6315982d-7e94-4bc3-8743-73a9377dcf88.png)

若当前信息为查询指令：
![image](https://user-images.githubusercontent.com/45732577/169640759-ad46c876-bd12-4d91-88a4-35c9f0f44b59.png)


### 4.2.2 last_modification_date
最近修改日期字段：
1）创建行记录时，初始为 1971-01-01；
2）若当前日期与 last_modification_date 不相等时，说明此时需要更新状态为今日初始状态，即state=0；
3）若当前日期与 last_modification_date 相等时，说明此时状态就为今日最新状态，根据打卡指令对 state 进行操作；
4.2.3 last_finish_date 与 cont_clock_days_count
1）创建行记录时
last_finish_date 初始为 1971-01-01；
cont_clock_days_count 初始为0；

2）若今日健康打卡全部完成，即state=63时，检查 last_finish_date 是否与 last_modification_date 仅差一天，若是，则说明为连续打卡，更新 cont_clock_days_count 为 cont_clock_days_count+1 ；若不是，则说明非连续打卡，更新 cont_clock_days_count 为1；最后，更新 last_finish_date 为 last_modification_date 
## 4.3 查询状态
若用户发送的是查询状态指令，则根据用户ID 在MySQL中查找回复一下内容：
```java
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
```
## 4.4 打卡规则
若用户发送的是查询状态指令，则回复一下内容：
```java
public void instructions(Message message) {
    String channelId = message.getChannelId();
    String messageId = message.getId();
    User author = message.getAuthor();
    String replyContent = "<@!" + author.getId() + ">";
    replyContent += "打卡规则：每日须进行7次喝水打卡，1次运动打卡，1次视力打卡，1次午睡打卡，@本机器人并发送相应打卡信息即可完成打卡。\n比如：@xxx 喝水打卡\n注意：不允许同一时间打卡多项！";
    api.getMessageApi().sendMessage(channelId, replyContent, messageId);
}
```
## 4.5 打卡指令
根据不同的打卡指令执行不同的打卡操作，核心通过 State 字段的位操作实现；
例如，打卡指令为喝水打卡：
1. 首先，根据 id 获取 user_state 中的行记录，若不存在则新建用户；
2. 若 userState 中最近修改日期不为今日，则更新今日打卡状态以及最近修改日期 state=0，last_modification_date = todayDate；
3. 若 userState 中喝水打卡已达到打卡次数（7次），则返回响应信息，不执行打卡操作；否则，执行第四步；
4. 更新 State ，判断今日健康打卡是否已全部完成，全部完成则输出对应信息，提交事务；否则，执行第五步；
5. 输出对应信息以及今日剩余打卡类型和对应打卡次数，提交事务，关闭连接池；
```java
public void healthPunch(Message message) {
        String channelId = message.getChannelId();
        String content = message.getContent();
        String messageId = message.getId();
        User author = message.getAuthor();
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        try {
            int deltaState = 0;
            String replyContent = "<@!" + author.getId() + ">";
            switch (content) {
                case "喝水打卡":
                    deltaState = (1 << 3);
                    break;
                case "运动打卡":
                    deltaState = (1 << 2);
                    break;
                case "视力打卡":
                    deltaState = (1 << 1);
                    break;
                case "午睡打卡":
                    deltaState = 1;
                    break;
                default:
                    deltaState = 0;
            }
            if (deltaState == 0) {
                replyContent += "输入有误,请重新打卡！";
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
            // 更新为今天的状态 即 state=0
            if (userState.getLastModificationDate().toString().compareTo(todayDate.toString()) < 0)
                userState.setState(0);
            int preState = userState.getState();
            // 该项打卡今日是否已满足要求
            if ((((preState >> 3) & 7) == 7 && deltaState == 8) || (((preState >> 2) & 1) == 1 && deltaState == 4)
                    || (((preState >> 1) & 1) == 1 && deltaState == 2) || ((preState & 1) == 1 && deltaState == 1)) {
                replyContent += "您今天的" + content + "已全部完成，请不要重复打卡！";
                api.getMessageApi().sendMessage(channelId, replyContent, messageId);
                return;
            }
            userState.setState(userState.getState() + deltaState);
            userState.setLastModificationDate(todayDate);
            replyContent += content + "成功！";
            // 今天健康打卡全部完成 state=63
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
                System.out.println(replyContent);
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
            System.out.println("更新成功");
            api.getMessageApi().sendMessage(channelId, replyContent, messageId);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("发生异常");
        } finally {
            sqlSession.close();
        }
    }
```
# 五、测试
创建者测试一：
![image](https://user-images.githubusercontent.com/45732577/169640770-fffddfb6-ac06-4550-a7b5-9fb7d501cce0.png)

新用户测试二：
![image](https://user-images.githubusercontent.com/45732577/169640774-c05b5d2c-9948-42b4-a742-1d37b84ab3c9.png)

# 六、可能遇到的问题
**1）鉴权成功后，@机器人发送信息，机器人端无法收到信息；**
检查是否绑定消息监听事件，websocket.connect 处是否存在错误；

**2）maven 导出资源报错：找不到  xxxxx.xml**  
在 pom.xml 中配置，把 Java 中的 xml 也加载进来，因为一般默认只加载 resources 里的 xml 文件；
```xml
<build>       
        <resources>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.xml</include>
                    <include>**/*.properties</include>
                </includes>
            </resource>

            <resource>
                <directory>src/main/resources</directory>
                <includes>
                    <include>**/*.xml</include>
                    <include>**/*.properties</include>
                </includes>
            </resource>
        </resources>
</build>
```
**3）BindingException：Dao文件没有在 MapperRegistry 中注册**
MapperRegistery，一个Mapper.xml 都需要在mybatis的核心配置文件中注册；

**4）更新 State 后未能同步到数据库中**
对于 MySQL 增删改的操作都视为一个事务，事务需要提交才能算完成，因此需要在更新 State 或者 UserState 后及时 commit 才能保证操作完成；
