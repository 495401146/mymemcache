package cache.model;



public class DictValue {
    private String value;
    private String flags;
    private String expireTime;

    public DictValue(String value, String flags, String expireTime) {
        this.value = value;
        this.flags = flags;
        this.expireTime = expireTime;
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DictValue dictValue = (DictValue) o;

        if (value != null ? !value.equals(dictValue.value) : dictValue.value != null) return false;
        if (flags != null ? !flags.equals(dictValue.flags) : dictValue.flags != null) return false;
        return expireTime != null ? expireTime.equals(dictValue.expireTime) : dictValue.expireTime == null;
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (flags != null ? flags.hashCode() : 0);
        result = 31 * result + (expireTime != null ? expireTime.hashCode() : 0);
        return result;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFlags() {
        return flags;
    }

    public void setFlags(String flags) {
        this.flags = flags;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }
}
