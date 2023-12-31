package vn.soft.ship_service.utils;

import org.springframework.beans.BeanUtils;

public class MapUtils {
    public static <S, D> D copyWithoutAudit(S source, D destination) {
        return copy(source, destination, "createBy", "createDate", "version", "id");
    }

    public static <S, D> D copy(S source, D destination) {
        BeanUtils.copyProperties(source, destination);
        return destination;
    }

    public static <S, D> D copy(S source, D destination, String... ignore) {
        BeanUtils.copyProperties(source, destination, ignore);
        return destination;
    }
}
