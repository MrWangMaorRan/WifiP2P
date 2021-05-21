package leavesc.hello.filetransfer.telephone;

public class CallLogBean {

    private String name;
    private String number;
    private String data;
    private int duration;
    private String typeString;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getTypeString() {
        return typeString;
    }

    public void setTypeString(String typeString) {
        this.typeString = typeString;
    }

    @Override
    public String toString() {
        return "CallLogBean{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", data='" + data + '\'' +
                ", duration=" + duration +
                ", typeString=" + typeString +
                '}';
    }
}
