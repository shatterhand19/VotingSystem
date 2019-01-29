package protocol.messages;

import java.io.Serializable;

/**
 * Created by bozhidar on 08.11.17.
 */
public class LoginMessage implements Serializable {
    private String name;
    private String matric;
    private String dob;
    private String pwd;
    //private long cnonce;

    public LoginMessage(String name, String matric, String dob, String pwd/*, long cnonce*/) {
        this.name = name;
        this.matric = matric;
        this.dob = dob;
        this.pwd = pwd;
        //this.cnonce = cnonce;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    /*public long getCnonce() {
        return cnonce;
    }

    public void setCnonce(long cnonce) {
        this.cnonce = cnonce;
    }*/

    public String getMatric() {
        return matric;
    }

    public void setMatric(String matric) {
        this.matric = matric;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoginMessage)) return false;

        LoginMessage that = (LoginMessage) o;

        //if (getCnonce() != that.getCnonce()) return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
        if (getMatric() != null ? !getMatric().equals(that.getMatric()) : that.getMatric() != null) return false;
        if (getDob() != null ? !getDob().equals(that.getDob()) : that.getDob() != null) return false;
        return getPwd() != null ? getPwd().equals(that.getPwd()) : that.getPwd() == null;
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getMatric() != null ? getMatric().hashCode() : 0);
        result = 31 * result + (getDob() != null ? getDob().hashCode() : 0);
        result = 31 * result + (getPwd() != null ? getPwd().hashCode() : 0);
        //result = 31 * result + (int) (getCnonce() ^ (getCnonce() >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "LoginMessage{" +
                "name='" + name + '\'' +
                ", matric='" + matric + '\'' +
                ", dob='" + dob + '\'' +
                ", pwd='" + pwd + '\'' +
                //", cnonce=" + cnonce +
                '}';
    }
}
