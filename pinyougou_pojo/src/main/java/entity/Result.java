package entity;

import java.io.Serializable;

/**
 * @BelongsProject: pinyougou_parent
 * @BelongsPackage: com.pinyougou.entity
 * @Author: pky
 * @CreateTime: 2019-05-22 19:47
 * @Description: ${Description}
 */
public class Result implements Serializable {
    //是否成功
    private boolean success;
    //操作返回消息
    private String message;

    @Override
    public String toString() {
        return "Result{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Result(boolean success, String message) {
        super();
        this.success = success;
        this.message = message;
    }
    //getter and setter.....
}

