package enumeration;

/**
 * @Author: Dunfu Peng
 * @Date: 2019/4/3 11:22
 */
public enum TaskType {

    FILE(1,"文件"),
    CRAWL_IMAGE(2,"豆瓣电影图片");

    private int code;
    private String desc;

    TaskType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }}
