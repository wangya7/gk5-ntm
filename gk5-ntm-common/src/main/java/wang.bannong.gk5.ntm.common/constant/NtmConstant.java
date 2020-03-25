package wang.bannong.gk5.ntm.common.constant;

/**
 * Created by bn. on 2019/7/4 3:24 PM
 */
public interface NtmConstant {
    Byte   EFF_STATUS = 1;
    Byte   EXP_STATUS = 0;
    String PROJECT    = "NTM";
    String ROOT_PATH  = "/";

    String COMMA     = ",";
    String UNDERLINE = "_";

    String LOG_IN  = PROJECT + "=======>>> ";
    String LOG_OUT = PROJECT + "=======<<< ";

    String ORDER_BY_CREATE_TIME_DESC = " create_time desc ";
    String ORDER_BY_CREATE_TIME_ASC  = " create_time asc ";
    String ORDER_BY_MODIFY_TIME_DESC = " modify_time desc ";
    String ORDER_BY_MODIFY_TIME_ASC  = " modify_time asc ";
    String ORDER_BY_ID_DESC          = " id desc ";
    String ORDER_BY_ID_ASC           = " id asc ";
    String PAGE_NUM                  = "pageNum";
    String PAGE_SIZE                 = "pageSize";


    Integer SUCCESS_CODE = 1;

    String BASE_TEST_URL = "BASE_TEST_URL";

    String EXP_MSG = "服务开小差啦，稍后重试";
}
