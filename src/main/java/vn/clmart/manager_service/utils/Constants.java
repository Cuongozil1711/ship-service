package vn.clmart.manager_service.utils;

public class Constants {
    public static class DELETE_FLG {
        public static final Integer NON_DELETE = 1;
        public static final Integer DELETE = 0;
    }

    public enum MODEL_IMAGE{
        PRODUCT, TYPE_PRODUCT
    }

    public enum TYPE_ROLE {
        ADMIN, CUSTOMER
    }

    public enum TYPE_ORDER {
        INIT, // đã thêm vào giỏ hàng
        PROCESSING, // đã đặt hàng

        COMPLETE, // hoàn thành
        CANCELED // huỷ hỏ
    }

    public static final String MAIL_ON_SUCCESS_03 = "./template/fileBz003.vm";
}
