package vn.clmart.manager_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ItemsSearchDto{
    private Long idPubliser;
    private Long idStall;
    private Long idCategory;
    private Date startDate;
    private Date endDate;
}
