package vn.clmart.manager_service.untils;

public class Constants {
    public static class DELETE_FLG {
        public static final Integer NON_DELETE = 1;
        public static final Integer DELETE = 0;
    }

    public enum RECEIPT_WARE_HOUSE{
        INIT, PENDING, PROCESSING, APPROVED, REJECTED, CANCELED, COMPLETE,
        EXTEND
    }

    public enum EMPLOYEE_WARE_HOUSE{
        INIT, PENDING, PROCESSING, APPROVED, REJECTED, CANCELED, COMPLETE,
        EXTEND
    }

    public enum BILL_EMPLOYEE{
        INIT, PENDING, PROCESSING, APPROVED, REJECTED, CANCELED, COMPLETE,
        EXTEND
    }
}
