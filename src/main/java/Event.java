public class Event {

    private String sessionId;
    private Client client;

    public void onClientClose(int code, String reason, boolean remote) {
        System.out.println("5秒后开始尝试恢复连接...");
        try {
            Thread.sleep(5000);
            new Thread(() -> reconnect(code)).start();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("重新连接失败,请检查网络!");
        }
    }

    private void reconnect(int code) {
        System.out.println("正在重新连接...");
        if (code != 4009) {
            sessionId = null;
        }
        client.reconnect();
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void onDispatch(Payload payload) {
    }

    public void onReconnect() {
    }

    public void onInvalidSession() {
    }

    public void onHello(Payload payload) {
    }

    public void onHeartbeat() {
    }
}
