package org.springcrazy.common.tool;


public class JsonResponse<T> {
    private Integer errCode = 0;
    private T data = null;
    private String msg;

    public JsonResponse() {
    }

    public Integer getErrCode() {
        return this.errCode;
    }

    public T getData() {
        return this.data;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setErrCode(final Integer errCode) {
        this.errCode = errCode;
    }

    public void setData(final T data) {
        this.data = data;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof JsonResponse)) {
            return false;
        } else {
            JsonResponse<?> other = (JsonResponse)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label47: {
                    Object this$errCode = this.getErrCode();
                    Object other$errCode = other.getErrCode();
                    if (this$errCode == null) {
                        if (other$errCode == null) {
                            break label47;
                        }
                    } else if (this$errCode.equals(other$errCode)) {
                        break label47;
                    }

                    return false;
                }

                Object this$data = this.getData();
                Object other$data = other.getData();
                if (this$data == null) {
                    if (other$data != null) {
                        return false;
                    }
                } else if (!this$data.equals(other$data)) {
                    return false;
                }

                Object this$msg = this.getMsg();
                Object other$msg = other.getMsg();
                if (this$msg == null) {
                    if (other$msg != null) {
                        return false;
                    }
                } else if (!this$msg.equals(other$msg)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof JsonResponse;
    }

    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object $errCode = this.getErrCode();
        result = result * 59 + ($errCode == null ? 43 : $errCode.hashCode());
        Object $data = this.getData();
        result = result * 59 + ($data == null ? 43 : $data.hashCode());
        Object $msg = this.getMsg();
        result = result * 59 + ($msg == null ? 43 : $msg.hashCode());
        return result;
    }

    public String toString() {
        return "JsonResponse(errCode=" + this.getErrCode() + ", data=" + this.getData() + ", msg=" + this.getMsg() + ")";
    }
}

