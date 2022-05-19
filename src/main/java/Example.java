public class Example {
    public static void main(String[] args) {
        AccessInfo accessInfo = new AccessInfo();
        accessInfo.setBotAppId(102006259);
        accessInfo.setBotToken("ykj14TlmLKqbWHUyAYzbwxaMUTflTg94");
        BotCore bot = new BotCore(accessInfo);

        ApiManager api = bot.getApiManager();
        bot.registerAtMessageEvent();
        IEventHandler handler = new IEventHandler(api);

        bot.setEventHandler(handler);
        bot.start();

    }
}
