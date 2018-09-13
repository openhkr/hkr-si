package com.reachauto.hkr.si.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * Created by Administrator on 2018/2/1.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundBO {

    private static final int SUCCESS = 1;
    private static final int FAIL = 2;
    private static final int NO_RESPONSE = 4;
    
    private Integer status;

    public static RefundBO getNoResponseInstants() {
        return new RefundBO(NO_RESPONSE);
    }

    public static RefundBO getSuccessInstants() {
        return new RefundBO(SUCCESS);
    }

    public static RefundBO getFailInstants() {
        return new RefundBO(FAIL);
    }

    public boolean isSuccessed() {
        return Objects.equals(status, new Integer(SUCCESS));
    }

    public boolean isFailed() {
        return Objects.equals(status, new Integer(FAIL));
    }

    public boolean isNoResponse() {
        return Objects.equals(status, new Integer(NO_RESPONSE));
    }
}
