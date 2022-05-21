package pojo;

import java.sql.Date;
import java.util.HashMap;

public class UserState {
    public String id;
    public int state;
    public Date lastModificationDate;
    public Date lastFinishDate;
    public int contClockDaysCount;

    @Override
    public String toString() {
        return "UserState{" +
                "id='" + id + '\'' +
                ", state=" + state +
                ", lastModificationDate=" + lastModificationDate +
                ", lastFinishDate=" + lastFinishDate +
                ", contClockDaysCount=" + contClockDaysCount +
                '}';
    }

    public UserState() {
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", this.id);
        hashMap.put("state", this.state);
        hashMap.put("lastModificationDate", this.lastModificationDate);
        hashMap.put("lastFinishDate", this.lastFinishDate);
        hashMap.put("contClockDaysCount", this.contClockDaysCount);
        return hashMap;
    }

    public UserState(String id, int state, Date lastModificationDate, Date lastFinishDate, int contClockDaysCount) {
        this.id = id;
        this.state = state;
        this.lastModificationDate = lastModificationDate;
        this.lastFinishDate = lastFinishDate;
        this.contClockDaysCount = contClockDaysCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Date getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(Date lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public Date getLastFinishDate() {
        return lastFinishDate;
    }

    public void setLastFinishDate(Date lastFinishDate) {
        this.lastFinishDate = lastFinishDate;
    }

    public int getContClockDaysCount() {
        return contClockDaysCount;
    }

    public void setContClockDaysCount(int contClockDaysCount) {
        this.contClockDaysCount = contClockDaysCount;
    }
}
