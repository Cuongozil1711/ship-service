package vn.soft.ship_service.config.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
public abstract class BaseDTO implements Serializable {
    private Date createDate;
    private Date modifiedDate;
    private Integer status;
    private String createBy;
    private String updateBy;
}
