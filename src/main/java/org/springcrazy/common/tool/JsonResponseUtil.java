package org.springcrazy.common.tool;


import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springcrazy.common.enums.ResponseCodeEnums;
import org.springcrazy.common.pojo.PaginationVO;


public class JsonResponseUtil {
    public static final String SUCCESS_MSG = "提交成功";
    public static final String ERROR = "提交失败";

    public JsonResponseUtil() {
    }

    public static <T> JsonResponse<T> success() {
        return (JsonResponse<T>) success("提交成功", (Object)null);
    }

    public static <T> JsonResponse<T> data(T data) {
        return success("提交成功", data);
    }

    public static <T> JsonResponse<T> success(String msg) {
        return (JsonResponse<T>) success(msg, (Object)null);
    }

    public static <T> JsonResponse<T> success(String msg, T data) {
        JsonResponse<T> res = new JsonResponse();
        res.setData(data);
        res.setMsg(msg);
        return res;
    }

    public static <T> JsonResponse<T> error(String msg) {
        JsonResponse<T> res = new JsonResponse();
        res.setErrCode(ResponseCodeEnums.BIZ_ERROR.getCode());
        res.setMsg(msg);
        return res;
    }

    public static <T> JsonResponse<T> error() {
        JsonResponse<T> res = new JsonResponse();
        res.setErrCode(ResponseCodeEnums.BIZ_ERROR.getCode());
        res.setMsg("提交失败");
        return res;
    }

    public static <T> JsonResponse<T> error(ResponseCodeEnums responseCodeEnums) {
        return error(responseCodeEnums.getCode(), responseCodeEnums.getMsg());
    }

    public static <T> JsonResponse<T> error(Integer code, String msg) {
        JsonResponse<T> res = new JsonResponse();
        res.setErrCode(code);
        res.setMsg(msg);
        return res;
    }

    public static PaginationVO paginate(IPage<?> ipage) {
        PaginationVO res = new PaginationVO();
        res.setRecords(ipage.getRecords());
        res.setTotal(ipage.getTotal());
        res.setPage(ipage.getCurrent());
        res.setPageSize(ipage.getSize());
        res.setPageCount(ipage.getPages());
        return res;
    }
}
