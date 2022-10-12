package vn.clmart.manager_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.clmart.manager_service.model.Order;

import java.util.Optional;
import java.util.*;

public interface OrderRepositorry extends JpaRepository<Order, Long> {

    @Query("select i from Order as i where i.companyId = :cid and i.deleteFlg = :status  and ((lower(concat(coalesce(i.code, ''), coalesce(i.name, ''))) like lower(concat('%',coalesce(:search, ''), '%')))  or (coalesce(:search, '') = '') ) order by i.createDate desc")
    Page<Order> findAllByCompanyId(Long cid, String search, Integer status, Pageable pageable);

    @Query("select i from Order as i where i.companyId = :cid " +
            "and (i.createDate between coalesce(:date, now()) " +
            "and now()   or coalesce(:date, current_date) = current_date)  " +
            "and ((lower(concat(coalesce(i.code, ''), coalesce(i.name, ''))) " +
            "like lower(concat('%',coalesce(:search, ''), '%')))  " +
            "or (coalesce(:search, '') = '') ) order by i.createDate desc")
    Page<Order> getAllByCompanyId(Long cid, String search, Date date, Pageable pageable);

    List<Order> findAllByCompanyIdAndIdCustomer(Long cid, Long customerId);

    Optional<Order> findByCompanyIdAndId(Long cid, Long id);

    Optional<Order> findByCompanyIdAndIdAndDeleteFlg(Long cid, Long id, Integer deleteFlg);

    List<Order> findAllByCompanyIdAndDeleteFlgAndCode(Long cid, Integer deleteFlg, String code);

    @Query(value = "select count(o.create_by) as sumOrder, o.create_by as createBy from `order` as o where date_format(o.create_date,'%Y, %m') = date_format(now(),'%Y, %m') and o.company_id = :cid and o.delete_flg = :deleteFlg  " +
            "group by o.create_by " +
            "order by count(o.create_by) asc", nativeQuery = true)
    List<Map<String, Object>> getOrdersByEmployee(
            @Param("cid")
            Long cid,
            @Param("deleteFlg")
            Integer deleteFlg);


    @Query(value = "select * from `order` as o where date_format(o.create_date,'%Y, %m') = date_format(now(),'%Y, %m') and " +
            "o.create_by = :uid and o.company_id = :cid and o.delete_flg = :deleteFlg ", nativeQuery = true)
    List<Order> getItemOrder(
            @Param("cid")
            Long cid,
            @Param("uid")
            String uid,
            @Param("deleteFlg")
            Integer deleteFlg);

    @Query(value = "select * from `order` as o where date_format(o.create_date,'%Y, %m') = date_format(:dateOrder,'%Y, %m') and " +
            "o.company_id = :cid and o.delete_flg = :deleteFlg ", nativeQuery = true)
    List<Order> getItemOrderByDateOrder(
            @Param("cid")
                    Long cid,
            @Param("dateOrder")
                    Date dateOrder,
            @Param("deleteFlg")
                    Integer deleteFlg);

    @Query(value = "select count(*) from `order` as o where date_format(o.create_date,'%Y, %m') = date_format(now(),'%Y, %m') and " +
            "o.company_id = :cid and o.delete_flg = :deleteFlg ", nativeQuery = true)
    Integer getCount(
            @Param("cid")
                    Long cid,
            @Param("deleteFlg")
                    Integer deleteFlg);

    @Query(value = "select count(*) from `order` as o where date_format(o.create_date,'%Y-%m-%d') = date_format(:date,'%Y-%m-%d') and " +
            "o.company_id = :cid and o.delete_flg = :deleteFlg ", nativeQuery = true)
    Integer getCountByDate(
            @Param("cid")
                    Long cid,
            @Param("deleteFlg")
                    Integer deleteFlg,
            @Param("date")
                    Date date
            );

    List<Order> findAllByCompanyIdAndDeleteFlg(Long cid, Integer deleteFlg);

    @Query(value = "select count(case when month(e.create_date) = :month then 0 end) as t1  " +
            " from  `order` as e " +
            "where e.company_id = :cid and e.delete_flg = :deleteFlg", nativeQuery = true)
    Integer getOrderForMonth(@Param("cid") Long cid, Integer deleteFlg, Integer month);


    @Query(value = "select count(case when month(e.create_date) = month(current_date) then 0 end) as t1  " +
            " from  `order` as e " +
            "where e.company_id = :cid and e.delete_flg = :deleteFlg and e.create_by = :uid", nativeQuery = true)
    Integer getOrderByEmployee(@Param("cid") Long cid, Integer deleteFlg, String uid);



}
